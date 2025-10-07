package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Hotspot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;


public interface HotspotRepository extends JpaRepository<Hotspot, Long> {
    @Query("SELECT h FROM Hotspot h " +
            "LEFT JOIN h.location l WHERE" +
            "(:id IS NULL OR h.id = :id) AND " +
            ":locationId IS NULL OR :locationId = l.id AND" +
            "(CAST(:pollutionLevel AS string) IS NULL OR CAST(h.pollutionLevel AS string) = CAST(:pollutionLevel AS string)) AND " +
            "(CAST(:start AS STRING) IS NULL OR CAST(h.dateCreated AS STRING) >= CAST(:start AS STRING)) AND " +
            "(CAST(:end AS STRING) IS NULL OR CAST(h.lastUpdated AS STRING) <= CAST(:end AS STRING))")
    Page<Hotspot> search(@Param("id") Long id,
                         @Param("locationId") Long locationId,
                         @Param("pollutionLevel") Double pollutionLevel,
                         @Param("start") OffsetDateTime start,
                         @Param("end") OffsetDateTime end,
                         Pageable pageable);


    List<Hotspot> findByDateCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate);

}
