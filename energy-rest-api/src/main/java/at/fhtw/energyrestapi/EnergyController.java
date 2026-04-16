package at.fhtw.energyrestapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class EnergyController {

    private final CurrentPercentageRepository percentageRepository;
    private final UsageDataRepository usageDataRepository;

    @Autowired
    public EnergyController(CurrentPercentageRepository percentageRepository,
                            UsageDataRepository usageDataRepository) {
        this.percentageRepository = percentageRepository;
        this.usageDataRepository  = usageDataRepository;
    }

    @GetMapping("/energy/current")
    public CurrentPercentage getCurrent() {
        return percentageRepository.findCurrent();
    }

    @GetMapping("/energy/historical")
    public List<UsageData> getHistorical(@RequestParam String start,
                                         @RequestParam String end) {
        return usageDataRepository.findByDateRange(start, end);
    }
}