package io.app.clisma_backend.model;

import io.app.clisma_backend.domain.Location;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmissionRecordDTO {

    private Long id;

    private Double coLevel;

    private Double noxLevel;

    private Double pm25Level;

    private Double pm10Level;

    private Double co2Level;

    @NotNull
    private Location location;

    @EmissionRecordVehicleDetectionIdUnique
    private Long vehicleDetectionId;

}
