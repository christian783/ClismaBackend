package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.Alert;
import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.events.BeforeDeleteVehicleDetection;
import io.app.clisma_backend.model.AlertDTO;
import io.app.clisma_backend.repos.AlertRepository;
import io.app.clisma_backend.repos.VehicleDetectionRepository;
import io.app.clisma_backend.util.NotFoundException;
import io.app.clisma_backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final VehicleDetectionRepository vehicleDetectionRepository;

    public AlertService(final AlertRepository alertRepository,
            final VehicleDetectionRepository vehicleDetectionRepository) {
        this.alertRepository = alertRepository;
        this.vehicleDetectionRepository = vehicleDetectionRepository;
    }

    public List<AlertDTO> findAll() {
        final List<Alert> alerts = alertRepository.findAll(Sort.by("id"));
        return alerts.stream()
                .map(alert -> mapToDTO(alert, new AlertDTO()))
                .toList();
    }

    public AlertDTO get(final Long id) {
        return alertRepository.findById(id)
                .map(alert -> mapToDTO(alert, new AlertDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AlertDTO alertDTO) {
        final Alert alert = new Alert();
        mapToEntity(alertDTO, alert);
        return alertRepository.save(alert).getId();
    }

    public void update(final Long id, final AlertDTO alertDTO) {
        final Alert alert = alertRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(alertDTO, alert);
        alertRepository.save(alert);
    }

    public void delete(final Long id) {
        final Alert alert = alertRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        alertRepository.delete(alert);
    }

    private AlertDTO mapToDTO(final Alert alert, final AlertDTO alertDTO) {
        alertDTO.setId(alert.getId());
        alertDTO.setType(alert.getType());
        alertDTO.setMessage(alert.getMessage());
        alertDTO.setSentTo(alert.getSentTo());
        alertDTO.setStatus(alert.getStatus());
        alertDTO.setVehicleDetectionId(alert.getVehicleDetectionId() == null ? null : alert.getVehicleDetectionId().getId());
        return alertDTO;
    }

    private Alert mapToEntity(final AlertDTO alertDTO, final Alert alert) {
        alert.setType(alertDTO.getType());
        alert.setMessage(alertDTO.getMessage());
        alert.setSentTo(alertDTO.getSentTo());
        alert.setStatus(alertDTO.getStatus());
        final VehicleDetection vehicleDetectionId = alertDTO.getVehicleDetectionId() == null ? null : vehicleDetectionRepository.findById(alertDTO.getVehicleDetectionId())
                .orElseThrow(() -> new NotFoundException("vehicleDetectionId not found"));
        alert.setVehicleDetectionId(vehicleDetectionId);
        return alert;
    }

    @EventListener(BeforeDeleteVehicleDetection.class)
    public void on(final BeforeDeleteVehicleDetection event) {
        final ReferencedException referencedException = new ReferencedException();
        final Alert vehicleDetectionIdAlert = alertRepository.findFirstByVehicleDetectionIdId(event.getId());
        if (vehicleDetectionIdAlert != null) {
            referencedException.setKey("vehicleDetection.alert.vehicleDetectionId.referenced");
            referencedException.addParam(vehicleDetectionIdAlert.getId());
            throw referencedException;
        }
    }

}
