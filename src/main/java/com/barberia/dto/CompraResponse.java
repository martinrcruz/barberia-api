package com.barberia.dto;

import com.barberia.entity.Compra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {

    private Long id;
    private String numeroCompra;
    private LocalDateTime fechaCompra;
    private ProveedorResponse proveedor;
    private SucursalBasicResponse sucursal;
    private Double subtotal;
    private Double iva;
    private Double total;
    private String numeroDocumento;
    private Compra.TipoDocumento tipoDocumento;
    private String observaciones;
    private List<DetalleCompraResponse> detalles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleCompraResponse {
        private Long id;
        private String tipoItem;
        private ProductoResponse producto;
        private InsumoResponse insumo;
        private String descripcion;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}

