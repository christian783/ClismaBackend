package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.Location;
import io.app.clisma_backend.repos.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import io.app.clisma_backend.model.LocationDTO;
import io.app.clisma_backend.util.NotFoundException;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

//    public List<LocationDTO> findAll() {
//        final List<Location> locations = locationRepository.findAll(Sort.by("id"));
//        return locations.stream()
//                .map(location -> mapToDTO(location, new LocationDTO()))
//                .toList();
//    }

    public Page<LocationDTO> search(Long id, String name, String description, Double latitude,
                                    Double longitude, OffsetDateTime start, OffsetDateTime end, Pageable pageable) {
        return locationRepository.search(id, name, description, latitude, longitude, start, end, pageable)
                .map(location -> mapToDTO(location, new LocationDTO()));
    }

    public LocationDTO get(final Long id) {
        return locationRepository.findById(id)
                .map(location -> mapToDTO(location, new LocationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Long create(final LocationDTO locationDTO) {
        final Location location = new Location();
        mapToEntity(locationDTO, location);
        return locationRepository.save(location).getId();
    }

    @Transactional
    public void update(final Long id, final LocationDTO locationDTO) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(locationDTO, location);
        locationRepository.save(location);
    }

    @Transactional
    public void delete(final Long id) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        locationRepository.delete(location);
    }

    private LocationDTO mapToDTO(final Location location, final LocationDTO locationDTO) {
        locationDTO.setId(location.getId());
        locationDTO.setName(location.getName());
        locationDTO.setDescription(location.getDescription());
        locationDTO.setLatitude(location.getLatitude());
        locationDTO.setLongitude(location.getLongitude());
        locationDTO.setDateCreated(location.getDateCreated());
        locationDTO.setLastUpdated(location.getLastUpdated());
        return locationDTO;
    }

    private Location mapToEntity(final LocationDTO locationDTO, final Location location) {
        location.setName(locationDTO.getName());
        location.setDescription(locationDTO.getDescription());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        return location;
    }
}

