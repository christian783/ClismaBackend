package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l WHERE " +
            "(:id IS NULL OR l.id = :id) AND " +
            "(:name IS NULL OR l.name LIKE %:name%) AND " +
            "(:description IS NULL OR l.description LIKE %:description%) AND " +
            "(CAST(:latitude AS string) IS NULL OR CAST(l.latitude AS string) = CAST(:latitude AS string)) AND " +
            "(CAST(:longitude AS string) IS NULL OR CAST(l.longitude AS string) = CAST(:longitude AS string)) AND " +
            "(CAST(:start AS STRING) IS NULL OR CAST(l.dateCreated AS STRING) >= CAST(:start AS STRING)) AND " +
            "(:end IS NULL OR l.lastUpdated <= :end)")
    Page<Location> search(@Param("id") Long id,
                          @Param("name") String name,
                          @Param("description") String description,
                          @Param("latitude") Double latitude,
                          @Param("longitude") Double longitude,
                          @Param("start") OffsetDateTime start,
                          @Param("end") OffsetDateTime end,
                          Pageable pageable);
}
