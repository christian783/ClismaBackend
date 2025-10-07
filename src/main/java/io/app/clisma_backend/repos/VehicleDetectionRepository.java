package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.VehicleDetection;
import io.app.clisma_backend.domain.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface VehicleDetectionRepository extends JpaRepository<VehicleDetection, Long> {
    @Query("SELECT v FROM VehicleDetection v " +
            "LEFT JOIN v.emissionRecord e " +
            "WHERE " +
            "(:id IS NULL OR v.id = :id) AND " +
            "(:licensePlate IS NULL OR v.licensePlate LIKE %:licensePlate%) AND " +
            "(:imageUrl IS NULL OR v.imageUrl LIKE %:imageUrl%) AND " +
            "(CAST(:vehicleType AS STRING) IS NULL OR CAST(v.vehicleType AS STRING) = CAST(:vehicleType AS STRING)) AND " +
            "(:emissionRecordId IS NULL OR :emissionRecordId = e.id) AND " +
            "(CAST(:start AS STRING) IS NULL OR v.dateCreated >= :start) AND " +
            "(CAST(:end AS STRING) IS NULL OR v.lastUpdated <= :end)")
    Page<VehicleDetection> search(@Param("id") Long id,
                                  @Param("licensePlate") String licensePlate,
                                  @Param("imageUrl") String imageUrl,
                                  @Param("vehicleType") VehicleType vehicleType,
                                  @Param("emissionRecordId") Long emissionRecordId,
                                  @Param("start") OffsetDateTime start,
                                  @Param("end") OffsetDateTime end,
                                  Pageable pageable);
}
