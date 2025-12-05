package com.barberia.service;

import com.barberia.dto.RegistroContableResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ExportService {

    /**
     * Exporta registros contables a Excel
     */
    ByteArrayOutputStream exportarContabilidadExcel(List<RegistroContableResponse> registros, 
                                                      LocalDateTime fechaInicio, 
                                                      LocalDateTime fechaFin,
                                                      String nombreSucursal) throws IOException;

    /**
     * Exporta registros contables a PDF
     */
    ByteArrayOutputStream exportarContabilidadPDF(List<RegistroContableResponse> registros, 
                                                   LocalDateTime fechaInicio, 
                                                   LocalDateTime fechaFin,
                                                   String nombreSucursal) throws IOException;

    /**
     * Exporta cualquier lista de datos a Excel genérico
     */
    ByteArrayOutputStream exportarListaExcel(List<?> datos, String nombreHoja, String titulo) throws IOException;

    /**
     * Genera reporte financiero completo en PDF
     */
    ByteArrayOutputStream generarReporteFinancieroPDF(Object resumenFinanciero, 
                                                       String nombreSucursal) throws IOException;
}

