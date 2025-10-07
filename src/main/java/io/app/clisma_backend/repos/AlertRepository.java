package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Alert;
import io.app.clisma_backend.domain.enums.AlertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    @Query("SELECT a FROM Alert a WHERE " +
            "(:id IS NULL OR a.id = :id) AND " +
            "(:type IS NULL OR a.type LIKE %:type%) AND " +
            "(:message IS NULL OR a.message LIKE %:message%) AND " +
            "(:sentTo IS NULL OR a.sentTo LIKE %:sentTo%) AND " +
            "(CAST(:status AS STRING) IS NULL OR CAST(a.status AS STRING) = CAST(:status AS STRING)) AND " +
            "(CAST(:start AS STRING) IS NULL OR CAST(a.dateCreated AS STRING) >= CAST(:start AS STRING)) AND " +
            "(CAST(:end AS STRING) IS NULL OR CAST(a.lastUpdated AS STRING) <= CAST(:end AS STRING))")
    Page<Alert> search(@Param("id") Long id,
                       @Param("type") String type,
                       @Param("message") String message,
                       @Param("sentTo") String sentTo,
                       @Param("status") AlertStatus status,
                       @Param("start") OffsetDateTime start,
                       @Param("end") OffsetDateTime end,
                       Pageable pageable);

    Alert findFirstByVehicleDetectionIdId(Long id);
    List<Alert> findByDateCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}
