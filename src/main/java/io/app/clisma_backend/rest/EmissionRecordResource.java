package io.app.clisma_backend.rest;

import io.app.clisma_backend.domain.enums.VehicleType;
import io.app.clisma_backend.model.EmissionRecordRequest;
import io.app.clisma_backend.model.EmissionRecordResponse;
import io.app.clisma_backend.service.EmissionRecordService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
@RequestMapping(value = "/api/emissionRecords", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmissionRecordResource {

    private final EmissionRecordService emissionRecordService;

    public EmissionRecordResource(final EmissionRecordService emissionRecordService) {
        this.emissionRecordService = emissionRecordService;
    }

//    @GetMapping
//    public ResponseEntity<List<EmissionRecordRequest>> getAllEmissionRecords() {
//        return ResponseEntity.ok(emissionRecordService.findAll());
//    }

    // EmissionRecordController.java
    @GetMapping("/search")
    public ResponseEntity<Page<EmissionRecordResponse>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Double aqi,
            @RequestParam(required = false) Double coPpm,
            @RequestParam(required = false) Integer mq135,
            @RequestParam(required = false) Double mq135R,
            @RequestParam(required = false) Integer mq7,
            @RequestParam(required = false) Double mq7R,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long vehicleDetectionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @ParameterObject @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(emissionRecordService.search(id, aqi, coPpm, mq135, mq135R, mq7, mq7R,
                locationId, vehicleDetectionId, start, end, pageable));
    }

    /**
     * Returns a list of vehicles with the highest pollution levels.
     *
     * @param pollutantType The type of pollutant to sort by (co, nox, pm25, pm10, co2, or total)
     * @param limit The maximum number of vehicles to return (defaults to 10)
     * @return A list of the highest polluting vehicles with their emission data
     */
    @GetMapping("/highest-polluters")
    public ResponseEntity<List<Map<String, Object>>> getHighestPollutingVehicles(
            @RequestParam(defaultValue = "total") String pollutantType,
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> highestPolluters = emissionRecordService.getHighestPollutingVehicles(pollutantType, limit);
        return ResponseEntity.ok(highestPolluters);
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<EmissionRecordDTO> getEmissionRecord(
//            @PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(emissionRecordService.get(id));
//    }

    @GetMapping("/averages/adjustable")
    public Map<String, Map<String, Double>> getIntervalAverages(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam String interval) {

        return emissionRecordService.calculateIntervalAverageEmissionRates(startDate, endDate, interval);
    }

    @GetMapping("/averages")
    public ResponseEntity<Map<String,Double>> getAverageEmissions() {
        Map<String,Double> averages = emissionRecordService.calculateAverageEmissionRates();
        return ResponseEntity.ok(averages);
    }

    @GetMapping("/averages/vehicleType/{vehicleType}")
    public ResponseEntity<Map<String,Double>> calculateAverageEmissionLevelsByVehicleType(@PathVariable("vehicleType") VehicleType vehicleType) {
        Map<String,Double> averages = emissionRecordService.calculateAverageEmissionLevelsByVehicleType(vehicleType);
        return ResponseEntity.ok(averages);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createEmissionRecord(
            @RequestBody @Valid final EmissionRecordRequest emissionRecordDTO) {
        final Long createdId = emissionRecordService.create(emissionRecordDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateEmissionRecord(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final EmissionRecordRequest emissionRecordDTO) {
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
