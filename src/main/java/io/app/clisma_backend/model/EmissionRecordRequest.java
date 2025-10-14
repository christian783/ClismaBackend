package io.app.clisma_backend.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmissionRecordRequest {

    private Long id;

    @JsonProperty("AQI")
    private double aqi;

    @JsonProperty("CO_PPM")
    private double coPpm;

    @JsonProperty("MQ135")
    private int mq135;

    @JsonProperty("MQ135_R")
    private double mq135R;

    @JsonProperty("MQ7")
    private int mq7;

    @JsonProperty("MQ7_R")
    private double mq7R;
    @NotNull
    private Long locationId;

    @EmissionRecordVehicleDetectionIdUnique
    private Long vehicleDetectionId;

}
