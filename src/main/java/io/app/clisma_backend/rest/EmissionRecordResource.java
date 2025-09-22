package io.app.clisma_backend.rest;

import io.app.clisma_backend.model.EmissionRecordDTO;
import io.app.clisma_backend.service.EmissionRecordService;
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
@RequestMapping(value = "/api/emissionRecords", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmissionRecordResource {

    private final EmissionRecordService emissionRecordService;

    public EmissionRecordResource(final EmissionRecordService emissionRecordService) {
        this.emissionRecordService = emissionRecordService;
    }

    @GetMapping
    public ResponseEntity<List<EmissionRecordDTO>> getAllEmissionRecords() {
        return ResponseEntity.ok(emissionRecordService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmissionRecordDTO> getEmissionRecord(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(emissionRecordService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createEmissionRecord(
            @RequestBody @Valid final EmissionRecordDTO emissionRecordDTO) {
        final Long createdId = emissionRecordService.create(emissionRecordDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateEmissionRecord(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final EmissionRecordDTO emissionRecordDTO) {
        emissionRecordService.update(id, emissionRecordDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteEmissionRecord(@PathVariable(name = "id") final Long id) {
        emissionRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
