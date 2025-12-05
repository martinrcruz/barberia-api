package com.barberia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "variantes_producto")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class VarianteProducto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre; // Ej: "Talla M", "250ml", "Rojo"

    @Column(name = "sku", length = 50, unique = true)
    private String sku; // Código único de la variante

    @Column(name = "precio_venta")
    private Double precioVenta; // Si null, usa el precio del producto padre

    @Column(name = "precio_costo")
    private Double precioCosto;

    @Column(name = "stock_actual")
    private Integer stockActual = 0;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;

    @Column(name = "atributos", columnDefinition = "TEXT")
    private String atributos; // JSON con atributos adicionales: {"talla": "M", "color": "Azul"}

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "orden")
    private Integer orden = 0; // Para ordenar las variantes en el frontend

    /**
     * Obtiene el precio de venta efectivo de la variante.
     * Si la variante tiene precio propio, lo usa; si no, usa el del producto padre.
     */
    public Double getPrecioVentaEfectivo() {
        return precioVenta != null ? precioVenta : (producto != null ? producto.getPrecioVenta() : 0.0);
    }

    /**
     * Obtiene el precio de costo efectivo de la variante.
     */
    public Double getPrecioCostoEfectivo() {
        return precioCosto != null ? precioCosto : (producto != null ? producto.getPrecioCosto() : 0.0);
    }
}

