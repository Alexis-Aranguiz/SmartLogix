package com.smartlogix.ms_inventario.service;

public interface StockObserver {

    /**
     *
      @param idProducto     //ID del producto afectado
      @param nombreProducto //Nombre del producto
      @param tipoAlerta     //BAJO, SOBRESTOCK, AGOTADO, NORMAL
      @param stockNuevo     //Nuevo valor de stock
     */
    void update(Long idProducto, String nombreProducto,
                String tipoAlerta, Integer stockNuevo);
}