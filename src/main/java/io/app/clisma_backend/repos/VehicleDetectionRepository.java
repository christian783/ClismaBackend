package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.VehicleDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface VehicleDetectionRepository extends JpaRepository<VehicleDetection, Long> {
    @Query("SELECT COUNT(v) FROM VehicleDetection v")
    int totalNumberofVehicleDetections();
}
