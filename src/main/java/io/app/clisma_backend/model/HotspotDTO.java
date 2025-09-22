package io.app.clisma_backend.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HotspotDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String location;

    @NotNull
    @Size(max = 255)
    private Double pollutionLevel;

}
