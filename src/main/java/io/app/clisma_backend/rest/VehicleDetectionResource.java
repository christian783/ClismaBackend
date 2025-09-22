package io.app.clisma_backend.rest;

import io.app.clisma_backend.model.VehicleDetectionDTO;
import io.app.clisma_backend.service.VehicleDetectionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/vehicleDetections", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleDetectionResource {

    private final VehicleDetectionService vehicleDetectionService;

    public VehicleDetectionResource(final VehicleDetectionService vehicleDetectionService) {
        this.vehicleDetectionService = vehicleDetectionService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDetectionDTO>> getAllVehicleDetections() {
        return ResponseEntity.ok(vehicleDetectionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDetectionDTO> getVehicleDetection(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(vehicleDetectionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createVehicleDetection(
            @RequestBody @Valid final VehicleDetectionDTO vehicleDetectionDTO) {
        final Long createdId = vehicleDetectionService.create(vehicleDetectionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateVehicleDetection(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final VehicleDetectionDTO vehicleDetectionDTO) {
        vehicleDetectionService.update(id, vehicleDetectionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteVehicleDetection(@PathVariable(name = "id") final Long id) {
        vehicleDetectionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
