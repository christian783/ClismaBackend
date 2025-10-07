package io.app.clisma_backend.rest;

import io.app.clisma_backend.domain.enums.VehicleType;
import io.app.clisma_backend.model.VehicleDetectionDTO;
import io.app.clisma_backend.service.VehicleDetectionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.time.OffsetDateTime;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/vehicleDetections", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleDetectionResource {

    private final VehicleDetectionService vehicleDetectionService;

    public VehicleDetectionResource(final VehicleDetectionService vehicleDetectionService) {
        this.vehicleDetectionService = vehicleDetectionService;
    }

    // VehicleDetectionController.java
    @GetMapping("/search")
    public ResponseEntity<Page<VehicleDetectionDTO>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @ParameterObject @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(vehicleDetectionService.search(id, licensePlate, imageUrl, vehicleType,locationId, start, end, pageable));
    }


//    @GetMapping
//    public ResponseEntity<List<VehicleDetectionDTO>> getAllVehicleDetections() {
//        return ResponseEntity.ok(vehicleDetectionService.findAll());
//    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getVehicleCount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end
    ) {
        Integer count = vehicleDetectionService.getTotalNumberOfDetections(start,end);
        return ResponseEntity.ok(count);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<VehicleDetectionDTO> getVehicleDetection(
//            @PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(vehicleDetectionService.get(id));
//    }

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
