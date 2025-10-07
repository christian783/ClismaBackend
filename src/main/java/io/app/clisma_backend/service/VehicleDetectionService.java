package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.domain.enums.VehicleType;
import io.app.clisma_backend.events.BeforeDeleteVehicleDetection;
import io.app.clisma_backend.model.VehicleDetectionDTO;
import io.app.clisma_backend.repos.VehicleDetectionRepository;
import io.app.clisma_backend.util.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class VehicleDetectionService {

    private final VehicleDetectionRepository vehicleDetectionRepository;
    private final ApplicationEventPublisher publisher;

    public VehicleDetectionService(final VehicleDetectionRepository vehicleDetectionRepository,
            final ApplicationEventPublisher publisher) {
        this.vehicleDetectionRepository = vehicleDetectionRepository;
        this.publisher = publisher;
    }

    // VehicleDetectionService.java
    public Page<VehicleDetectionDTO> search(Long id, String licensePlate, String imageUrl,
                                            VehicleType vehicleType,Long emissionRecordId,OffsetDateTime start, OffsetDateTime end, Pageable pageable) {
        return vehicleDetectionRepository.search(id, licensePlate, imageUrl, vehicleType,emissionRecordId, start, end, pageable)
                .map(vehicleDetection -> mapToDTO(vehicleDetection, new VehicleDetectionDTO()));
    }

//    public List<VehicleDetectionDTO> findAll() {
//        final List<VehicleDetection> vehicleDetections = vehicleDetectionRepository.findAll(Sort.by("id"));
//        return vehicleDetections.stream()
//                .map(vehicleDetection -> mapToDTO(vehicleDetection, new VehicleDetectionDTO()))
//                .toList();
//    }

    public VehicleDetectionDTO get(final Long id) {
        return vehicleDetectionRepository.findById(id)
                .map(vehicleDetection -> mapToDTO(vehicleDetection, new VehicleDetectionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VehicleDetectionDTO vehicleDetectionDTO) {
        final VehicleDetection vehicleDetection = new VehicleDetection();
        mapToEntity(vehicleDetectionDTO, vehicleDetection);
        return vehicleDetectionRepository.save(vehicleDetection).getId();
    }

    public void update(final Long id, final VehicleDetectionDTO vehicleDetectionDTO) {
        final VehicleDetection vehicleDetection = vehicleDetectionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(vehicleDetectionDTO, vehicleDetection);
        vehicleDetectionRepository.save(vehicleDetection);
    }

    public int getTotalNumberOfDetections(OffsetDateTime start, OffsetDateTime end) {
        return (int) vehicleDetectionRepository.findByDateCreatedBetween(start, end).stream().count();
    }

    public void delete(final Long id) {
        final VehicleDetection vehicleDetection = vehicleDetectionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteVehicleDetection(id));
        vehicleDetectionRepository.delete(vehicleDetection);
    }

    private VehicleDetectionDTO mapToDTO(final VehicleDetection vehicleDetection,
            final VehicleDetectionDTO vehicleDetectionDTO) {
        vehicleDetectionDTO.setId(vehicleDetection.getId());
        vehicleDetectionDTO.setLicensePlate(vehicleDetection.getLicensePlate());
        vehicleDetectionDTO.setImageUrl(vehicleDetection.getImageUrl());
        return vehicleDetectionDTO;
    }

    private VehicleDetection mapToEntity(final VehicleDetectionDTO vehicleDetectionDTO,
            final VehicleDetection vehicleDetection) {
        vehicleDetection.setLicensePlate(vehicleDetectionDTO.getLicensePlate());
        vehicleDetection.setImageUrl(vehicleDetectionDTO.getImageUrl());
        return vehicleDetection;
    }

}
