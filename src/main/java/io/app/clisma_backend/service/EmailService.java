package io.app.clisma_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmissionAlert(String to, String vehicleLicense, double aqiValue, double coPpmValue) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("High Emission Alert");
        message.setText(String.format("""
            High Emission Alert!
            
            Vehicle with license plate %s has exceeded emission thresholds:
            AQI: %.2f
            CO PPM: %.2f
            
            Please take necessary action.
            
            Best regards,
            CLISMA System
            """, vehicleLicense, aqiValue, coPpmValue));

        mailSender.send(message);
    }
}
