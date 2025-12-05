package com.barberia.service.impl;

import com.barberia.dto.RegistroContableResponse;
import com.barberia.service.ExportService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ExportServiceImpl implements ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String FONT_NAME = "Arial";

    @Override
    public ByteArrayOutputStream exportarContabilidadExcel(List<RegistroContableResponse> registros,
                                                             LocalDateTime fechaInicio,
                                                             LocalDateTime fechaFin,
                                                             String nombreSucursal) throws IOException {
        log.info("Exportando {} registros contables a Excel", registros.size());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Registros Contables");

            // Estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dateStyle = crearEstiloFecha(workbook);
            CellStyle moneyStyle = crearEstiloMoneda(workbook);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE CONTABILIDAD - " + (nombreSucursal != null ? nombreSucursal : "TODAS LAS SUCURSALES"));
            titleCell.setCellStyle(crearEstiloTitulo(workbook));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));

            // Fechas
            Row dateRangeRow = sheet.createRow(1);
            Cell dateRangeCell = dateRangeRow.createCell(0);
            String rangoFechas = "Período: ";
            if (fechaInicio != null) {
                rangoFechas += fechaInicio.format(DATE_FORMATTER);
            } else {
                rangoFechas += "Inicio";
            }
            rangoFechas += " - ";
            if (fechaFin != null) {
                rangoFechas += fechaFin.format(DATE_FORMATTER);
            } else {
                rangoFechas += "Actual";
            }
            dateRangeCell.setCellValue(rangoFechas);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 6));

            // Fila vacía
            sheet.createRow(2);

            // Encabezados
            Row headerRow = sheet.createRow(3);
            String[] columnas = {"ID", "Fecha", "Tipo Movimiento", "Concepto", "Monto", "Sucursal", "Referencia"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 4;
            BigDecimal totalIngresos = BigDecimal.ZERO;
            BigDecimal totalEgresos = BigDecimal.ZERO;

            for (RegistroContableResponse registro : registros) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(registro.getId());

                Cell dateCell = row.createCell(1);
                if (registro.getFechaRegistro() != null) {
                    dateCell.setCellValue(registro.getFechaRegistro().format(DATE_FORMATTER));
                }
                dateCell.setCellStyle(dateStyle);

                row.createCell(2).setCellValue(registro.getTipoMovimiento());
                row.createCell(3).setCellValue(registro.getConcepto() != null ? registro.getConcepto() : "");

                Cell moneyCell = row.createCell(4);
                if (registro.getMonto() != null) {
                    moneyCell.setCellValue(registro.getMonto().doubleValue());
                    moneyCell.setCellStyle(moneyStyle);

                    if ("INGRESO".equals(registro.getTipoMovimiento())) {
                        totalIngresos = totalIngresos.add(registro.getMonto());
                    } else if ("EGRESO".equals(registro.getTipoMovimiento())) {
                        totalEgresos = totalEgresos.add(registro.getMonto());
                    }
                }

                row.createCell(5).setCellValue(registro.getNombreSucursal() != null ? registro.getNombreSucursal() : "");
                row.createCell(6).setCellValue(registro.getReferenciaMovimiento() != null ? registro.getReferenciaMovimiento() : "");
            }

            // Totales
            sheet.createRow(rowNum++); // Fila vacía
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = totalRow.createCell(3);
            totalLabelCell.setCellValue("TOTALES:");
            totalLabelCell.setCellStyle(headerStyle);

            Row ingresosRow = sheet.createRow(rowNum++);
            Cell ingresosLabelCell = ingresosRow.createCell(3);
            ingresosLabelCell.setCellValue("Total Ingresos:");
            ingresosLabelCell.setCellStyle(crearEstiloSubtotal(workbook));

            Cell ingresosValueCell = ingresosRow.createCell(4);
            ingresosValueCell.setCellValue(totalIngresos.doubleValue());
            ingresosValueCell.setCellStyle(moneyStyle);

            Row egresosRow = sheet.createRow(rowNum++);
            Cell egresosLabelCell = egresosRow.createCell(3);
            egresosLabelCell.setCellValue("Total Egresos:");
            egresosLabelCell.setCellStyle(crearEstiloSubtotal(workbook));

            Cell egresosValueCell = egresosRow.createCell(4);
            egresosValueCell.setCellValue(totalEgresos.doubleValue());
            egresosValueCell.setCellStyle(moneyStyle);

            Row saldoRow = sheet.createRow(rowNum++);
            Cell saldoLabelCell = saldoRow.createCell(3);
            saldoLabelCell.setCellValue("Saldo Neto:");
            saldoLabelCell.setCellStyle(headerStyle);

            Cell saldoValueCell = saldoRow.createCell(4);
            BigDecimal saldoNeto = totalIngresos.subtract(totalEgresos);
            saldoValueCell.setCellValue(saldoNeto.doubleValue());
            CellStyle saldoStyle = crearEstiloMoneda(workbook);
            Font saldoFont = workbook.createFont();
            saldoFont.setBold(true);
            saldoStyle.setFont(saldoFont);
            saldoValueCell.setCellStyle(saldoStyle);

            // Ajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir a ByteArray
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("Exportación a Excel completada exitosamente");

            return outputStream;
        }
    }

    @Override
    public ByteArrayOutputStream exportarContabilidadPDF(List<RegistroContableResponse> registros,
                                                          LocalDateTime fechaInicio,
                                                          LocalDateTime fechaFin,
                                                          String nombreSucursal) throws IOException {
        log.info("Exportando {} registros contables a PDF", registros.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Generar HTML para el PDF
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; }");
        html.append("h1 { color: #08415C; text-align: center; }");
        html.append("h2 { color: #EF6461; text-align: center; font-size: 14px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th { background-color: #08415C; color: white; padding: 10px; text-align: left; }");
        html.append("td { border: 1px solid #ddd; padding: 8px; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append(".total { font-weight: bold; background-color: #D5DFE5; }");
        html.append(".money { text-align: right; }");
        html.append("</style></head><body>");

        // Título
        html.append("<h1>REPORTE DE CONTABILIDAD</h1>");
        html.append("<h2>").append(nombreSucursal != null ? nombreSucursal : "TODAS LAS SUCURSALES").append("</h2>");

        // Período
        html.append("<p style='text-align: center;'>Período: ");
        if (fechaInicio != null) {
            html.append(fechaInicio.format(DATE_FORMATTER));
        } else {
            html.append("Inicio");
        }
        html.append(" - ");
        if (fechaFin != null) {
            html.append(fechaFin.format(DATE_FORMATTER));
        } else {
            html.append("Actual");
        }
        html.append("</p>");

        // Tabla
        html.append("<table>");
        html.append("<tr><th>Fecha</th><th>Tipo</th><th>Concepto</th><th>Monto</th><th>Sucursal</th></tr>");

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;

        for (RegistroContableResponse registro : registros) {
            html.append("<tr>");
            html.append("<td>").append(registro.getFechaRegistro() != null ? registro.getFechaRegistro().format(DATE_FORMATTER) : "").append("</td>");
            html.append("<td>").append(registro.getTipoMovimiento()).append("</td>");
            html.append("<td>").append(registro.getConcepto() != null ? registro.getConcepto() : "").append("</td>");
            html.append("<td class='money'>$").append(registro.getMonto() != null ? String.format("%,.2f", registro.getMonto()) : "0.00").append("</td>");
            html.append("<td>").append(registro.getNombreSucursal() != null ? registro.getNombreSucursal() : "").append("</td>");
            html.append("</tr>");

            if (registro.getMonto() != null) {
                if ("INGRESO".equals(registro.getTipoMovimiento())) {
                    totalIngresos = totalIngresos.add(registro.getMonto());
                } else if ("EGRESO".equals(registro.getTipoMovimiento())) {
                    totalEgresos = totalEgresos.add(registro.getMonto());
                }
            }
        }

        // Totales
        html.append("<tr class='total'><td colspan='3'>Total Ingresos:</td><td class='money'>$").append(String.format("%,.2f", totalIngresos)).append("</td><td></td></tr>");
        html.append("<tr class='total'><td colspan='3'>Total Egresos:</td><td class='money'>$").append(String.format("%,.2f", totalEgresos)).append("</td><td></td></tr>");
        
        BigDecimal saldoNeto = totalIngresos.subtract(totalEgresos);
        html.append("<tr class='total' style='background-color: #08415C; color: white;'><td colspan='3'>Saldo Neto:</td><td class='money'>$").append(String.format("%,.2f", saldoNeto)).append("</td><td></td></tr>");

        html.append("</table>");
        html.append("<p style='text-align: center; margin-top: 30px; font-size: 10px; color: #666;'>Generado el ")
            .append(LocalDateTime.now().format(DATE_FORMATTER)).append(" - BarberiaApp</p>");
        html.append("</body></html>");

        // Convertir HTML a PDF usando OpenHTMLToPDF
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html.toString(), null);
            builder.toStream(outputStream);
            builder.run();
            log.info("Exportación a PDF completada exitosamente");
        } catch (Exception e) {
            log.error("Error al convertir HTML a PDF: {}", e.getMessage(), e);
            throw new IOException("Error al generar PDF: " + e.getMessage(), e);
        }

        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportarListaExcel(List<?> datos, String nombreHoja, String titulo) throws IOException {
        log.info("Exportando lista genérica a Excel: {}", titulo);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(nombreHoja);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(titulo);
            titleCell.setCellStyle(crearEstiloTitulo(workbook));

            // Nota: Implementación genérica básica
            // En producción, usar reflexión para obtener campos del objeto

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream;
        }
    }

    @Override
    public ByteArrayOutputStream generarReporteFinancieroPDF(Object resumenFinanciero, String nombreSucursal) throws IOException {
        log.info("Generando reporte financiero PDF");
        
        // Implementación similar a exportarContabilidadPDF
        // pero con formato específico para resumen financiero
        
        return new ByteArrayOutputStream();
    }

    // Métodos auxiliares para estilos Excel

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloFecha(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloMoneda(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloSubtotal(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}

