package com.smartlogix.ms_inventario.service;

/**
 * PATRÓN OBSERVER — Interfaz Subscriber (GoF)
 * ══════════════════════════════════════════
 * Equivale a «interface» EventListener del diagrama del profe.
 * Todo Observer concreto debe implementar el método update().
 *
 * Publisher:  ProductoService (notifica cambios de stock)
 * Subscriber: Cualquier clase que implemente esta interfaz
 */
public interface StockObserver {

    /**
     * update() del GoF
     * El Publisher llama este método en cada suscriptor registrado
     * cuando el stock cambia.
     *
     * @param idProducto     ID del producto afectado
     * @param nombreProducto Nombre del producto
     * @param tipoAlerta     BAJO, SOBRESTOCK, AGOTADO, NORMA
     * @param stockNuevo     Nuevo valor de stock
     */
    void update(Long idProducto, String nombreProducto,
                String tipoAlerta, Integer stockNuevo);
}