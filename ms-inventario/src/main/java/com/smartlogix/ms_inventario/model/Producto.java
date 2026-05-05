package com.smartlogix.ms_inventario.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "stock_maximo", nullable = false)
    private Integer stockMaximo;

    @Column(nullable = false)
    private Boolean activo = true;


    public boolean esStockCritico() {
        return this.stock <= this.stockMinimo;
    }

    public boolean esSobrestock() {
        return this.stock >= this.stockMaximo;
    }

    public boolean esAgotado() {
        return this.stock == 0;
    }
}