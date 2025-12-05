package com.barberia.service.impl;

import com.barberia.entity.*;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.VentaRepository;
import com.barberia.service.PdfService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Servicio de generación de comprobantes PDF usando iText7.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {

    private final VentaRepository ventaRepository;

    @Override
    @Transactional(readOnly = true)
    public ByteArrayOutputStream generarComprobante(Long ventaId) {
        log.info("Generando comprobante PDF para venta ID: {}", ventaId);

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + ventaId));

        String htmlContent = generarHtmlComprobante(venta);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // Convertir HTML a PDF usando iText7
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlContent, outputStream, properties);
            
            log.info("Comprobante PDF generado exitosamente para venta ID: {}", ventaId);
        } catch (Exception e) {
            log.error("Error al generar comprobante PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar comprobante PDF: " + e.getMessage(), e);
        }

        return outputStream;
    }

    @Override
    @Transactional
    public String guardarComprobante(Long ventaId) {
        log.info("Guardando comprobante para venta ID: {}", ventaId);

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + ventaId));

        // Generar nombre de archivo
        String nombreArchivo = String.format("comprobante_%s_%s.pdf",
                venta.getNumeroVenta(),
                venta.getFechaVenta().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

        // En producción, aquí se guardaría el archivo en el sistema de archivos o S3
        String rutaComprobante = "/documentos/comprobantes/" + nombreArchivo;

        venta.setComprobanteUrl(rutaComprobante);
        ventaRepository.save(venta);

        log.info("Comprobante guardado exitosamente en: {}", rutaComprobante);
        return rutaComprobante;
    }

    private String generarHtmlComprobante(Venta venta) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang='es'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Comprobante - ").append(venta.getNumeroVenta()).append("</title>");
        html.append(getEstilosCSS());
        html.append("</head>");
        html.append("<body>");

        // Encabezado
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>COMPROBANTE DE VENTA</h1>");
        html.append("<div class='logo-section'>");
        html.append("<p class='company-name'>BarberiaApp</p>");
        html.append("</div>");
        html.append("</div>");

        // Información de la sucursal y venta
        html.append("<div class='info-section'>");
        html.append("<div class='info-left'>");
        html.append("<h3>").append(escapeHtml(venta.getSucursal().getNombre())).append("</h3>");
        html.append("<p>").append(escapeHtml(venta.getSucursal().getDireccion() != null ? venta.getSucursal().getDireccion() : "")).append("</p>");
        html.append("<p>Teléfono: ").append(escapeHtml(venta.getSucursal().getTelefono() != null ? venta.getSucursal().getTelefono() : "")).append("</p>");
        html.append("</div>");
        html.append("<div class='info-right'>");
        html.append("<p><strong>N° Venta:</strong> ").append(escapeHtml(venta.getNumeroVenta())).append("</p>");
        html.append("<p><strong>Fecha:</strong> ").append(venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>");
        html.append("<p><strong>Atendido por:</strong> ").append(escapeHtml(venta.getTrabajador().getNombreCompleto())).append("</p>");
        if (venta.getCliente() != null) {
            html.append("<p><strong>Cliente:</strong> ").append(escapeHtml(venta.getCliente().getNombreCompleto())).append("</p>");
        }
        html.append("</div>");
        html.append("</div>");

        // Tabla de detalles
        html.append("<table class='items-table'>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>Descripción</th>");
        html.append("<th>Cantidad</th>");
        html.append("<th>Precio Unit.</th>");
        html.append("<th>Subtotal</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");

        for (DetalleVenta detalle : venta.getDetalles()) {
            html.append("<tr>");
            
            String descripcion = "";
            if (detalle.getDescripcion() != null && !detalle.getDescripcion().isEmpty()) {
                descripcion = detalle.getDescripcion();
            } else if (detalle.getTipoItem() == DetalleVenta.TipoItem.PRODUCTO && detalle.getProducto() != null) {
                descripcion = detalle.getProducto().getNombre();
            } else if (detalle.getTipoItem() == DetalleVenta.TipoItem.SERVICIO && detalle.getServicio() != null) {
                descripcion = detalle.getServicio().getNombre();
            } else {
                descripcion = detalle.getTipoItem().name();
            }
            
            html.append("<td>").append(escapeHtml(descripcion)).append("</td>");
            html.append("<td>").append(detalle.getCantidad() != null ? detalle.getCantidad() : 0).append("</td>");
            html.append("<td>$").append(String.format("%.2f", detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : 0.0)).append("</td>");
            html.append("<td>$").append(String.format("%.2f", detalle.getSubtotal() != null ? detalle.getSubtotal() : 0.0)).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody>");
        html.append("</table>");

        // Totales
        html.append("<div class='totals-section'>");
        html.append("<div class='totals'>");
        html.append("<div class='total-row'>");
        html.append("<span>Subtotal:</span>");
        html.append("<span>$").append(String.format("%.2f", venta.getSubtotal())).append("</span>");
        html.append("</div>");
        
        if (venta.getIva() != null && venta.getIva() > 0) {
            html.append("<div class='total-row'>");
            html.append("<span>IVA (19%):</span>");
            html.append("<span>$").append(String.format("%.2f", venta.getIva())).append("</span>");
            html.append("</div>");
        }
        
        html.append("<div class='total-row total-final'>");
        html.append("<span><strong>TOTAL:</strong></span>");
        html.append("<span><strong>$").append(String.format("%.2f", venta.getTotal())).append("</strong></span>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");

        // Métodos de pago (si aplica con PagoVenta en el futuro)
        if (venta.getPagos() != null && !venta.getPagos().isEmpty()) {
            html.append("<div class='payment-section'>");
            html.append("<h4>Métodos de Pago</h4>");
            for (PagoVenta pago : venta.getPagos()) {
                html.append("<p>").append(pago.getMetodoPago().getNombre()).append(": $")
                    .append(String.format("%.2f", pago.getMonto())).append("</p>");
            }
            html.append("</div>");
        }

        // Pie de página
        html.append("<div class='footer'>");
        html.append("<p>¡Gracias por su preferencia!</p>");
        html.append("<p class='small'>Este es un comprobante válido de su transacción</p>");
        html.append("</div>");

        html.append("</div>"); // cierre container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private String getEstilosCSS() {
        return "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #333; background-color: #f5f5f5; }" +
                ".container { max-width: 800px; margin: 20px auto; background-color: white; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; border-bottom: 3px solid #08415C; padding-bottom: 15px; margin-bottom: 20px; }" +
                ".header h1 { color: #08415C; font-size: 24px; margin-bottom: 10px; }" +
                ".company-name { font-size: 20px; font-weight: bold; color: #EF6461; }" +
                ".info-section { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px; padding: 15px; background-color: #D5DFE5; border-radius: 5px; }" +
                ".info-section h3 { color: #08415C; margin-bottom: 10px; }" +
                ".info-section p { margin-bottom: 5px; font-size: 14px; }" +
                ".items-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }" +
                ".items-table thead { background-color: #08415C; color: white; }" +
                ".items-table th { padding: 12px; text-align: left; font-weight: 600; }" +
                ".items-table td { padding: 10px; border-bottom: 1px solid #ddd; }" +
                ".items-table tbody tr:hover { background-color: #f9f9f9; }" +
                ".totals-section { display: flex; justify-content: flex-end; margin-bottom: 30px; }" +
                ".totals { width: 300px; }" +
                ".total-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #ddd; }" +
                ".total-final { border-top: 2px solid #08415C; border-bottom: none; font-size: 18px; color: #08415C; padding-top: 12px; }" +
                ".payment-section { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px; }" +
                ".payment-section h4 { color: #08415C; margin-bottom: 10px; }" +
                ".footer { text-align: center; padding-top: 20px; border-top: 2px solid #D5DFE5; margin-top: 30px; }" +
                ".footer p { margin-bottom: 5px; }" +
                ".footer .small { font-size: 12px; color: #666; }" +
                "@media print { body { background-color: white; } .container { box-shadow: none; margin: 0; } }" +
                "</style>";
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

