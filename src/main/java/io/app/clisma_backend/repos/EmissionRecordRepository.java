package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.EmissionRecord;
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
            "(CAST(:coLevel AS string) IS NULL OR CAST(e.coLevel AS string) = CAST(:coLevel AS string)) AND " +
            "(CAST(:noxLevel AS string) IS NULL OR CAST(e.noxLevel AS string) = CAST(:noxLevel AS string)) AND " +
            "(CAST(:pm25Level AS string) IS NULL OR CAST(e.pm25Level AS string) = CAST(:pm25Level AS string)) AND " +
            "(CAST(:pm10Level AS string) IS NULL OR CAST(e.pm10Level AS string) = CAST(:pm10Level AS string)) AND"+
            "(:locationId IS NULL OR :locationId = l.id) AND " +
            "(:vehicleDetectionId IS NULL OR :vehicleDetectionId = e.vehicleDetectionId) AND " +
            "(CAST(:start AS STRING) IS NULL OR CAST(e.dateCreated AS STRING) >= CAST(:start AS STRING)) AND " +
            "(CAST(:end AS STRING) IS NULL OR CAST(e.lastUpdated AS STRING) <= CAST(:end AS STRING))")
    Page<EmissionRecord> search(@Param("id") Long id,
                                @Param("coLevel") Double coLevel,
                                @Param("noxLevel") Double noxLevel,
                                @Param("pm25Level") Double pm25Level,
                                @Param("pm10Level") Double pm10Level,
                                @Param("locationId") Long locationId,
                                @Param("vehicleDetectionId") Long vehicleDetectionId,
                                @Param("start") OffsetDateTime created,
                                @Param("end") OffsetDateTime lastUpdated,
                                Pageable pageable
    );

    boolean existsByVehicleDetectionIdId(Long id);

    @Query("SELECT e FROM EmissionRecord e WHERE e.location = :location AND e.dateCreated BETWEEN :start AND :end")
    List<EmissionRecord> findByLocationAndTimestampBetween(@Param("location") String location,
                                                           @Param("start") OffsetDateTime start,
                                                           @Param("end") OffsetDateTime end);

    EmissionRecord findFirstByVehicleDetectionIdId(Long id);

    List<EmissionRecord> findByDateCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}
