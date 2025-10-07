package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.EmissionRecord;
import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.events.BeforeDeleteVehicleDetection;
import io.app.clisma_backend.model.EmissionRecordDTO;
import io.app.clisma_backend.repos.EmissionRecordRepository;
import io.app.clisma_backend.repos.VehicleDetectionRepository;
import io.app.clisma_backend.util.NotFoundException;
import io.app.clisma_backend.util.ReferencedException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EmissionRecordService {

    private final EmissionRecordRepository emissionRecordRepository;
    private final VehicleDetectionRepository vehicleDetectionRepository;

    public EmissionRecordService(final EmissionRecordRepository emissionRecordRepository,
            final VehicleDetectionRepository vehicleDetectionRepository) {
        this.emissionRecordRepository = emissionRecordRepository;
        this.vehicleDetectionRepository = vehicleDetectionRepository;
    }

    public List<EmissionRecordDTO> findAll() {
        final List<EmissionRecord> emissionRecords = emissionRecordRepository.findAll(Sort.by("id"));
        return emissionRecords.stream()
                .map(emissionRecord -> mapToDTO(emissionRecord, new EmissionRecordDTO()))
                .toList();
    }

    // EmissionRecordService.java
    public Page<EmissionRecordDTO> search(Long id, Double coLevel, Double noxLevel, Double pm25Level,
                                          Double pm10Level, Long locationId, Long vehicleDetectionId, OffsetDateTime start,
                                          OffsetDateTime end, Pageable pageable) {
        return emissionRecordRepository.search(id, coLevel, noxLevel, pm25Level, pm10Level,
                        locationId, vehicleDetectionId, start, end, pageable)
                .map(emissionRecord -> mapToDTO(emissionRecord, new EmissionRecordDTO()));

    }


    public EmissionRecordDTO get(final Long id) {
        return emissionRecordRepository.findById(id)
                .map(emissionRecord -> mapToDTO(emissionRecord, new EmissionRecordDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EmissionRecordDTO emissionRecordDTO) {
        final EmissionRecord emissionRecord = new EmissionRecord();
        mapToEntity(emissionRecordDTO, emissionRecord);
        return emissionRecordRepository.save(emissionRecord).getId();
    }

    public void update(final Long id, final EmissionRecordDTO emissionRecordDTO) {
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
            double coSum = 0, noxSum = 0, pm25Sum = 0, pm10Sum = 0, co2Sum = 0;
            for (EmissionRecord r : intervalRecords) {
                coSum += r.getCoLevel() != null ? r.getCoLevel() : 0;
                noxSum += r.getNoxLevel() != null ? r.getNoxLevel() : 0;
                pm25Sum += r.getPm25Level() != null ? r.getPm25Level() : 0;
                pm10Sum += r.getPm10Level() != null ? r.getPm10Level() : 0;
                co2Sum += r.getCo2Level() != null ? r.getCo2Level() : 0;
            }
            Map<String, Double> averages = Map.of(
                    "coLevel", coSum / count,
                    "noxLevel", noxSum / count,
                    "pm25Level", pm25Sum / count,
                    "pm10Level", pm10Sum / count,
                    "co2Level", co2Sum / count
            );
            intervalAverages.put(entry.getKey(), averages);
        }
        return intervalAverages;
    }


    public Map<String, Double> calculateAverageEmissionRates() {
        List<EmissionRecord> records = emissionRecordRepository.findAll();
        int count = records.size();
        if (count == 0) {
            return Map.of(
                    "coLevel", 0.0,
                    "noxLevel", 0.0,
                    "pm25Level", 0.0,
                    "pm10Level", 0.0,
                    "co2Level", 0.0
            );
        }
        double coSum = 0, noxSum = 0, pm25Sum = 0, pm10Sum = 0, co2Sum = 0;
        for (EmissionRecord record : records) {
            coSum += record.getCoLevel() != null ? record.getCoLevel() : 0;
            noxSum += record.getNoxLevel() != null ? record.getNoxLevel() : 0;
            pm25Sum += record.getPm25Level() != null ? record.getPm25Level() : 0;
            pm10Sum += record.getPm10Level() != null ? record.getPm10Level() : 0;
            co2Sum += record.getCo2Level() != null ? record.getCo2Level() : 0;
        }
        return Map.of(
                "coLevel", coSum / count,
                "noxLevel", noxSum / count,
                "pm25Level", pm25Sum / count,
                "pm10Level", pm10Sum / count,
                "co2Level", co2Sum / count
        );
    }

    private EmissionRecordDTO mapToDTO(final EmissionRecord emissionRecord,
            final EmissionRecordDTO emissionRecordDTO) {
        emissionRecordDTO.setId(emissionRecord.getId());
        emissionRecordDTO.setCoLevel(emissionRecord.getCoLevel());
        emissionRecordDTO.setNoxLevel(emissionRecord.getNoxLevel());
        emissionRecordDTO.setPm25Level(emissionRecord.getPm25Level());
        emissionRecordDTO.setPm10Level(emissionRecord.getPm10Level());
        emissionRecordDTO.setCo2Level(emissionRecord.getCo2Level());
        emissionRecordDTO.setLocation(emissionRecord.getLocation());
        emissionRecordDTO.setVehicleDetectionId(emissionRecord.getVehicleDetectionId() == null ? null : emissionRecord.getVehicleDetectionId().getId());
        return emissionRecordDTO;
    }

    private EmissionRecord mapToEntity(final EmissionRecordDTO emissionRecordDTO,
            final EmissionRecord emissionRecord) {
        emissionRecord.setCoLevel(emissionRecordDTO.getCoLevel());
        emissionRecord.setNoxLevel(emissionRecordDTO.getNoxLevel());
        emissionRecord.setPm25Level(emissionRecordDTO.getPm25Level());
        emissionRecord.setPm10Level(emissionRecordDTO.getPm10Level());
        emissionRecord.setCo2Level(emissionRecordDTO.getCo2Level());
        emissionRecord.setLocation(emissionRecordDTO.getLocation());
        final VehicleDetection vehicleDetectionId = emissionRecordDTO.getVehicleDetectionId() == null ? null : vehicleDetectionRepository.findById(emissionRecordDTO.getVehicleDetectionId())
                .orElseThrow(() -> new NotFoundException("vehicleDetectionId not found"));
        emissionRecord.setVehicleDetectionId(vehicleDetectionId);
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
