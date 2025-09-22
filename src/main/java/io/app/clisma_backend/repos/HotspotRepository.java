package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Hotspot;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HotspotRepository extends JpaRepository<Hotspot, Long> {
}
