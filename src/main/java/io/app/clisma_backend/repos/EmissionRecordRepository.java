package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.EmissionRecord;
import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.domain.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;

public interface EmissionRecordRepository extends JpaRepository<EmissionRecord, Long> {
    @Query("SELECT e FROM EmissionRecord e " +
            "LEFT JOIN e.location l " +
            "WHERE " +
            "(:id IS NULL OR e.id = :id) AND " +
            "(CAST(:aqi AS string) IS NULL OR CAST(e.aqi AS string) = CAST(:aqi AS string)) AND " +
            "(CAST(:coPpm AS string) IS NULL OR CAST(e.coPpm AS string) = CAST(:coPpm AS string)) AND " +
            "(CAST(:mq135 AS string) IS NULL OR CAST(e.mq135 AS string) = CAST(:mq135 AS string)) AND " +
            "(CAST(:mq135R AS string) IS NULL OR CAST(e.mq135R AS string) = CAST(:mq135R AS string)) AND " +
            "(CAST(:mq7 AS string) IS NULL OR CAST(e.mq7 AS string) = CAST(:mq7 AS string)) AND " +
            "(CAST(:mq7R AS string) IS NULL OR CAST(e.mq7R AS string) = CAST(:mq7R AS string)) AND " +
            "(:locationId IS NULL OR :locationId = l.id) AND " +
            "(:vehicleDetectionId IS NULL OR :vehicleDetectionId = e.vehicleDetectionId) AND " +
            "(CAST(:start AS STRING) IS NULL OR CAST(e.dateCreated AS STRING) >= CAST(:start AS STRING)) AND " +
            "(CAST(:end AS STRING) IS NULL OR CAST(e.lastUpdated AS STRING) <= CAST(:end AS STRING))")
    Page<EmissionRecord> search(@Param("id") Long id,
                              @Param("aqi") Double aqi,
                              @Param("coPpm") Double coPpm,
                              @Param("mq135") Integer mq135,
                              @Param("mq135R") Double mq135R,
                              @Param("mq7") Integer mq7,
                              @Param("mq7R") Double mq7R,
                              @Param("locationId") Long locationId,
                              @Param("vehicleDetectionId") Long vehicleDetectionId,
                              @Param("start") OffsetDateTime start,
                              @Param("end") OffsetDateTime end,
                              Pageable pageable
    );

    boolean existsByVehicleDetectionIdId(Long id);

    @Query("SELECT e FROM EmissionRecord e WHERE e.location = :location AND e.dateCreated BETWEEN :start AND :end")
    List<EmissionRecord> findByLocationAndTimestampBetween(@Param("location") String location,
                                                           @Param("start") OffsetDateTime start,
                                                           @Param("end") OffsetDateTime end);



    /**
     * Finds emission records with vehicle data for pollution analysis
     * @return List of emission records with eagerly fetched vehicle detection data
     */
    @Query("SELECT e FROM EmissionRecord e JOIN FETCH e.vehicleDetectionId v ORDER BY e.id")
    List<EmissionRecord> findAllWithVehicleDetection();

    EmissionRecord findFirstByVehicleDetectionIdId(Long id);

    List<EmissionRecord> findByDateCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}
