package io.app.clisma_backend.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class LocationDTO {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime dateCreated;
    private OffsetDateTime lastUpdated;
}