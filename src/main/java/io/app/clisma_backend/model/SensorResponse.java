package io.app.clisma_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

// Main Response Class
public class SensorResponse {
    @JsonProperty("reading_count")
    private int readingCount;

    private Sensors sensors;
    private Status status;
    private LocalDateTime timestamp;

    @JsonProperty("vehicle_detection")
    private VehicleDetection vehicleDetection;

    // Getters and Setters
    public int getReadingCount() {
        return readingCount;
    }

    public void setReadingCount(int readingCount) {
        this.readingCount = readingCount;
    }

    public Sensors getSensors() {
        return sensors;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public VehicleDetection getVehicleDetection() {
        return vehicleDetection;
    }

    public void setVehicleDetection(VehicleDetection vehicleDetection) {
        this.vehicleDetection = vehicleDetection;
    }
}

// PM2.5 Sensor Class
class Pm25Sensor {
    private boolean connected;
    private boolean fresh;
    private String unit;
    private int value;
    private boolean visible;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

// Ultrasonic Sensor Class
class UltrasonicSensor {
    @JsonProperty("actual_distance")
    private int actualDistance;

    @JsonProperty("detection_state")
    private String detectionState;

    private String direction;
    private boolean enabled;

    @JsonProperty("path_type")
    private String pathType;

    private double sensor1;
    private double sensor2;
    private double speed;
    private String unit;

    @JsonProperty("vehicle_detected")
    private boolean vehicleDetected;

    public int getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(int actualDistance) {
        this.actualDistance = actualDistance;
    }

    public String getDetectionState() {
        return detectionState;
    }

    public void setDetectionState(String detectionState) {
        this.detectionState = detectionState;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPathType() {
        return pathType;
    }

    public void setPathType(String pathType) {
        this.pathType = pathType;
    }

    public double getSensor1() {
        return sensor1;
    }

    public void setSensor1(double sensor1) {
        this.sensor1 = sensor1;
    }

    public double getSensor2() {
        return sensor2;
    }

    public void setSensor2(double sensor2) {
        this.sensor2 = sensor2;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isVehicleDetected() {
        return vehicleDetected;
    }

    public void setVehicleDetected(boolean vehicleDetected) {
        this.vehicleDetected = vehicleDetected;
    }
}

// Status Class
class Status {
    @JsonProperty("mq_sensors")
    private String mqSensors;

    private String pms5003;
    private String ultrasonic;

    public String getMqSensors() {
        return mqSensors;
    }

    public void setMqSensors(String mqSensors) {
        this.mqSensors = mqSensors;
    }

    public String getPms5003() {
        return pms5003;
    }

    public void setPms5003(String pms5003) {
        this.pms5003 = pms5003;
    }

    public String getUltrasonic() {
        return ultrasonic;
    }

    public void setUltrasonic(String ultrasonic) {
        this.ultrasonic = ultrasonic;
    }
}

