package io.app.clisma_backend.rest;

import io.app.clisma_backend.model.AlertDTO;
import io.app.clisma_backend.service.AlertService;
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
@RequestMapping(value = "/api/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlertResource {

    private final AlertService alertService;

    public AlertResource(final AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(alertService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAlert(@RequestBody @Valid final AlertDTO alertDTO) {
        final Long createdId = alertService.create(alertDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAlert(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AlertDTO alertDTO) {
        alertService.update(id, alertDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAlert(@PathVariable(name = "id") final Long id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
