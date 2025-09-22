package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.EmissionRecord;
import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.events.BeforeDeleteVehicleDetection;
import io.app.clisma_backend.model.EmissionRecordDTO;
import io.app.clisma_backend.repos.EmissionRecordRepository;
import io.app.clisma_backend.repos.VehicleDetectionRepository;
import io.app.clisma_backend.util.NotFoundException;
import io.app.clisma_backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
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
