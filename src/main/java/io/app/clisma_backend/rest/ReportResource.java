package io.app.clisma_backend.rest;

import io.app.clisma_backend.domain.Report;
import io.app.clisma_backend.service.ReportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.File;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/reports")
public class ReportResource {
    private final ReportService reportService;

    public ReportResource(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/csv")
    public ResponseEntity<Resource> generateCSVReport() {
        Report report = reportService.generateCSVReport();
        Resource resource = new FileSystemResource(new File(report.getFilename()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getFilename())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/pdf")
    public ResponseEntity<Resource> generatePDFReport() {
        Report report = reportService.generatePDFReport();
        Resource resource = new FileSystemResource(new File(report.getFilename()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getFilename())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
