package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Hotspot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;


public interface HotspotRepository extends JpaRepository<Hotspot, Long> {
    @Query("SELECT h FROM Hotspot h " +
            "LEFT JOIN h.location l WHERE" +
            "(:id IS NULL OR h.id = :id) AND " +
            ":locationId IS NULL OR :locationId = l.id AND" +
            "(CAST(:pollutionLevel AS string) IS NULL OR CAST(h.pollutionLevel AS string) = CAST(:pollutionLevel AS string)) AND " +
            "(CAST(:start AS STRING) IS NULL OR h.dateCreated >= :start) AND " +
            "(CAST(:end AS STRING) IS NULL OR h.lastUpdated <= :end)")
    Page<Hotspot> search(@Param("id") Long id,
                         @Param("locationId") Long locationId,
                         @Param("pollutionLevel") Double pollutionLevel,
                         @Param("start") OffsetDateTime start,
                         @Param("end") OffsetDateTime end,
                         Pageable pageable);

}
