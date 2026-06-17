package at.fhtw.energyproducer;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class EnergyMessage {

    private String type;
    private String association;
    private double kwh;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datetime;

    public EnergyMessage() {}

    public EnergyMessage(String type, String association,
                         double kwh, LocalDateTime datetime) {
        this.type = type;
        this.association = association;
        this.kwh = kwh;
        this.datetime = datetime;
    }

    public String getType()            { return type; }
    public String getAssociation()     { return association; }
    public double getKwh()             { return kwh; }
    public LocalDateTime getDatetime() { return datetime; }

    public void setType(String type)             { this.type = type; }
    public void setAssociation(String a)         { this.association = a; }
    public void setKwh(double kwh)               { this.kwh = kwh; }
    public void setDatetime(LocalDateTime dt)    { this.datetime = dt; }
}