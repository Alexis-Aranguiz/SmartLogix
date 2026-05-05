package com.smartlogix.ms_inventario.event;

import org.springframework.context.ApplicationEvent;


public class StockActualizadoEvent extends ApplicationEvent {

    private final Long idProducto;
    private final String skuProducto;
    private final String nombreProducto;
    private final Integer stockAnterior;
    private final Integer stockNuevo;
    private final String tipoAlerta; 

    public StockActualizadoEvent(Object source, Long idProducto, String skuProducto,
                                  String nombreProducto, Integer stockAnterior,
                                  Integer stockNuevo, String tipoAlerta) {
        super(source);
        this.idProducto = idProducto;
        this.skuProducto = skuProducto;
        this.nombreProducto = nombreProducto;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.tipoAlerta = tipoAlerta;
    }

    public Long getIdProducto() { return idProducto; }
    public String getSkuProducto() { return skuProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public Integer getStockAnterior() { return stockAnterior; }
    public Integer getStockNuevo() { return stockNuevo; }
    public String getTipoAlerta() { return tipoAlerta; }
}