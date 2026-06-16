package at.fhtw.energyrestapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class EnergyController {

    @Autowired
    private CurrentPercentageRepository percentageRepository;

    @Autowired
    private UsageDataRepository usageDataRepository;

    @GetMapping("/energy/current")
    public ResponseEntity<CurrentPercentage> getCurrent() {
        CurrentPercentage current = percentageRepository.findTopByOrderByHourDesc();
        if (current == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(current, HttpStatus.OK);
    }

    @GetMapping("/energy/historical")
    public ResponseEntity<List<UsageData>> getHistorical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<UsageData> data = usageDataRepository.findByHourBetweenOrderByHourAsc(start, end);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}