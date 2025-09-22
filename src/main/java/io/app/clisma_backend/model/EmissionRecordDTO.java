package io.app.clisma_backend.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmissionRecordDTO {

    private Long id;

    @Max(255)
    private Double coLevel;

    @Max(255)
    private Double noxLevel;

    @Max(255)
    private Double pm25Level;

    @Max(255)
    private Double pm10Level;

    @Max(255)
    private Double co2Level;

    @NotNull
    @Size(max = 255)
    private String location;

    @EmissionRecordVehicleDetectionIdUnique
    private Long vehicleDetectionId;

}
