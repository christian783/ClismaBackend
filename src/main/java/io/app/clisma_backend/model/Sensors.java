package io.app.clisma_backend.model;

// Sensors Class
public class Sensors {
    private MqSensor mq;
    private Pm25Sensor pm25;
    private UltrasonicSensor ultrasonic;

    public MqSensor getMq() {
        return mq;
    }

    public void setMq(MqSensor mq) {
        this.mq = mq;
    }

    public Pm25Sensor getPm25() {
        return pm25;
    }

    public void setPm25(Pm25Sensor pm25) {
        this.pm25 = pm25;
    }

    public UltrasonicSensor getUltrasonic() {
        return ultrasonic;
    }

    public void setUltrasonic(UltrasonicSensor ultrasonic) {
        this.ultrasonic = ultrasonic;
    }
}
