package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.EmissionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface EmissionRecordRepository extends JpaRepository<EmissionRecord, Long> {

    EmissionRecord findFirstByVehicleDetectionIdId(Long id);

    boolean existsByVehicleDetectionIdId(Long id);

    @Query("SELECT e FROM EmissionRecord e WHERE e.location = :location AND e.dateCreated BETWEEN :start AND :end")
    List<EmissionRecord> findByLocationAndTimestampBetween(@Param("location") String location,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);

}
