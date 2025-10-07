//package io.app.clisma_backend.config;
//
//import io.app.clisma_backend.domain.*;
//import io.app.clisma_backend.domain.enums.AlertStatus;
//import io.app.clisma_backend.domain.enums.VehicleType;
//import io.app.clisma_backend.repos.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.OffsetDateTime;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class DatabaseInitializer implements CommandLineRunner {
//    private final LocationRepository locationRepository;
//    private final EmissionRecordRepository emissionRecordRepository;
//    private final VehicleDetectionRepository vehicleDetectionRepository;
//    private final HotspotRepository hotspotRepository;
//    private final AlertRepository alertRepository;
//
//    @Override
//    public void run(String... args) {
//        if (locationRepository.count() == 0) {
//            // Create sample locations
//            List<Location> locations = Arrays.asList(
//                    new Location(null, "Downtown Station", "Main monitoring station in downtown", -34.6037, -58.3816,
//                            OffsetDateTime.now(), OffsetDateTime.now()),
//                    new Location(null, "Industrial Park", "Monitoring station in industrial zone", -34.6261, -58.4127,
//                            OffsetDateTime.now(), OffsetDateTime.now()),
//                    new Location(null, "Residential Area", "Monitoring station in residential district", -34.5735, -58.4173,
//                            OffsetDateTime.now(), OffsetDateTime.now())
//            );
//            locationRepository.saveAll(locations);
//
//            // Create sample vehicle detections
//            List<VehicleDetection> detections = Arrays.asList(
//                    new VehicleDetection(null, "ABC123", "vehicle1.jpg", VehicleType.CAR, null, new HashSet<>(),
//                            OffsetDateTime.now(), OffsetDateTime.now()),
//                    new VehicleDetection(null, "XYZ789", "vehicle2.jpg", VehicleType.TRUCK, null, new HashSet<>(),
//                            OffsetDateTime.now(), OffsetDateTime.now()),
//                    new VehicleDetection(null, "DEF456", "vehicle3.jpg", VehicleType.BUS, null, new HashSet<>(),
//                            OffsetDateTime.now(), OffsetDateTime.now())
//            );
//            vehicleDetectionRepository.saveAll(detections);
//
//            // Create sample emission records
//            List<EmissionRecord> emissions = Arrays.asList(
//                    new EmissionRecord(null, 2.5, 0.8, 15.0, 25.0, 150.0, locations.get(0), detections.get(0),
//                            OffsetDateTime.now(), OffsetDateTime.now()),
//                    new EmissionRecord(null, 3.1, 1.2, 18.0, 30.0, 180.0, locations.get(1), detections.get(1),
//                            OffsetDateTime.now(), OffsetDateTime.now()),
//                    new EmissionRecord(null, 1.8, 0.6, 12.0, 20.0, 130.0, locations.get(2), detections.get(2),
//                            OffsetDateTime.now(), OffsetDateTime.now())
//            );
//            emissionRecordRepository.saveAll(emissions);
//
//            // Update vehicle detections with emission records
//            for (int i = 0; i < detections.size(); i++) {
//                detections.get(i).setEmissionRecord(emissions.get(i));
//            }
//            vehicleDetectionRepository.saveAll(detections);
//
//            // Create sample hotspots
//            List<Hotspot> hotspots = Arrays.asList(
//                    new Hotspot(null, locations.get(0), 75.0, OffsetDateTime.now(), OffsetDateTime.now()),
//                    new Hotspot(null, locations.get(1), 85.0, OffsetDateTime.now(), OffsetDateTime.now()),
//                    new Hotspot(null, locations.get(2), 45.0, OffsetDateTime.now(), OffsetDateTime.now())
//            );
//            hotspotRepository.saveAll(hotspots);
//
//            // Create sample alerts
//            List<Alert> alerts = Arrays.asList(
//                    new Alert(null, "High CO", "CO levels exceeded threshold", "admin@clisma.io",
//                            AlertStatus.SENT, detections.get(0), OffsetDateTime.now(), OffsetDateTime.now()),
//                    new Alert(null, "NOx Warning", "NOx levels approaching critical", "supervisor@clisma.io",
//                            AlertStatus.PENDING, detections.get(1), OffsetDateTime.now(), OffsetDateTime.now()),
//                    new Alert(null, "PM2.5 Alert", "PM2.5 levels above normal", "operator@clisma.io",
//                            AlertStatus.SENT, detections.get(2), OffsetDateTime.now(), OffsetDateTime.now())
//            );
//            alertRepository.saveAll(alerts);
//        }
//    }
//}
//
