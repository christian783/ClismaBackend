package io.app.clisma_backend.model;

import io.app.clisma_backend.domain.enums.VehicleType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VehicleDetectionDTO {

    private Long id;

    @Size(max = 255)
    private String licensePlate;

    @Size(max = 255)
    private String imageUrl;

    private VehicleType vehicleType;

    private Long userId;

}
