package at.fhtw.energyjavafxgui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainController {

    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML private Label lblCommunityPool;
    @FXML private Label lblGridPortion;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private Label lblProduced;
    @FXML private Label lblUsed;
    @FXML private Label lblGrid;
    @FXML private Label lblStatus;

    @FXML
    public void initialize() {
        dpStart.setValue(LocalDate.of(2025, 1, 9));
        dpEnd.setValue(LocalDate.of(2025, 1, 10));
    }

    @FXML
    public void onRefreshClick() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/energy/current"))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode node = objectMapper.readTree(response.body());
                double depleted    = node.get("communityDepleted").asDouble();
                double gridPortion = node.get("gridPortion").asDouble();

                Platform.runLater(() -> {
                    lblCommunityPool.setText(String.format("Community Pool     %.2f%% used", depleted));
                    lblGridPortion.setText(String.format("Grid Portion     %.2f%%", gridPortion));
                    lblStatus.setText("✓ Refreshed successfully");
                });
                return null;
            }
        };

        task.setOnFailed(e ->
                Platform.runLater(() ->
                        lblStatus.setText("✗ Error: " + task.getException().getMessage())));

        new Thread(task).start();
    }

    @FXML
    public void onShowDataClick() {
        LocalDate start = dpStart.getValue();
        LocalDate end   = dpEnd.getValue();

        if (start == null || end == null) {
            lblStatus.setText("Please select both dates.");
            return;
        }

        String startEncoded = URLEncoder.encode(
                start.atStartOfDay().format(ISO_FMT), StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(
                end.atTime(23, 59, 59).format(ISO_FMT), StandardCharsets.UTF_8);

        String url = BASE_URL + "/energy/historical?start=" + startEncoded + "&end=" + endEncoded;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode array = objectMapper.readTree(response.body());
                double totalProduced = 0, totalUsed = 0, totalGrid = 0;

                for (JsonNode entry : array) {
                    totalProduced += entry.get("communityProduced").asDouble();
                    totalUsed     += entry.get("communityUsed").asDouble();
                    totalGrid     += entry.get("gridUsed").asDouble();
                }

                final double fp = totalProduced, fu = totalUsed, fg = totalGrid;
                final int count = array.size();

                Platform.runLater(() -> {
                    lblProduced.setText(String.format("Community produced     %.3f kWh", fp));
                    lblUsed.setText(String.format("Community used           %.3f kWh", fu));
                    lblGrid.setText(String.format("Grid used                    %.3f kWh", fg));
                    lblStatus.setText("✓ Loaded " + count + " hour(s)");
                });
                return null;
            }
        };

        task.setOnFailed(e ->
                Platform.runLater(() ->
                        lblStatus.setText("✗ Error: " + task.getException().getMessage())));

        new Thread(task).start();
    }
}