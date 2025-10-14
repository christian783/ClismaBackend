package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.EmissionRecord;
import io.app.clisma_backend.domain.Hotspot;
import io.app.clisma_backend.repos.EmissionRecordRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class PollutionCalculatorService {
    private final EmissionRecordRepository emissionRepository;

    public PollutionCalculatorService(EmissionRecordRepository emissionRepository) {
        this.emissionRepository = emissionRepository;
    }

    // ==== AQI Breakpoints ====
    private static final List<double[]> AQI_BREAKPOINTS = Arrays.asList(
            new double[]{0, 50, 0, 50},      // Good
            new double[]{51, 100, 51, 100},  // Moderate
            new double[]{101, 150, 101, 150}, // Unhealthy for Sensitive Groups
            new double[]{151, 200, 151, 200}, // Unhealthy
            new double[]{201, 300, 201, 300}, // Very Unhealthy
            new double[]{301, Double.MAX_VALUE, 301, 500} // Hazardous
    );

    private static final List<double[]> CO_PPM_BREAKPOINTS = Arrays.asList(
            new double[]{0, 4.4, 0, 50},     // Good
            new double[]{4.5, 9.4, 51, 100}, // Moderate
            new double[]{9.5, 12.4, 101, 150}, // Unhealthy for Sensitive Groups
            new double[]{12.5, 15.4, 151, 200}, // Unhealthy
            new double[]{15.5, 30.4, 201, 300}, // Very Unhealthy
            new double[]{30.5, Double.MAX_VALUE, 301, 500} // Hazardous
    );

    private static final List<double[]> MQ135_BREAKPOINTS = Arrays.asList(
            new double[]{0, 100, 0, 50},     // Low pollution
            new double[]{101, 200, 51, 100}, // Moderate pollution
            new double[]{201, 300, 101, 150}, // High pollution
            new double[]{301, 400, 151, 200}, // Very high pollution
            new double[]{401, 500, 201, 300}, // Severe pollution
            new double[]{501, Double.MAX_VALUE, 301, 500} // Extreme pollution
    );

    private static final List<double[]> MQ135R_BREAKPOINTS = Arrays.asList(
            new double[]{0, 1.0, 0, 50},     // Clean air
            new double[]{1.1, 2.0, 51, 100}, // Light pollution
            new double[]{2.1, 3.0, 101, 150}, // Moderate pollution
            new double[]{3.1, 4.0, 151, 200}, // Heavy pollution
            new double[]{4.1, 5.0, 201, 300}, // Very heavy pollution
            new double[]{5.1, Double.MAX_VALUE, 301, 500} // Extreme pollution
    );

    private static final List<double[]> MQ7_BREAKPOINTS = Arrays.asList(
            new double[]{0, 100, 0, 50},     // Low CO
            new double[]{101, 200, 51, 100}, // Moderate CO
            new double[]{201, 300, 101, 150}, // High CO
            new double[]{301, 400, 151, 200}, // Very high CO
            new double[]{401, 500, 201, 300}, // Severe CO
            new double[]{501, Double.MAX_VALUE, 301, 500} // Extreme CO
    );

    private static final List<double[]> MQ7R_BREAKPOINTS = Arrays.asList(
            new double[]{0, 1.0, 0, 50},     // Clean air
            new double[]{1.1, 2.0, 51, 100}, // Light CO presence
            new double[]{2.1, 3.0, 101, 150}, // Moderate CO presence
            new double[]{3.1, 4.0, 151, 200}, // High CO presence
            new double[]{4.1, 5.0, 201, 300}, // Very high CO presence
            new double[]{5.1, Double.MAX_VALUE, 301, 500} // Dangerous CO presence
    );

    // ==== Core Formula: Linear Interpolation ====
    private double calculateSubIndex(double concentration, List<double[]> breakpoints) {
        for (double[] bp : breakpoints) {
            double cLow = bp[0], cHigh = bp[1], iLow = bp[2], iHigh = bp[3];

            // Clamp if concentration is beyond defined range
            if (concentration > cHigh && cHigh == Double.MAX_VALUE) {
                return iHigh;
            }

            if (concentration >= cLow && concentration <= cHigh) {
                return ((iHigh - iLow) / (cHigh - cLow)) * (concentration - cLow) + iLow;
            }
        }
        return 500; // Fallback: maximum AQI
    }

    // ==== Main Calculation ====
    public double calculatePollutionLevel(String location, OffsetDateTime endTime,
                                          ChronoUnit averagingUnit, long duration,
                                          boolean useWeighted) {

        OffsetDateTime startTime = endTime.minus(duration, averagingUnit);
        List<EmissionRecord> records =
                emissionRepository.findByLocationAndTimestampBetween(location, startTime, endTime);

        if (records.isEmpty()) return 0.0;

        // Calculate averages for each sensor
        double avgAqi = records.stream().mapToDouble(EmissionRecord::getAqi).average().orElse(0.0);
        double avgCoPpm = records.stream().mapToDouble(EmissionRecord::getCoPpm).average().orElse(0.0);
        double avgMq135 = records.stream().mapToDouble(EmissionRecord::getMq135).average().orElse(0.0);
        double avgMq135R = records.stream().mapToDouble(EmissionRecord::getMq135R).average().orElse(0.0);
        double avgMq7 = records.stream().mapToDouble(EmissionRecord::getMq7).average().orElse(0.0);
        double avgMq7R = records.stream().mapToDouble(EmissionRecord::getMq7R).average().orElse(0.0);

        // Calculate sub-indices for each sensor
        double iAqi = calculateSubIndex(avgAqi, AQI_BREAKPOINTS);
        double iCoPpm = calculateSubIndex(avgCoPpm, CO_PPM_BREAKPOINTS);
        double iMq135 = calculateSubIndex(avgMq135, MQ135_BREAKPOINTS);
        double iMq135R = calculateSubIndex(avgMq135R, MQ135R_BREAKPOINTS);
        double iMq7 = calculateSubIndex(avgMq7, MQ7_BREAKPOINTS);
        double iMq7R = calculateSubIndex(avgMq7R, MQ7R_BREAKPOINTS);

        if (useWeighted) {
            // Weighted average approach (weights based on sensor reliability and importance)
            return (0.3 * iAqi +       // AQI is most reliable overall air quality indicator
                   0.2 * iCoPpm +      // CO PPM is important for carbon monoxide detection
                   0.15 * iMq7 +       // MQ7 specifically detects CO
                   0.15 * iMq7R +      // MQ7 resistance provides additional CO context
                   0.1 * iMq135 +      // MQ135 detects various air pollutants
                   0.1 * iMq135R);     // MQ135 resistance gives additional context
        } else {
            // Standard approach: worst pollutant dominates
            return Math.max(
                Math.max(Math.max(iAqi, iCoPpm), Math.max(iMq135, iMq135R)),
                Math.max(iMq7, iMq7R)
            );
        }
    }

    // Example: update a hotspot with 24-hour AQI
    public void updateHotspot(Hotspot hotspot, boolean useWeighted) {
        double level = calculatePollutionLevel(
                hotspot.getLocation().getName(),
                OffsetDateTime.now(),
                ChronoUnit.HOURS,
                24,
                useWeighted = false
        );
        hotspot.setPollutionLevel(level);
        // Save via repository...
    }
}
