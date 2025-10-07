package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.Hotspot;
import io.app.clisma_backend.model.HotspotDTO;
import io.app.clisma_backend.repos.HotspotRepository;
import io.app.clisma_backend.util.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class HotspotService {

    private final HotspotRepository hotspotRepository;

    public HotspotService(final HotspotRepository hotspotRepository) {
        this.hotspotRepository = hotspotRepository;
    }

//    public List<HotspotDTO> findAll() {
//        final List<Hotspot> hotspots = hotspotRepository.findAll(Sort.by("id"));
//        return hotspots.stream()
//                .map(hotspot -> mapToDTO(hotspot, new HotspotDTO()))
//                .toList();
//    }

    // HotspotService.java
    public Page<HotspotDTO> search(Long id, Long locationId,Double pollutionLevel, OffsetDateTime start,
                                   OffsetDateTime end, Pageable pageable) {
        return hotspotRepository.search(id,locationId,pollutionLevel, start, end, pageable)
                .map(hotspot -> mapToDTO(hotspot, new HotspotDTO()));
    }


    public HotspotDTO get(final Long id) {
        return hotspotRepository.findById(id)
                .map(hotspot -> mapToDTO(hotspot, new HotspotDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final HotspotDTO hotspotDTO) {
        final Hotspot hotspot = new Hotspot();
        mapToEntity(hotspotDTO, hotspot);
        return hotspotRepository.save(hotspot).getId();
    }

    public void update(final Long id, final HotspotDTO hotspotDTO) {
        final Hotspot hotspot = hotspotRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(hotspotDTO, hotspot);
        hotspotRepository.save(hotspot);
    }

    public Integer getTotalNumberOfHotspots() {
        return (int) hotspotRepository.count();
    }

    public void delete(final Long id) {
        final Hotspot hotspot = hotspotRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        hotspotRepository.delete(hotspot);
    }

    private HotspotDTO mapToDTO(final Hotspot hotspot, final HotspotDTO hotspotDTO) {
        hotspotDTO.setId(hotspot.getId());
        hotspotDTO.setLocation(hotspot.getLocation());
        hotspotDTO.setPollutionLevel(hotspot.getPollutionLevel());
        return hotspotDTO;
    }

    private Hotspot mapToEntity(final HotspotDTO hotspotDTO, final Hotspot hotspot) {
        hotspot.setLocation(hotspotDTO.getLocation());
        hotspot.setPollutionLevel(hotspotDTO.getPollutionLevel());
        return hotspot;
    }

}
