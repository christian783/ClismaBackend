package io.app.clisma_backend.rest;

import io.app.clisma_backend.model.HotspotDTO;
import io.app.clisma_backend.service.HotspotService;
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
@RequestMapping(value = "/api/hotspots", produces = MediaType.APPLICATION_JSON_VALUE)
public class HotspotResource {

    private final HotspotService hotspotService;

    public HotspotResource(final HotspotService hotspotService) {
        this.hotspotService = hotspotService;
    }

    @GetMapping
    public ResponseEntity<List<HotspotDTO>> getAllHotspots() {
        return ResponseEntity.ok(hotspotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotspotDTO> getHotspot(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(hotspotService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createHotspot(@RequestBody @Valid final HotspotDTO hotspotDTO) {
        final Long createdId = hotspotService.create(hotspotDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateHotspot(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final HotspotDTO hotspotDTO) {
        hotspotService.update(id, hotspotDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteHotspot(@PathVariable(name = "id") final Long id) {
        hotspotService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
