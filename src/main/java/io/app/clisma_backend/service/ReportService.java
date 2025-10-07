package io.app.clisma_backend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import io.app.clisma_backend.domain.Report;
import io.app.clisma_backend.repos.EmissionRecordRepository;
import io.app.clisma_backend.repos.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final EmissionRecordRepository emissionRecordRepository;
    private final ReportRepository reportRepository;
    private final EmissionRecordService emissionRecordService;

    public Report generateCSVReport(){
        try {
            String fileName = "emission_report_"+System.currentTimeMillis()+".csv";
            CSVWriter writer = new CSVWriter(new FileWriter(fileName));

            writer.writeNext(new String[]{"Month", "CO Level" , "NOx Level", "PM2.5 Level", "PM10 Level", "CO2 Level"});

            Map<String , Map<String,Double>> monthlyData = emissionRecordService.calculateIntervalAverageEmissionRates(OffsetDateTime.now(), OffsetDateTime.now().minusMonths(6), "month");

            monthlyData.forEach((month, data) -> {
                writer.writeNext(new String[]{
                        month,
                        String.valueOf(data.get("coLevel")),
                        String.valueOf(data.get("noxLevel")),
                        String.valueOf(data.get("pm25Level")),
                        String.valueOf(data.get("pm10Level")),
                        String.valueOf(data.get("co2Level"))
                });
            });
            writer.close();
            Report report = new Report();
            report.setFilename(fileName);
            report.setFileType("csv");
            report.setReportType("monthly_emissions");
            return reportRepository.save(report);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV report", e);
        }
    }

    public Report generatePDFReport(){
        try {
            String fileName = "emission_report_"+System.currentTimeMillis()+".pdf";
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            document.add(new Paragraph("Emission Report" , titleFont));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(6);

            table.addCell(new PdfPCell(new Phrase("Month", headerFont)));
            table.addCell(new PdfPCell(new Phrase("CO Level", headerFont)));
            table.addCell(new PdfPCell(new Phrase("NOx Level", headerFont)));
            table.addCell(new PdfPCell(new Phrase("PM2.5 Level", headerFont)));
            table.addCell(new PdfPCell(new Phrase("PM10 Level", headerFont)));
            table.addCell(new PdfPCell(new Phrase("CO2 Level", headerFont)));

            Map<String, Map<String, Double>> monthlyData = emissionRecordService.calculateIntervalAverageEmissionRates(OffsetDateTime.now(), OffsetDateTime.now().minusMonths(6), "month");
            monthlyData.forEach((month, data) -> {
                table.addCell(month);
                table.addCell(String.format("%.2f", data.get("coLevel")));
                table.addCell(String.format("%.2f", data.get("noxLevel")));
                table.addCell(String.format("%.2f", data.get("pm25Level")));
                table.addCell(String.format("%.2f", data.get("pm10Level")));
                table.addCell(String.format("%.2f", data.get("co2Level")));
            });

            document.add(table);
            document.close();

            Report report = new Report();
            report.setFilename(fileName);
            report.setFileType("pdf");
            report.setReportType("monthly_emissions");
            return reportRepository.save(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }



}
