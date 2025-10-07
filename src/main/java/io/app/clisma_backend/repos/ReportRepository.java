package io.app.clisma_backend.repos;

import io.app.clisma_backend.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
