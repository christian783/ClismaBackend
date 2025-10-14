package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.EmissionRecord;
import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.domain.enums.AlertStatus;
import io.app.clisma_backend.domain.enums.VehicleType;
import io.app.clisma_backend.events.BeforeDeleteVehicleDetection;
import io.app.clisma_backend.model.*;
import io.app.clisma_backend.repos.EmissionRecordRepository;
import io.app.clisma_backend.repos.LocationRepository;
import io.app.clisma_backend.repos.UserRepository;
import io.app.clisma_backend.repos.VehicleDetectionRepository;
import io.app.clisma_backend.util.NotFoundException;
import io.app.clisma_backend.util.ReferencedException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
@EnableScheduling
public class EmissionRecordService {

    private final EmissionRecordRepository emissionRecordRepository;
    private final VehicleDetectionRepository vehicleDetectionRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final AlertService alertService;
    private final EmailService emailService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://10.12.73.154:5000/api/sensors";

    @Value("${emission.threshold.aqi}")
    private double aqiThreshold;

    @Value("${emission.threshold.coppm}")
    private double coPpmThreshold;

    @Value("${emission.alert.email}")
    private String alertEmail;

    public EmissionRecordService(final EmissionRecordRepository emissionRecordRepository,
                                 final VehicleDetectionRepository vehicleDetectionRepository, LocationRepository locationRepository,
                                 EmailService emailService, UserRepository userRepository, AlertService alertService) {
        this.alertService = alertService;
        this.emissionRecordRepository = emissionRecordRepository;
        this.vehicleDetectionRepository = vehicleDetectionRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // EmissionRecordService.java

    @Transactional(readOnly = true)
    public Page<EmissionRecordResponse> search(Long id, Double aqi, Double coPpm,
            Integer mq135, Double mq135R, Integer mq7, Double mq7R,
            Long locationId, Long vehicleDetectionId,
            OffsetDateTime start, OffsetDateTime end,
            Pageable pageable) {
        return emissionRecordRepository.search(id, aqi, coPpm, mq135, mq135R, mq7, mq7R,
                locationId, vehicleDetectionId, start, end, pageable)
                .map(record -> mapToResponseDTO(record, new EmissionRecordResponse()));
    }


    public EmissionRecordRequest get(final Long id) {
        return emissionRecordRepository.findById(id)
                .map(emissionRecord -> mapToDTO(emissionRecord, new EmissionRecordRequest()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EmissionRecordRequest emissionRecordDTO) {
        final EmissionRecord emissionRecord = new EmissionRecord();
        mapToEntity(emissionRecordDTO, emissionRecord);
        EmissionRecord savedRecord = emissionRecordRepository.save(emissionRecord);
        checkEmissionThresholds(savedRecord);
        return savedRecord.getId();
    }

    public void update(final Long id, final EmissionRecordRequest emissionRecordDTO) {
        final EmissionRecord emissionRecord = emissionRecordRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(emissionRecordDTO, emissionRecord);
        emissionRecordRepository.save(emissionRecord);
    }

    public void delete(final Long id) {
        final EmissionRecord emissionRecord = emissionRecordRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        emissionRecordRepository.delete(emissionRecord);
    }

    public Map<String, Map<String, Double>> calculateIntervalAverageEmissionRates(
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String interval) {

        List<EmissionRecord> records = emissionRecordRepository.findByDateCreatedBetween(startDate, endDate);
        Map<String, List<EmissionRecord>> recordsByInterval = new HashMap<>();

        for (EmissionRecord record : records) {
            String key = switch (interval.toLowerCase()) {
                case "year" -> String.valueOf(record.getDateCreated().getYear());
                case "month" -> record.getDateCreated().getYear() + "-" +
                        String.format("%02d", record.getDateCreated().getMonthValue());
                case "day" -> record.getDateCreated().getYear() + "-" +
                        String.format("%02d", record.getDateCreated().getMonthValue()) + "-" +
                        String.format("%02d", record.getDateCreated().getDayOfMonth());
                case "hour" -> record.getDateCreated().getYear() + "-" +
                        String.format("%02d", record.getDateCreated().getMonthValue()) + "-" +
                        String.format("%02d", record.getDateCreated().getDayOfMonth()) + " " +
                        String.format("%02d", record.getDateCreated().getHour());
                default -> "total";
            };
            recordsByInterval.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }

        Map<String, Map<String, Double>> intervalAverages = new HashMap<>();
        for (Map.Entry<String, List<EmissionRecord>> entry : recordsByInterval.entrySet()) {
            List<EmissionRecord> intervalRecords = entry.getValue();
            int count = intervalRecords.size();

            // Initialize sums for all sensor values
            double aqiSum = 0, coPpmSum = 0;
            int mq135Sum = 0;
            double mq135RSum = 0;
            int mq7Sum = 0;
            double mq7RSum = 0;

            for (EmissionRecord r : intervalRecords) {
                aqiSum += r.getAqi();
                coPpmSum += r.getCoPpm();
                mq135Sum += r.getMq135();
                mq135RSum += r.getMq135R();
                mq7Sum += r.getMq7();
                mq7RSum += r.getMq7R();
            }

            Map<String, Double> averages = new HashMap<>();
            averages.put("aqi", aqiSum / count);
            averages.put("coPpm", coPpmSum / count);
            averages.put("mq135", (double) mq135Sum / count);
            averages.put("mq135R", mq135RSum / count);
            averages.put("mq7", (double) mq7Sum / count);
            averages.put("mq7R", mq7RSum / count);

            intervalAverages.put(entry.getKey(), averages);
        }
        return intervalAverages;
    }


    public Map<String, Double> calculateAverageEmissionLevelsByVehicleType(VehicleType vehicleType) {
        List<EmissionRecord> records = vehicleDetectionRepository.search(null,null,null,vehicleType,null,null,null, Pageable.unpaged())
                .map(VehicleDetection::getEmissionRecord)
                .filter(Objects::nonNull)
                .toList();


        int count = records.size();
        if (count == 0) {
            return Map.of(
                "aqi", 0.0,
                "coPpm", 0.0,
                "mq135", 0.0,
                "mq135R", 0.0,
                "mq7", 0.0,
                "mq7R", 0.0
            );
        }

        double aqiSum = 0, coPpmSum = 0;
        int mq135Sum = 0;
        double mq135RSum = 0;
        int mq7Sum = 0;
        double mq7RSum = 0;

        for (EmissionRecord record : records) {
            aqiSum += record.getAqi();
            coPpmSum += record.getCoPpm();
            mq135Sum += record.getMq135();
            mq135RSum += record.getMq135R();
            mq7Sum += record.getMq7();
            mq7RSum += record.getMq7R();
        }

        Map<String, Double> averages = new HashMap<>();
        averages.put("aqi", aqiSum / count);
        averages.put("coPpm", coPpmSum / count);
        averages.put("mq135", (double) mq135Sum / count);
        averages.put("mq135R", mq135RSum / count);
        averages.put("mq7", (double) mq7Sum / count);
        averages.put("mq7R", mq7RSum / count);

        return averages;
    }

    private void checkEmissionThresholds(EmissionRecord record) {
        if (record.getVehicleDetectionId() != null &&
            (record.getAqi() > aqiThreshold || record.getCoPpm() > coPpmThreshold)) {

            AlertDTO alertDTO = AlertDTO.builder().type("Severe")
                    .sentTo(String.valueOf(record.getVehicleDetectionId().getVehicleOwner().getEmail()))
                    .message(String.format("High emissions detected for vehicle %s: AQI=%.2f, CO PPM=%.2f",
                            record.getVehicleDetectionId().getLicensePlate(),
                            record.getAqi(),
                            record.getCoPpm()))
                    .vehicleDetectionId(record.getVehicleDetectionId().getId())
                    .status(AlertStatus.SENT)
                    .build();

            alertService.create(alertDTO);
            String licensePlate = record.getVehicleDetectionId().getLicensePlate();
            emailService.sendEmissionAlert(
                    record.getVehicleDetectionId().getVehicleOwner().getEmail(),
                licensePlate,
                record.getAqi(),
                record.getCoPpm()
            );
        }
    }

    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void fetchDataPeriodically() {
        SensorResponse sensorResponse = restTemplate.getForObject(url, SensorResponse.class);
        io.app.clisma_backend.model.VehicleDetection vehicleDetection = sensorResponse.getVehicleDetection();
        if (vehicleDetection.isActive()) {
            Sensors sensors = sensorResponse.getSensors();
            MqSensor mqSensor = sensors.getMq();
            MqData mqData = mqSensor.getData();
            VehicleDetection vehicleDetection1 = new VehicleDetection();
            vehicleDetection1.setLicensePlate("ABC123");
            vehicleDetection1.setVehicleType(VehicleType.CAR);
            vehicleDetection1.setVehicleOwner(userRepository.findById(10496L).orElseThrow(NotFoundException::new));
            vehicleDetectionRepository.save(vehicleDetection1);

            EmissionRecordRequest emissionRecordRequest = new EmissionRecordRequest();
            emissionRecordRequest.setAqi(mqData.getAqi());
            emissionRecordRequest.setMq7R(mqData.getMq7R());
            emissionRecordRequest.setMq7(mqData.getMq7());
            emissionRecordRequest.setCoPpm(mqData.getCoPpm());
            emissionRecordRequest.setMq135(mqData.getMq135());
            emissionRecordRequest.setMq135R(mqData.getMq135R());
            emissionRecordRequest.setLocationId(10002L);
            emissionRecordRequest.setVehicleDetectionId(vehicleDetection1.getId());

            create(emissionRecordRequest); // This will now automatically check thresholds
        }

    }

    public Map<String, Double> calculateAverageEmissionRates() {
        List<EmissionRecord> records = emissionRecordRepository.findAll();
        int count = records.size();
        if (count == 0) {
            return Map.of(
                "aqi", 0.0,
                "coPpm", 0.0,
                "mq135", 0.0,
                "mq135R", 0.0,
                "mq7", 0.0,
                "mq7R", 0.0
            );
        }

        double aqiSum = 0, coPpmSum = 0;
        int mq135Sum = 0;
        double mq135RSum = 0;
        int mq7Sum = 0;
        double mq7RSum = 0;

        for (EmissionRecord record : records) {
            aqiSum += record.getAqi();
            coPpmSum += record.getCoPpm();
            mq135Sum += record.getMq135();
            mq135RSum += record.getMq135R();
            mq7Sum += record.getMq7();
            mq7RSum += record.getMq7R();
        }

        Map<String, Double> averages = new HashMap<>();
        averages.put("aqi", aqiSum / count);
        averages.put("coPpm", coPpmSum / count);
        averages.put("mq135", (double) mq135Sum / count);
        averages.put("mq135R", mq135RSum / count);
        averages.put("mq7", (double) mq7Sum / count);
        averages.put("mq7R", mq7RSum / count);

        return averages;
    }

        /**
         * Returns a list of the highest polluting vehicles based on their emission records.
         *
         * @param pollutantType The type of pollutant to sort by ("co", "nox", "pm25", "pm10", "co2", or "total")
         * @param limit The maximum number of vehicles to return
         * @return A list of vehicles with their emission metrics, sorted by highest pollution levels
         */
        @Transactional(readOnly = true)
        public List<Map<String, Object>> getHighestPollutingVehicles(String pollutantType, int limit) {
            List<EmissionRecord> allRecords = emissionRecordRepository.findAllWithVehicleDetection();
            Map<String, List<EmissionRecord>> recordsByVehicle = new HashMap<>();

            for (EmissionRecord record : allRecords) {
                if (record.getVehicleDetectionId() != null && record.getVehicleDetectionId().getLicensePlate() != null) {
                    String licensePlate = record.getVehicleDetectionId().getLicensePlate();
                    recordsByVehicle.computeIfAbsent(licensePlate, k -> new ArrayList<>()).add(record);
                }
            }

            List<Map<String, Object>> vehiclePollutionData = new ArrayList<>();

            for (Map.Entry<String, List<EmissionRecord>> entry : recordsByVehicle.entrySet()) {
                String licensePlate = entry.getKey();
                List<EmissionRecord> vehicleRecords = entry.getValue();

                if (vehicleRecords.isEmpty()) continue;

                VehicleDetection vehicle = vehicleRecords.get(0).getVehicleDetectionId();
                int recordCount = vehicleRecords.size();

                // Calculate totals for all metrics
                double aqiTotal = 0, coPpmTotal = 0;
                int mq135Total = 0;
                double mq135RTotal = 0;
                int mq7Total = 0;
                double mq7RTotal = 0;

                for (EmissionRecord record : vehicleRecords) {
                    aqiTotal += record.getAqi();
                    coPpmTotal += record.getCoPpm();
                    mq135Total += record.getMq135();
                    mq135RTotal += record.getMq135R();
                    mq7Total += record.getMq7();
                    mq7RTotal += record.getMq7R();
                }

                // Calculate averages
                double aqiAvg = aqiTotal / recordCount;
                double coPpmAvg = coPpmTotal / recordCount;
                double mq135Avg = (double) mq135Total / recordCount;
                double mq135RAvg = mq135RTotal / recordCount;
                double mq7Avg = (double) mq7Total / recordCount;
                double mq7RAvg = mq7RTotal / recordCount;

                // Calculate pollution score based on AQI and sensor readings
                double totalScore = (aqiAvg * 0.4) + // AQI weighted heavily
                              (coPpmAvg * 0.2) + // CO PPM also significant
                              (mq135Avg * 0.1) + // MQ135 sensor readings
                              (mq135RAvg * 0.1) + // MQ135R resistance readings
                              (mq7Avg * 0.1) + // MQ7 sensor readings
                              (mq7RAvg * 0.1); // MQ7R resistance readings

                Map<String, Object> vehicleData = new HashMap<>();
                vehicleData.put("licensePlate", licensePlate);
                vehicleData.put("vehicleType", vehicle.getVehicleType());
                vehicleData.put("recordCount", recordCount);
                vehicleData.put("aqi", aqiAvg);
                vehicleData.put("coPpm", coPpmAvg);
                vehicleData.put("mq135", mq135Avg);
                vehicleData.put("mq135R", mq135RAvg);
                vehicleData.put("mq7", mq7Avg);
                vehicleData.put("mq7R", mq7RAvg);
                vehicleData.put("totalScore", totalScore);

                vehiclePollutionData.add(vehicleData);
            }

            // Sort by the selected pollutant type
            Comparator<Map<String, Object>> comparator = switch (pollutantType.toLowerCase()) {
                case "aqi" -> Comparator.comparing(m -> (Double) m.get("aqi"), Comparator.reverseOrder());
                case "coppm" -> Comparator.comparing(m -> (Double) m.get("coPpm"), Comparator.reverseOrder());
                case "mq135" -> Comparator.comparing(m -> (Double) m.get("mq135"), Comparator.reverseOrder());
                case "mq135r" -> Comparator.comparing(m -> (Double) m.get("mq135R"), Comparator.reverseOrder());
                case "mq7" -> Comparator.comparing(m -> (Double) m.get("mq7"), Comparator.reverseOrder());
                case "mq7r" -> Comparator.comparing(m -> (Double) m.get("mq7R"), Comparator.reverseOrder());
                default -> Comparator.comparing(m -> (Double) m.get("totalScore"), Comparator.reverseOrder());
            };

            vehiclePollutionData.sort(comparator);
            return vehiclePollutionData.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
    }

    private EmissionRecordRequest mapToDTO(final EmissionRecord emissionRecord,
                                           final EmissionRecordRequest emissionRecordDTO) {
        emissionRecordDTO.setId(emissionRecord.getId());
        emissionRecordDTO.setAqi(emissionRecord.getAqi());
        emissionRecordDTO.setCoPpm(emissionRecord.getCoPpm());
        emissionRecordDTO.setMq135(emissionRecord.getMq135());
        emissionRecordDTO.setMq135R(emissionRecord.getMq135R());
        emissionRecordDTO.setMq7(emissionRecord.getMq7());
        emissionRecordDTO.setMq7R(emissionRecord.getMq7R());

        emissionRecordDTO.setLocationId(emissionRecord.getLocation() == null ? null : emissionRecord.getLocation().getId());
        emissionRecordDTO.setVehicleDetectionId(emissionRecord.getVehicleDetectionId() == null ? null : emissionRecord.getVehicleDetectionId().getId());
        return emissionRecordDTO;
    }

    private EmissionRecordResponse mapToResponseDTO(final EmissionRecord emissionRecord,
            final EmissionRecordResponse response) {
        response.setId(emissionRecord.getId());
        response.setAqi(emissionRecord.getAqi());
        response.setCoPpm(emissionRecord.getCoPpm());
        response.setMq135(emissionRecord.getMq135());
        response.setMq135R(emissionRecord.getMq135R());
        response.setMq7(emissionRecord.getMq7());
        response.setMq7R(emissionRecord.getMq7R());

        response.setLocationId(emissionRecord.getLocation() == null ? null :
                emissionRecord.getLocation().getId());

        if (emissionRecord.getVehicleDetectionId() != null) {
            VehicleDetection vehicleDetection = emissionRecord.getVehicleDetectionId();
            VehicleDetectionDTO vehicleDetectionDTO = new VehicleDetectionDTO();
            vehicleDetectionDTO.setId(vehicleDetection.getId());
            vehicleDetectionDTO.setLicensePlate(vehicleDetection.getLicensePlate());
            vehicleDetectionDTO.setVehicleType(vehicleDetection.getVehicleType());
            // Add any other fields you want to include in the response
            response.setVehicleDetection(vehicleDetectionDTO);
        }

        return response;
    }

    private EmissionRecord mapToEntity(final EmissionRecordRequest emissionRecordDTO,
            final EmissionRecord emissionRecord) {
        emissionRecord.setAqi(emissionRecordDTO.getAqi());
        emissionRecord.setCoPpm(emissionRecordDTO.getCoPpm());
        emissionRecord.setMq135(emissionRecordDTO.getMq135());
        emissionRecord.setMq135R(emissionRecordDTO.getMq135R());
        emissionRecord.setMq7(emissionRecordDTO.getMq7());
        emissionRecord.setMq7R(emissionRecordDTO.getMq7R());

        emissionRecord.setLocation(locationRepository.findById(emissionRecordDTO.getLocationId())
                .orElseThrow(() -> new NotFoundException("Location not found")));

        if (emissionRecordDTO.getVehicleDetectionId() != null) {
            final VehicleDetection vehicleDetection = vehicleDetectionRepository.findById(emissionRecordDTO.getVehicleDetectionId())
                    .orElseThrow(() -> new NotFoundException("VehicleDetection not found"));
            emissionRecord.setVehicleDetectionId(vehicleDetection);
        }

        return emissionRecord;
    }

    public boolean vehicleDetectionIdExists(final Long id) {
        return emissionRecordRepository.existsByVehicleDetectionIdId(id);
    }

    @EventListener(BeforeDeleteVehicleDetection.class)
    public void on(final BeforeDeleteVehicleDetection event) {
        final ReferencedException referencedException = new ReferencedException();
        final EmissionRecord vehicleDetectionIdEmissionRecord = emissionRecordRepository.findFirstByVehicleDetectionIdId(event.getId());
        if (vehicleDetectionIdEmissionRecord != null) {
            referencedException.setKey("vehicleDetection.emissionRecord.vehicleDetectionId.referenced");
            referencedException.addParam(vehicleDetectionIdEmissionRecord.getId());
            throw referencedException;
        }
    }

}
