package at.fhtw.currentpercentage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PercentageListener {

    private final UsageDataRepository usageDataRepository;
    private final CurrentPercentageRepository percentageRepository;

    public PercentageListener(UsageDataRepository usageDataRepository,
                              CurrentPercentageRepository percentageRepository) {
        this.usageDataRepository = usageDataRepository;
        this.percentageRepository = percentageRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.UPDATE_QUEUE)
    @Transactional
    public void handleUpdate(UpdateMessage message) {
        if (message == null || message.getHour() == null) return;

        UsageData usage = usageDataRepository.findByHour(message.getHour())
                .orElse(null);

        if (usage == null) {
            System.out.println("[PercentageService] Kein Eintrag für: " + message.getHour());
            return;
        }

        double produced = usage.getCommunityProduced();
        double used     = usage.getCommunityUsed();
        double grid     = usage.getGridUsed();
        double total    = used + grid;

        // Wie viel % der Produktion wurde verbraucht (max. 100%)
        double communityDepleted = (produced > 0)
                ? Math.min((used / produced) * 100.0, 100.0)
                : 0.0;

        // Grid-Anteil an der gesamten verbrauchten Energie
        double gridPortion = (total > 0)
                ? (grid / total) * 100.0
                : 0.0;

        // Auf 2 Nachkommastellen runden
        communityDepleted = Math.round(communityDepleted * 100.0) / 100.0;
        gridPortion       = Math.round(gridPortion * 100.0) / 100.0;

        // Upsert: vorhandenen Eintrag aktualisieren oder neuen erstellen
        CurrentPercentage cp = percentageRepository
                .findByHour(message.getHour())
                .orElse(new CurrentPercentage());

        cp.setHour(message.getHour());
        cp.setCommunityDepleted(communityDepleted);
        cp.setGridPortion(gridPortion);
        percentageRepository.save(cp);

        System.out.printf("[PercentageService] hour=%s | depleted=%.2f%% | grid=%.2f%%%n",
                message.getHour(), communityDepleted, gridPortion);
    }
}