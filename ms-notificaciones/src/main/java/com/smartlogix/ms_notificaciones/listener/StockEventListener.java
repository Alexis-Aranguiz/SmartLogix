package com.smartlogix.ms_notificaciones.listener;

import com.smartlogix.ms_notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockEventListener {

    @Autowired
    private NotificacionService notificacionService;

    public void onStockActualizado(Long idProducto, String nombreProducto,
                                    String tipoAlerta, Integer stockNuevo) {
        System.out.println(" Observer recibió evento de stock: " +
                nombreProducto + " [" + tipoAlerta + "]");

        String asunto = "";
        String mensaje = "";
        String destinatario = "encargado@smartlogix.cl";

        switch (tipoAlerta) {
            case "AGOTADO" -> {
                asunto = " Producto agotado: " + nombreProducto;
                mensaje = "El producto " + nombreProducto +
                        " se ha agotado. Por favor reabastecer urgente.";
            }
            case "BAJO" -> {
                asunto = " Stock bajo: " + nombreProducto;
                mensaje = "El producto " + nombreProducto +
                        " tiene stock bajo (actual: " + stockNuevo + "). Considerar reabastecimiento.";
            }
            case "SOBRESTOCK" -> {
                asunto = " Sobrestock: " + nombreProducto;
                mensaje = "El producto " + nombreProducto +
                        " tiene sobrestock (actual: " + stockNuevo + "). Pausar compras.";
            }
        }

        if (!asunto.isEmpty()) {
            notificacionService.enviarNotificacion("EMAIL", destinatario, asunto, mensaje);
        }
    }
}