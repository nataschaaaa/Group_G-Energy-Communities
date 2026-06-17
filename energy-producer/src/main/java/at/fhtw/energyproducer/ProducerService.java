package at.fhtw.energyproducer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final WeatherService weatherService;
    private final Random random = new Random();

    public ProducerService(RabbitTemplate rabbitTemplate,
                           WeatherService weatherService) {
        this.rabbitTemplate = rabbitTemplate;
        this.weatherService = weatherService;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    public void sendProductionMessage() throws InterruptedException {
        // Zufällige Pause 0–4 Sekunden → unregelmäßige, realistische Intervalle
        Thread.sleep(random.nextInt(4000));

        double solarFactor = weatherService.getSolarFactor();

        // Plausible kWh pro Intervall: 0.001–0.010 kWh, skaliert mit Sonnenschein
        double kwh = (0.001 + random.nextDouble() * 0.009) * solarFactor;
        kwh = Math.round(kwh * 10000.0) / 10000.0;

        EnergyMessage message = new EnergyMessage(
                "PRODUCER",        // type – Lea's Service erwartet genau diesen String
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.ENERGY_QUEUE, message);
        System.out.printf("[Producer] Gesendet: %.4f kWh | solarFactor=%.2f | %s%n",
                kwh, solarFactor, LocalDateTime.now());
    }
}