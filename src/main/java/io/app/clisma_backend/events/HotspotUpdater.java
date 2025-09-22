package io.app.clisma_backend.events;

import io.app.clisma_backend.domain.Hotspot;
import io.app.clisma_backend.repos.HotspotRepository;
import io.app.clisma_backend.service.PollutionCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HotspotUpdater {

    private final PollutionCalculatorService calculator;

    private final HotspotRepository hotspotRepo;

    @Scheduled(fixedRate = 3600000) // Hourly
    public void updateAllHotspots() {
        List<Hotspot> hotspots = hotspotRepo.findAll(); // Or by active locations
        hotspots.forEach((hotspot) -> {
            calculator.updateHotspot(hotspot, false);
        });
        hotspotRepo.saveAll(hotspots);
    }
}
