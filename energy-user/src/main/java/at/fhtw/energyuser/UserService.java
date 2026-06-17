package at.fhtw.energyuser;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public UserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    public void sendUsageMessage() throws InterruptedException {
        Thread.sleep(random.nextInt(4000));

        int hour = LocalDateTime.now().getHour();
        double timeFactor = getTimeOfDayFactor(hour);

        // Plausible kWh pro Intervall: 0.001–0.008, skaliert mit Tageszeit
        double kwh = (0.001 + random.nextDouble() * 0.007) * timeFactor;
        kwh = Math.round(kwh * 10000.0) / 10000.0;

        EnergyMessage message = new EnergyMessage(
                "USER",            // type – Lea's Service erwartet genau diesen String
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.ENERGY_QUEUE, message);
        System.out.printf("[User] Gesendet: %.4f kWh | hour=%d timeFactor=%.2f%n",
                kwh, hour, timeFactor);
    }

    /**
     * Tageszeit-abhängiger Verbrauchsfaktor.
     * Peak morgens (7–9 Uhr) und abends (17–20 Uhr).
     */
    private double getTimeOfDayFactor(int hour) {
        if (hour >= 7  && hour <= 9)  return 1.0;   // Morgen-Peak
        if (hour >= 17 && hour <= 20) return 0.9;   // Abend-Peak
        if (hour >= 10 && hour <= 16) return 0.5;   // Tagesbetrieb
        if (hour >= 21 && hour <= 23) return 0.4;   // Ruhiger Abend
        return 0.2;                                   // Nacht (0–6 Uhr)
    }
}