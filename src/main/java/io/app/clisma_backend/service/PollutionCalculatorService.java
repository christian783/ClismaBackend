package io.app.clisma_backend.service;

import io.app.clisma_backend.domain.EmissionRecord;
import io.app.clisma_backend.domain.Hotspot;
import io.app.clisma_backend.repos.EmissionRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private static final List<double[]> PM25_BREAKPOINTS = Arrays.asList(
            new double[]{0, 10, 0, 50},
            new double[]{10.1, 25, 51, 100},
            new double[]{25.1, 50, 101, 150},
            new double[]{50.1, 100, 151, 200},
            new double[]{100.1, 250, 201, 300},
            new double[]{250.1, Double.MAX_VALUE, 301, 500}
    );

    private static final List<double[]> PM10_BREAKPOINTS = Arrays.asList(
            new double[]{0, 20, 0, 50},
            new double[]{20.1, 50, 51, 100},
            new double[]{50.1, 100, 101, 150},
            new double[]{100.1, 250, 151, 200},
            new double[]{250.1, 500, 201, 300},
            new double[]{500.1, Double.MAX_VALUE, 301, 500}
    );

    private static final List<double[]> CO_BREAKPOINTS = Arrays.asList( // µg/m³
            new double[]{0, 2000, 0, 50},
            new double[]{2000.1, 4000, 51, 100},
            new double[]{4000.1, 10000, 101, 150},
            new double[]{10000.1, 30000, 151, 200},
            new double[]{30000.1, 40000, 201, 300},
            new double[]{40000.1, Double.MAX_VALUE, 301, 500}
    );

    private static final List<double[]> NOX_BREAKPOINTS = Arrays.asList( // µg/m³
            new double[]{0, 60, 0, 50},
            new double[]{60.1, 80, 51, 100},
            new double[]{80.1, 190, 101, 150},
            new double[]{190.1, 380, 151, 200},
            new double[]{380.1, 500, 201, 300},
            new double[]{500.1, Double.MAX_VALUE, 301, 500}
    );

    private static final List<double[]> CO2_BREAKPOINTS = Arrays.asList( // ppm
            new double[]{0, 400, 0, 50},
            new double[]{400.1, 1000, 51, 100},
            new double[]{1000.1, 2000, 101, 150},
            new double[]{2000.1, 5000, 151, 200},
            new double[]{5000.1, 10000, 201, 300},
            new double[]{10000.1, Double.MAX_VALUE, 301, 500}
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
    public double calculatePollutionLevel(String location, LocalDateTime endTime,
                                          ChronoUnit averagingUnit, long duration,
                                          boolean useWeighted) {

        LocalDateTime startTime = endTime.minus(duration, averagingUnit);
        List<EmissionRecord> records =
                emissionRepository.findByLocationAndTimestampBetween(location, startTime, endTime);

        if (records.isEmpty()) return 0.0;

        // Average concentrations (unit conversions applied consistently)
        double avgCo = records.stream().mapToDouble(EmissionRecord::getCoLevel).average().orElse(0.0) * 1000; // mg/m³ → µg/m³
        double avgNox = records.stream().mapToDouble(EmissionRecord::getNoxLevel).average().orElse(0.0);      // µg/m³
        double avgPm25 = records.stream().mapToDouble(EmissionRecord::getPm25Level).average().orElse(0.0);   // µg/m³
        double avgPm10 = records.stream().mapToDouble(EmissionRecord::getPm10Level).average().orElse(0.0);   // µg/m³
        double avgCo2 = records.stream().mapToDouble(EmissionRecord::getCo2Level).average().orElse(0.0);     // ppm

        // Calculate sub-indices
        double iCo = calculateSubIndex(avgCo, CO_BREAKPOINTS);
        double iNox = calculateSubIndex(avgNox, NOX_BREAKPOINTS);
        double iPm25 = calculateSubIndex(avgPm25, PM25_BREAKPOINTS);
        double iPm10 = calculateSubIndex(avgPm10, PM10_BREAKPOINTS);
        double iCo2 = calculateSubIndex(avgCo2, CO2_BREAKPOINTS);

        if (useWeighted) {
            // Weighted average approach (assign higher weight to PM2.5 and NOx)
            return (0.3 * iPm25 + 0.25 * iNox + 0.2 * iPm10 + 0.15 * iCo + 0.1 * iCo2);
        } else {
            // Standard AQI: worst pollutant dominates
            return Math.max(Math.max(iCo, iNox), Math.max(Math.max(iPm25, iPm10), iCo2));
        }
    }

    // Example: update a hotspot with 24-hour AQI
    public void updateHotspot(Hotspot hotspot, boolean useWeighted) {
        double level = calculatePollutionLevel(
                hotspot.getLocation(),
                LocalDateTime.now(),
                ChronoUnit.HOURS,
                24,
                useWeighted = false
        );
        hotspot.setPollutionLevel(level);
        // Save via repository...
    }
}
