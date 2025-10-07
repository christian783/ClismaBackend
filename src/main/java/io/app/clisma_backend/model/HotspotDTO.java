package io.app.clisma_backend.model;

import io.app.clisma_backend.domain.Location;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HotspotDTO {

    private Long id;

    @NotNull
    private Location location;

    @NotNull
    private Double pollutionLevel;

}
