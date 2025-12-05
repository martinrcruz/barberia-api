package com.barberia.service;

import com.barberia.entity.Venta;

import java.io.ByteArrayOutputStream;

public interface PdfService {
    ByteArrayOutputStream generarComprobante(Long ventaId);
    String guardarComprobante(Long ventaId);
}

