package at.fhtw.energyrestapi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "community_depleted")
    private double communityDepleted;

    @Column(name = "grid_portion")
    private double gridPortion;

    public CurrentPercentage() {}

    public Long getId() { return id; }
    public LocalDateTime getHour() { return hour; }
    public double getCommunityDepleted() { return communityDepleted; }
    public double getGridPortion() { return gridPortion; }

    public void setId(Long id) { this.id = id; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public void setCommunityDepleted(double v) { this.communityDepleted = v; }
    public void setGridPortion(double v) { this.gridPortion = v; }
}