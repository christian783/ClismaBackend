package io.app.clisma_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// MQ Data Class
public class MqData {
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

    public double getAqi() {
        return aqi;
    }

    public void setAqi(double aqi) {
        this.aqi = aqi;
    }

    public double getCoPpm() {
        return coPpm;
    }

    public void setCoPpm(double coPpm) {
        this.coPpm = coPpm;
    }

    public int getMq135() {
        return mq135;
    }

    public void setMq135(int mq135) {
        this.mq135 = mq135;
    }

    public double getMq135R() {
        return mq135R;
    }

    public void setMq135R(double mq135R) {
        this.mq135R = mq135R;
    }

    public int getMq7() {
        return mq7;
    }

    public void setMq7(int mq7) {
        this.mq7 = mq7;
    }

    public double getMq7R() {
        return mq7R;
    }

    public void setMq7R(double mq7R) {
        this.mq7R = mq7R;
    }
}
