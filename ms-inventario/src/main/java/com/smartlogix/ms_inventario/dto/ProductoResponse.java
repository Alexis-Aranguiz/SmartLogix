package com.smartlogix.ms_inventario.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String sku;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String estadoStock; // NORMAL, BAJO, SOBRESTOCK, AGOTADO
    private Boolean activo;
}