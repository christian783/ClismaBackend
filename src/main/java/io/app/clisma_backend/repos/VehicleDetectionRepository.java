package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.VehicleDetection;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VehicleDetectionRepository extends JpaRepository<VehicleDetection, Long> {
}
