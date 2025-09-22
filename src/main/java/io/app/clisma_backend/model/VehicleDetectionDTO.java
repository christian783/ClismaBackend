package io.app.clisma_backend.model;

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

}
