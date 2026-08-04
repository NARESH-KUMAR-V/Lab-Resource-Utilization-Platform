package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.EquipmentUtilizationStatsDTO;
import com.labplatform.lab_platform_backend.entity.Billing;
import com.labplatform.lab_platform_backend.entity.EquipmentCertificate;
import com.labplatform.lab_platform_backend.entity.Maintenance;
import com.labplatform.lab_platform_backend.entity.SharingRequest;
import com.labplatform.lab_platform_backend.service.*;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final EquipmentUtilizationStatsService statsService;
    private final MaintenanceService maintenanceService;
    private final EquipmentCertificateService certificateService;
    private final SharingRequestService sharingRequestService;
    private final BillingService billingService;

    public ReportController(
            EquipmentUtilizationStatsService statsService,
            MaintenanceService maintenanceService,
            EquipmentCertificateService certificateService,
            SharingRequestService sharingRequestService,
            BillingService billingService) {

        this.statsService = statsService;
        this.maintenanceService = maintenanceService;
        this.certificateService = certificateService;
        this.sharingRequestService = sharingRequestService;
        this.billingService = billingService;
    }

    // ===================== UTILIZATION REPORTS =====================

    @GetMapping("/utilization/excel")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadUtilizationExcel() throws Exception {

        List<EquipmentUtilizationStatsDTO> stats = statsService.getAllStats();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Equipment Utilization");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = {"Equipment", "Laboratory", "Total Bookings", "Usage Hours",
                "Usage Days", "Utilization %", "Last Used", "Idle Days",
                "Avg/Month", "Avg/Week", "Tier"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        int rowNum = 1;
        for (EquipmentUtilizationStatsDTO s : stats) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getEquipmentName());
            row.createCell(1).setCellValue(s.getLaboratoryName() != null ? s.getLaboratoryName() : "-");
            row.createCell(2).setCellValue(s.getTotalBookings());
            row.createCell(3).setCellValue(s.getTotalUsageHours());
            row.createCell(4).setCellValue(s.getTotalUsageDays());
            row.createCell(5).setCellValue(s.getUtilizationPercentage());
            row.createCell(6).setCellValue(s.getLastUsedDate() != null ? s.getLastUsedDate() : "-");
            row.createCell(7).setCellValue(s.getIdleDays());
            row.createCell(8).setCellValue(s.getAvgUsagePerMonth());
            row.createCell(9).setCellValue(s.getAvgUsagePerWeek());
            row.createCell(10).setCellValue(s.getUtilizationTier());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=utilization_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping("/utilization/pdf")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadUtilizationPdf() throws Exception {

        List<EquipmentUtilizationStatsDTO> stats = statsService.getAllStats();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font dataFont   = FontFactory.getFont(FontFactory.HELVETICA, 8);

        document.add(new Paragraph("Equipment Utilization Report", titleFont));
        document.add(new Paragraph("Generated: " + LocalDate.now(),
                FontFactory.getFont(FontFactory.HELVETICA, 9)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        String[] cols = {"Equipment", "Lab", "Bookings", "Hours", "Days", "Util %", "Idle Days", "Tier"};
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new Color(0, 51, 102));
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (EquipmentUtilizationStatsDTO s : stats) {
            table.addCell(new Phrase(s.getEquipmentName(), dataFont));
            table.addCell(new Phrase(s.getLaboratoryName() != null ? s.getLaboratoryName() : "-", dataFont));
            table.addCell(new Phrase(String.valueOf(s.getTotalBookings()), dataFont));
            table.addCell(new Phrase(String.format("%.1f", s.getTotalUsageHours()), dataFont));
            table.addCell(new Phrase(String.valueOf(s.getTotalUsageDays()), dataFont));
            table.addCell(new Phrase(String.format("%.1f%%", s.getUtilizationPercentage()), dataFont));
            table.addCell(new Phrase(String.valueOf(s.getIdleDays()), dataFont));
            table.addCell(new Phrase(s.getUtilizationTier(), dataFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=utilization_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    // ===================== MAINTENANCE REPORTS =====================

    @GetMapping("/maintenance/excel")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadMaintenanceExcel() throws Exception {

        List<Maintenance> records = maintenanceService.getAllMaintenanceRecords();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Maintenance History");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font hf = workbook.createFont();
        hf.setColor(IndexedColors.WHITE.getIndex());
        hf.setBold(true);
        headerStyle.setFont(hf);

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = {"ID", "Equipment", "Laboratory", "Technician", "Type",
                "Date", "Status", "Cost", "Auto Generated", "Notes"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        int rowNum = 1;
        for (Maintenance m : records) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getId());
            row.createCell(1).setCellValue(m.getEquipment().getName());
            row.createCell(2).setCellValue(
                    m.getEquipment().getLaboratory() != null
                            ? m.getEquipment().getLaboratory().getName() : "-");
            row.createCell(3).setCellValue(
                    m.getTechnician() != null ? m.getTechnician().getName() : "-");
            row.createCell(4).setCellValue(
                    m.getMaintenanceType() != null ? m.getMaintenanceType().name() : "-");
            row.createCell(5).setCellValue(m.getMaintenanceDate().toString());
            row.createCell(6).setCellValue(m.getStatus().name());
            row.createCell(7).setCellValue(m.getMaintenanceCost() != null ? m.getMaintenanceCost() : 0.0);
            row.createCell(8).setCellValue(Boolean.TRUE.equals(m.getIsAutoGenerated()) ? "Yes" : "No");
            row.createCell(9).setCellValue(m.getCompletionNotes() != null ? m.getCompletionNotes() : "-");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=maintenance_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping("/maintenance/pdf")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadMaintenancePdf() throws Exception {

        List<Maintenance> records = maintenanceService.getAllMaintenanceRecords();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font dataFont   = FontFactory.getFont(FontFactory.HELVETICA, 8);

        document.add(new Paragraph("Maintenance History Report", titleFont));
        document.add(new Paragraph("Generated: " + LocalDate.now(),
                FontFactory.getFont(FontFactory.HELVETICA, 9)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        String[] cols = {"Equipment", "Lab", "Technician", "Type", "Date", "Status", "Cost", "Auto"};
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new Color(0, 51, 102));
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (Maintenance m : records) {
            table.addCell(new Phrase(m.getEquipment().getName(), dataFont));
            table.addCell(new Phrase(
                    m.getEquipment().getLaboratory() != null
                            ? m.getEquipment().getLaboratory().getName() : "-", dataFont));
            table.addCell(new Phrase(
                    m.getTechnician() != null ? m.getTechnician().getName() : "-", dataFont));
            table.addCell(new Phrase(
                    m.getMaintenanceType() != null ? m.getMaintenanceType().name() : "-", dataFont));
            table.addCell(new Phrase(m.getMaintenanceDate().toString(), dataFont));
            table.addCell(new Phrase(m.getStatus().name(), dataFont));
            table.addCell(new Phrase(
                    String.format("%.2f", m.getMaintenanceCost() != null ? m.getMaintenanceCost() : 0.0), dataFont));
            table.addCell(new Phrase(
                    Boolean.TRUE.equals(m.getIsAutoGenerated()) ? "Yes" : "No", dataFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=maintenance_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    // ===================== SHARING REPORTS =====================

    @GetMapping("/sharing/excel")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadSharingExcel() throws Exception {

        List<SharingRequest> records = sharingRequestService.getAllRequests();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inter-Institution Sharing");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font hf = workbook.createFont();
        hf.setColor(IndexedColors.WHITE.getIndex());
        hf.setBold(true);
        headerStyle.setFont(hf);

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = {"ID", "Equipment", "Requester Name", "Requester Email",
                "Requesting Institution", "Request Date", "Status", "Purpose"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5500);
        }

        int rowNum = 1;
        for (SharingRequest s : records) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(s.getEquipment().getName());
            row.createCell(2).setCellValue(s.getRequester() != null ? s.getRequester().getName() : "-");
            row.createCell(3).setCellValue(s.getRequester() != null ? s.getRequester().getEmail() : "-");
            row.createCell(4).setCellValue(s.getRequestingInstitution() != null ? s.getRequestingInstitution() : "-");
            row.createCell(5).setCellValue(s.getRequestDate() != null ? s.getRequestDate().toString() : "-");
            row.createCell(6).setCellValue(s.getStatus());
            row.createCell(7).setCellValue(s.getPurpose() != null ? s.getPurpose() : "-");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sharing_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping("/sharing/pdf")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadSharingPdf() throws Exception {

        List<SharingRequest> records = sharingRequestService.getAllRequests();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font dataFont   = FontFactory.getFont(FontFactory.HELVETICA, 8);

        document.add(new Paragraph("Inter-Institution Resource Sharing Report", titleFont));
        document.add(new Paragraph("Generated: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);

        String[] cols = {"Equipment", "Requester", "Institution", "Date", "Status", "Purpose", "ID"};
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new Color(0, 51, 102));
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (SharingRequest s : records) {
            table.addCell(new Phrase(s.getEquipment().getName(), dataFont));
            table.addCell(new Phrase(s.getRequester() != null ? s.getRequester().getName() : "-", dataFont));
            table.addCell(new Phrase(s.getRequestingInstitution() != null ? s.getRequestingInstitution() : "-", dataFont));
            table.addCell(new Phrase(s.getRequestDate() != null ? s.getRequestDate().toString() : "-", dataFont));
            table.addCell(new Phrase(s.getStatus(), dataFont));
            table.addCell(new Phrase(s.getPurpose() != null ? s.getPurpose() : "-", dataFont));
            table.addCell(new Phrase(String.valueOf(s.getId()), dataFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sharing_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    // ===================== COST & BILLING REPORTS =====================

    @GetMapping("/cost/excel")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadCostExcel() throws Exception {

        List<Billing> records = billingService.getAllBillings();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cost & Billing Report");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font hf = workbook.createFont();
        hf.setColor(IndexedColors.WHITE.getIndex());
        hf.setBold(true);
        headerStyle.setFont(hf);

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = {"Invoice No.", "Equipment", "User Name", "Department",
                "Institution", "Usage Days", "Est. Cost (₹)", "Sharing Fee (₹)", "Total (₹)", "Status", "Date"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5500);
        }

        int rowNum = 1;
        for (Billing b : records) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(b.getInvoiceNumber());
            row.createCell(1).setCellValue(b.getEquipment() != null ? b.getEquipment().getName() : "-");
            row.createCell(2).setCellValue(b.getUser() != null ? b.getUser().getName() : "-");
            row.createCell(3).setCellValue(b.getDepartment() != null ? b.getDepartment() : "-");
            row.createCell(4).setCellValue(b.getInstitution() != null ? b.getInstitution().getName() : "-");
            row.createCell(5).setCellValue(b.getUsageDays() != null ? b.getUsageDays() : 0.0);
            row.createCell(6).setCellValue(b.getEstimatedCost() != null ? b.getEstimatedCost() : 0.0);
            row.createCell(7).setCellValue(b.getInterInstitutionFee() != null ? b.getInterInstitutionFee() : 0.0);
            row.createCell(8).setCellValue(b.getTotalAmount() != null ? b.getTotalAmount() : 0.0);
            row.createCell(9).setCellValue(b.getBillingStatus().name());
            row.createCell(10).setCellValue(b.getInvoiceDate() != null ? b.getInvoiceDate().toString() : "-");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_billing_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping("/cost/pdf")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadCostPdf() throws Exception {

        List<Billing> records = billingService.getAllBillings();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font dataFont   = FontFactory.getFont(FontFactory.HELVETICA, 8);

        document.add(new Paragraph("Academic Cost Allocation & Billing Report", titleFont));
        document.add(new Paragraph("Generated: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        String[] cols = {"Invoice No", "Equipment", "User", "Department", "Est Cost", "Sharing Fee", "Total (₹)", "Status"};
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new Color(0, 51, 102));
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (Billing b : records) {
            table.addCell(new Phrase(b.getInvoiceNumber(), dataFont));
            table.addCell(new Phrase(b.getEquipment() != null ? b.getEquipment().getName() : "-", dataFont));
            table.addCell(new Phrase(b.getUser() != null ? b.getUser().getName() : "-", dataFont));
            table.addCell(new Phrase(b.getDepartment() != null ? b.getDepartment() : "-", dataFont));
            table.addCell(new Phrase(String.format("₹%.2f", b.getEstimatedCost() != null ? b.getEstimatedCost() : 0.0), dataFont));
            table.addCell(new Phrase(String.format("₹%.2f", b.getInterInstitutionFee() != null ? b.getInterInstitutionFee() : 0.0), dataFont));
            table.addCell(new Phrase(String.format("₹%.2f", b.getTotalAmount() != null ? b.getTotalAmount() : 0.0), dataFont));
            table.addCell(new Phrase(b.getBillingStatus().name(), dataFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_billing_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    // ===================== CERTIFICATE REPORTS =====================

    @GetMapping("/certificates/excel")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadCertificatesExcel() throws Exception {

        List<EquipmentCertificate> records = certificateService.getAllCertificates();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Certificate Report");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font hf = workbook.createFont();
        hf.setColor(IndexedColors.WHITE.getIndex());
        hf.setBold(true);
        headerStyle.setFont(hf);

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = {"Equipment", "Certificate Name", "Certificate No.", "Issue Date",
                "Expiry Date", "Issued By", "Status", "Remarks"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5500);
        }

        int rowNum = 1;
        for (EquipmentCertificate c : records) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getEquipment().getName());
            row.createCell(1).setCellValue(c.getCertificateName());
            row.createCell(2).setCellValue(c.getCertificateNumber() != null ? c.getCertificateNumber() : "-");
            row.createCell(3).setCellValue(c.getIssueDate() != null ? c.getIssueDate().toString() : "-");
            row.createCell(4).setCellValue(c.getExpiryDate().toString());
            row.createCell(5).setCellValue(c.getIssuedBy() != null ? c.getIssuedBy() : "-");
            row.createCell(6).setCellValue(c.getStatus().name());
            row.createCell(7).setCellValue(c.getRemarks() != null ? c.getRemarks() : "-");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificates_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping("/certificates/pdf")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<byte[]> downloadCertificatesPdf() throws Exception {

        List<EquipmentCertificate> records = certificateService.getAllCertificates();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font dataFont   = FontFactory.getFont(FontFactory.HELVETICA, 8);

        document.add(new Paragraph("Equipment Calibration & Certification Report", titleFont));
        document.add(new Paragraph("Generated: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);

        String[] cols = {"Equipment", "Certificate", "Cert No", "Issued By", "Issue Date", "Expiry Date", "Status"};
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new Color(0, 51, 102));
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (EquipmentCertificate c : records) {
            table.addCell(new Phrase(c.getEquipment().getName(), dataFont));
            table.addCell(new Phrase(c.getCertificateName(), dataFont));
            table.addCell(new Phrase(c.getCertificateNumber() != null ? c.getCertificateNumber() : "-", dataFont));
            table.addCell(new Phrase(c.getIssuedBy() != null ? c.getIssuedBy() : "-", dataFont));
            table.addCell(new Phrase(c.getIssueDate() != null ? c.getIssueDate().toString() : "-", dataFont));
            table.addCell(new Phrase(c.getExpiryDate().toString(), dataFont));
            table.addCell(new Phrase(c.getStatus().name(), dataFont));
        }

        document.add(table);
        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificates_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }
}
