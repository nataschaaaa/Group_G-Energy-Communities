package at.fhtw.usageservice;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class UpdateMessage {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime hour;

    public UpdateMessage() {}

    public UpdateMessage(LocalDateTime hour) {
        this.hour = hour;
    }

    public LocalDateTime getHour()          { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
}