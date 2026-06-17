package at.fhtw.energyproducer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gibt Solar-Faktor 0.0–1.0 zurück.
     * Basiert auf Open-Meteo weathercode:
     *   0 = klarer Himmel → 1.0
     *   1 = hauptsächlich klar → 0.8
     *   2 = teilweise bewölkt → 0.5
     *   3 = bedeckt → 0.3
     *   >3 = Regen/Gewitter → 0.1
     */
    public double getSolarFactor() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(weatherApiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            int weatherCode = root.path("current_weather")
                    .path("weathercode").asInt(3);

            if (weatherCode == 0) return 1.0;
            if (weatherCode <= 1) return 0.8;
            if (weatherCode <= 2) return 0.5;
            if (weatherCode <= 3) return 0.3;
            return 0.1;

        } catch (Exception e) {
            System.out.println("[WeatherService] API nicht erreichbar, Fallback 0.5: " + e.getMessage());
            return 0.5;
        }
    }
}