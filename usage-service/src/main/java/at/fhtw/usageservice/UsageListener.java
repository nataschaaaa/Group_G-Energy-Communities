package at.fhtw.usageservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsageListener {

    private final UsageDataRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public UsageListener(UsageDataRepository repository,
                         RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.ENERGY_QUEUE)
    @Transactional
    public void handleMessage(EnergyMessage message) {
        if (message == null || message.getDatetime() == null) return;

        // Zeitstempel auf volle Stunde runden (14:33:00 → 14:00:00)
        LocalDateTime hour = message.getDatetime()
                .withMinute(0).withSecond(0).withNano(0);

        // Existierenden Eintrag laden, oder neuen anlegen
        UsageData row = repository.findByHour(hour)
                .orElse(new UsageData(hour));

        if ("PRODUCER".equals(message.getType())) {
            row.setCommunityProduced(row.getCommunityProduced() + message.getKwh());
            System.out.printf("[UsageService] PRODUCER +%.4f kWh | hour=%s%n",
                    message.getKwh(), hour);

        } else if ("USER".equals(message.getType())) {
            double demanded  = message.getKwh();
            double available = row.getCommunityProduced() - row.getCommunityUsed();

            if (available >= demanded) {
                // Alles aus Community
                row.setCommunityUsed(row.getCommunityUsed() + demanded);
            } else {
                // Community erschöpft → Rest vom Grid
                double fromCommunity = Math.max(available, 0.0);
                double fromGrid      = demanded - fromCommunity;
                row.setCommunityUsed(row.getCommunityUsed() + fromCommunity);
                row.setGridUsed(row.getGridUsed() + fromGrid);
            }
            System.out.printf("[UsageService] USER %.4f kWh | community=%.4f grid=%.4f%n",
                    demanded, row.getCommunityUsed(), row.getGridUsed());

        } else {
            return; // andere Typen ignorieren
        }

        repository.save(row);

        // Natascha's Service benachrichtigen
        rabbitTemplate.convertAndSend(RabbitMQConfig.UPDATE_QUEUE, new UpdateMessage(hour));
        System.out.println("[UsageService] UPDATE gesendet für hour: " + hour);
    }
}
