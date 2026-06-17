package at.fhtw.usageservice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyMessage {

    private String type;
    private String association;
    private double kwh;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datetime;

    public EnergyMessage() {}

    public String getType()            { return type; }
    public String getAssociation()     { return association; }
    public double getKwh()             { return kwh; }
    public LocalDateTime getDatetime() { return datetime; }

    public void setType(String t)            { this.type = t; }
    public void setAssociation(String a)     { this.association = a; }
    public void setKwh(double kwh)           { this.kwh = kwh; }
    public void setDatetime(LocalDateTime d) { this.datetime = d; }
}