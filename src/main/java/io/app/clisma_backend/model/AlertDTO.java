package io.app.clisma_backend.model;

import io.app.clisma_backend.domain.AlertStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AlertDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String type;

    @NotNull
    @Size(max = 255)
    private String message;

    @NotNull
    @Size(max = 255)
    private String sentTo;

    @NotNull
    @Size(max = 255)
    private AlertStatus status;

    private Long vehicleDetectionId;

}
