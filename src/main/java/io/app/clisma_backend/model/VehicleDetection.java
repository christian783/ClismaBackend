package io.app.clisma_backend.model;

// Vehicle Detection Class
public class VehicleDetection {
    private boolean active;
    private String message;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
