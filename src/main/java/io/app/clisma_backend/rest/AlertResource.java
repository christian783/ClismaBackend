package io.app.clisma_backend.rest;

import io.app.clisma_backend.domain.enums.AlertStatus;
import io.app.clisma_backend.model.AlertDTO;
import io.app.clisma_backend.service.AlertService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;

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
@RequestMapping(value = "/api/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlertResource {

    private final AlertService alertService;

    public AlertResource(final AlertService alertService) {
        this.alertService = alertService;
    }

//    @GetMapping
//    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
//        return ResponseEntity.ok(alertService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<AlertDTO> getAlert(@PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(alertService.get(id));
//    }

    // AlertController.java
    @GetMapping("/search")
    public ResponseEntity<Page<AlertDTO>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String sentTo,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @ParameterObject @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(alertService.search(id, type, message, sentTo, status, start, end, pageable));
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

    @GetMapping("/count")
    public ResponseEntity<Integer> getViolationsCount() {
        Integer count = alertService.countTotalNumberOfViolations();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAlert(@PathVariable(name = "id") final Long id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
