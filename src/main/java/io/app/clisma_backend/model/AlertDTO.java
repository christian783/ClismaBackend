package io.app.clisma_backend.model;

import io.app.clisma_backend.domain.enums.AlertStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private AlertStatus status;

    private Long vehicleDetectionId;

}
