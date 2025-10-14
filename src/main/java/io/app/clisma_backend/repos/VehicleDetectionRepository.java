package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.domain.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface VehicleDetectionRepository extends JpaRepository<VehicleDetection, Long> {
    @Query("SELECT v FROM VehicleDetection v " +
            "LEFT JOIN v.emissionRecord e " +
            "WHERE " +
            "(:id IS NULL OR v.id = :id) AND " +
            "(:licensePlate IS NULL OR v.licensePlate LIKE %:licensePlate%) AND " +
            "(:imageUrl IS NULL OR v.imageUrl LIKE %:imageUrl%) AND " +
            "(CAST(:vehicleType AS STRING) IS NULL OR CAST(v.vehicleType AS STRING) = CAST(:vehicleType AS STRING)) AND " +
            "(:emissionRecordId IS NULL OR e.id = :emissionRecordId) AND " +
            "(CAST(:start AS STRING) IS NULL OR CAST(v.dateCreated AS STRING) >= CAST(:start AS STRING)) AND " +
            "(CAST(:end AS STRING) IS NULL OR CAST(v.lastUpdated AS STRING) <= CAST(:end AS STRING))")
    Page<VehicleDetection> search(@Param("id") Long id,
                                  @Param("licensePlate") String licensePlate,
                                  @Param("imageUrl") String imageUrl,
                                  @Param("vehicleType") VehicleType vehicleType,
                                  @Param("emissionRecordId") Long emissionRecordId,
                                  @Param("start") OffsetDateTime start,
                                  @Param("end") OffsetDateTime end,
                                  Pageable pageable);

    List<VehicleDetection> findByDateCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}
