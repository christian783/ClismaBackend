package io.app.clisma_backend.rest;

import io.app.clisma_backend.model.HotspotDTO;
import io.app.clisma_backend.service.HotspotService;
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
@RequestMapping(value = "/api/hotspots", produces = MediaType.APPLICATION_JSON_VALUE)
public class HotspotResource {

    private final HotspotService hotspotService;

    public HotspotResource(final HotspotService hotspotService) {
        this.hotspotService = hotspotService;
    }

//    @GetMapping
//    public ResponseEntity<List<HotspotDTO>> getAllHotspots() {
//        return ResponseEntity.ok(hotspotService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<HotspotDTO> getHotspot(@PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(hotspotService.get(id));
//    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createHotspot(@RequestBody @Valid final HotspotDTO hotspotDTO) {
        final Long createdId = hotspotService.create(hotspotDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    // HotspotController.java
    @GetMapping("/search")
    public ResponseEntity<Page<HotspotDTO>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Double pollutionLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @ParameterObject @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(hotspotService.search(id, locationId, pollutionLevel, start, end, pageable));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Long> updateHotspot(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final HotspotDTO hotspotDTO) {
        hotspotService.update(id, hotspotDTO);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getHotspotCount() {
        Integer count = hotspotService.getTotalNumberOfHotspots();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteHotspot(@PathVariable(name = "id") final Long id) {
        hotspotService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
