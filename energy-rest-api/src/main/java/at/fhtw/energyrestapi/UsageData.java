package at.fhtw.energyrestapi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_data")
public class UsageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "community_produced")
    private double communityProduced;

    @Column(name = "community_used")
    private double communityUsed;

    @Column(name = "grid_used")
    private double gridUsed;

    public UsageData() {}

    public Long getId() { return id; }
    public LocalDateTime getHour() { return hour; }
    public double getCommunityProduced() { return communityProduced; }
    public double getCommunityUsed() { return communityUsed; }
    public double getGridUsed() { return gridUsed; }

    public void setId(Long id) { this.id = id; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public void setCommunityProduced(double v) { this.communityProduced = v; }
    public void setCommunityUsed(double v) { this.communityUsed = v; }
    public void setGridUsed(double v) { this.gridUsed = v; }
}