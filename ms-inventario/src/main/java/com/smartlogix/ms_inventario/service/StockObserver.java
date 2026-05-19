package com.smartlogix.ms_inventario.service;

public interface StockObserver {

    /**
     * update() 
     * El Publisher llama este método en cada suscriptor registrado
     * cuando el stock cambia.
     *
      @param idProducto     ID del producto afectado
      @param nombreProducto Nombre del producto
      @param tipoAlerta     BAJO, SOBRESTOCK, AGOTADO, NORMAL
      @param stockNuevo     Nuevo valor de stock
     */
    void update(Long idProducto, String nombreProducto,
                String tipoAlerta, Integer stockNuevo);
}