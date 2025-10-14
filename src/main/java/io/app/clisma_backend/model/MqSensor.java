package io.app.clisma_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// MQ Sensor Class
public class MqSensor {
    private boolean connected;
    private MqData data;
    private boolean fresh;
    private boolean visible;

    @JsonProperty("warming_up")
    private boolean warmingUp;

    @JsonProperty("warmup_remaining")
    private int warmupRemaining;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public MqData getData() {
        return data;
    }

    public void setData(MqData data) {
        this.data = data;
    }

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isWarmingUp() {
        return warmingUp;
    }

    public void setWarmingUp(boolean warmingUp) {
        this.warmingUp = warmingUp;
    }

    public int getWarmupRemaining() {
        return warmupRemaining;
    }

    public void setWarmupRemaining(int warmupRemaining) {
        this.warmupRemaining = warmupRemaining;
    }
}
