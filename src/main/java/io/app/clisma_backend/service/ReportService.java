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

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final EmissionRecordRepository emissionRecordRepository;
    private final ReportRepository reportRepository;
    private final EmissionRecordService emissionRecordService;

    public Report generateCSVReport() {
        try {
            String fileName = "emission_report_" + System.currentTimeMillis() + ".csv";
            CSVWriter writer = new CSVWriter(new FileWriter(fileName));

            // Updated headers to include all emission metrics
            writer.writeNext(new String[]{
                "Time Period",
                "AQI",
                "CO PPM",
                "MQ135 Reading",
                "MQ135 Resistance",
                "MQ7 Reading",
                "MQ7 Resistance"
            });

            Map<String, Map<String, Double>> intervalData = emissionRecordService.calculateIntervalAverageEmissionRates(
                    OffsetDateTime.now().minusMonths(6),
                    OffsetDateTime.now(),
                    "month"
            );

            intervalData.forEach((interval, data) -> {
                writer.writeNext(new String[]{
                        interval,
                        String.format("%.2f", data.get("aqi")),
                        String.format("%.2f", data.get("coPpm")),
                        String.format("%.2f", data.get("mq135")),
                        String.format("%.2f", data.get("mq135R")),
                        String.format("%.2f", data.get("mq7")),
                        String.format("%.2f", data.get("mq7R"))
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

    public Report generatePDFReport() {
        try {
            String fileName = "emission_report_" + System.currentTimeMillis() + ".pdf";
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.DARK_GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            Paragraph title = new Paragraph("Emission Analysis Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // Add timestamp
            Paragraph timestamp = new Paragraph(
                "Generated on: " + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss")),
                subtitleFont
            );
            timestamp.setAlignment(Element.ALIGN_CENTER);
            timestamp.setSpacingAfter(20);
            document.add(timestamp);

            // Add description
            Paragraph description = new Paragraph(
                "This report provides a detailed analysis of emission levels over the past 6 months, " +
                "including Air Quality Index (AQI), Carbon Monoxide levels (CO PPM), and sensor readings from MQ135 and MQ7 sensors.",
                contentFont
            );
            description.setSpacingAfter(20);
            document.add(description);

            // Create and style the table
            PdfPTable table = new PdfPTable(7); // 7 columns for all metrics
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths
            float[] columnWidths = {2f, 1f, 1f, 1f, 1f, 1f, 1f};
            table.setWidths(columnWidths);

            // Add headers with background color
            PdfPCell[] headers = {
                new PdfPCell(new Phrase("Time Period", headerFont)),
                new PdfPCell(new Phrase("AQI", headerFont)),
                new PdfPCell(new Phrase("CO PPM", headerFont)),
                new PdfPCell(new Phrase("MQ135", headerFont)),
                new PdfPCell(new Phrase("MQ135R", headerFont)),
                new PdfPCell(new Phrase("MQ7", headerFont)),
                new PdfPCell(new Phrase("MQ7R", headerFont))
            };

            for (PdfPCell header : headers) {
                header.setBackgroundColor(new Color(63, 81, 181));
                header.setPadding(5);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(header);
            }

            // Add data
            Map<String, Map<String, Double>> monthlyData = emissionRecordService.calculateIntervalAverageEmissionRates(
                    OffsetDateTime.now().minusMonths(6),
                    OffsetDateTime.now(),
                    "month"
            );

            monthlyData.forEach((month, data) -> {
                addCell(table, month, contentFont);
                addCell(table, String.format("%.2f", data.get("aqi")), contentFont);
                addCell(table, String.format("%.2f", data.get("coPpm")), contentFont);
                addCell(table, String.format("%.2f", data.get("mq135")), contentFont);
                addCell(table, String.format("%.2f", data.get("mq135R")), contentFont);
                addCell(table, String.format("%.2f", data.get("mq7")), contentFont);
                addCell(table, String.format("%.2f", data.get("mq7R")), contentFont);
            });

            document.add(table);

            // Add summary section
            addSummarySection(document, monthlyData);

            document.close();

            Report report = new Report();
            report.setFilename(fileName);
            report.setFileType("pdf");
            report.setReportType("monthly_emissions");
            return reportRepository.save(report);

        } catch (FileNotFoundException | DocumentException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addCell(PdfPTable table, String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addSummarySection(Document document, Map<String, Map<String, Double>> monthlyData) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph summaryTitle = new Paragraph("Summary Analysis", sectionFont);
        summaryTitle.setSpacingBefore(20);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);

        // Calculate averages across all months
        double avgAqi = 0, avgCoPpm = 0, avgMq135 = 0, avgMq135R = 0, avgMq7 = 0, avgMq7R = 0;
        int count = monthlyData.size();

        for (Map<String, Double> data : monthlyData.values()) {
            avgAqi += data.get("aqi");
            avgCoPpm += data.get("coPpm");
            avgMq135 += data.get("mq135");
            avgMq135R += data.get("mq135R");
            avgMq7 += data.get("mq7");
            avgMq7R += data.get("mq7R");
        }

        if (count > 0) {
            avgAqi /= count;
            avgCoPpm /= count;
            avgMq135 /= count;
            avgMq135R /= count;
            avgMq7 /= count;
            avgMq7R /= count;
        }

        // Add summary statistics
        document.add(new Paragraph(String.format("Average AQI: %.2f", avgAqi), contentFont));
        document.add(new Paragraph(String.format("Average CO PPM: %.2f", avgCoPpm), contentFont));
        document.add(new Paragraph(String.format("Average MQ135 Reading: %.2f", avgMq135), contentFont));
        document.add(new Paragraph(String.format("Average MQ135 Resistance: %.2f", avgMq135R), contentFont));
        document.add(new Paragraph(String.format("Average MQ7 Reading: %.2f", avgMq7), contentFont));
        document.add(new Paragraph(String.format("Average MQ7 Resistance: %.2f", avgMq7R), contentFont));
    }
}
