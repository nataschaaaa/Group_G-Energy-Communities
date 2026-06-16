package at.fhtw.energyjavafxgui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // Current Status
    @FXML private Label lblCommunityPool;
    @FXML private Label lblGridPortion;
    @FXML private Label lblStatus;

    // Date pickers
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;

    // TableView
    @FXML private TableView<UsageDataRow> historicalTable;
    @FXML private TableColumn<UsageDataRow, String> colHour;
    @FXML private TableColumn<UsageDataRow, Double> colProduced;
    @FXML private TableColumn<UsageDataRow, Double> colUsed;
    @FXML private TableColumn<UsageDataRow, Double> colGrid;

    @FXML
    public void initialize() {
        // Spalten mit Getter-Namen der UsageDataRow verknüpfen
        colHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
        colProduced.setCellValueFactory(new PropertyValueFactory<>("communityProduced"));
        colUsed.setCellValueFactory(new PropertyValueFactory<>("communityUsed"));
        colGrid.setCellValueFactory(new PropertyValueFactory<>("gridUsed"));

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

                if (response.statusCode() == 404) {
                    Platform.runLater(() -> {
                        lblCommunityPool.setText("Noch keine Daten");
                        lblGridPortion.setText("Noch keine Daten");
                        lblStatus.setText("ℹ DB ist leer");
                    });
                    return null;
                }

                JsonNode node = objectMapper.readTree(response.body());
                double depleted    = node.get("communityDepleted").asDouble();
                double gridPortion = node.get("gridPortion").asDouble();

                Platform.runLater(() -> {
                    lblCommunityPool.setText(String.format("%.2f%%", depleted));
                    lblGridPortion.setText(String.format("%.2f%%", gridPortion));
                    lblStatus.setText("✓ Aktualisiert");
                });
                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() ->
                lblStatus.setText("✗ Fehler: " + task.getException().getMessage())));

        new Thread(task).start();
    }

    @FXML
    public void onShowDataClick() {
        LocalDate start = dpStart.getValue();
        LocalDate end   = dpEnd.getValue();

        if (start == null || end == null) {
            lblStatus.setText("Bitte Start- und Enddatum wählen.");
            return;
        }
        if (start.isAfter(end)) {
            lblStatus.setText("Startdatum muss vor dem Enddatum liegen.");
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
                ObservableList<UsageDataRow> rows = FXCollections.observableArrayList();

                for (JsonNode entry : array) {
                    rows.add(new UsageDataRow(
                            entry.get("hour").asText(),
                            entry.get("communityProduced").asDouble(),
                            entry.get("communityUsed").asDouble(),
                            entry.get("gridUsed").asDouble()
                    ));
                }

                final int count = rows.size();
                Platform.runLater(() -> {
                    historicalTable.setItems(rows);
                    lblStatus.setText("✓ " + count + " Einträge geladen");
                });
                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() ->
                lblStatus.setText("✗ Fehler: " + task.getException().getMessage())));

        new Thread(task).start();
    }
}