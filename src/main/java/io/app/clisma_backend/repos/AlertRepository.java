package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AlertRepository extends JpaRepository<Alert, Long> {

    Alert findFirstByVehicleDetectionIdId(Long id);

}
