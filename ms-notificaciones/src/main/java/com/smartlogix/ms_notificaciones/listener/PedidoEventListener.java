package com.smartlogix.ms_notificaciones.listener;

import com.smartlogix.ms_notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventListener {

    @Autowired
    private NotificacionService notificacionService;

    public void onPedidoActualizado(String numeroPedido, String clienteEmail, String estado) {
        System.out.println("Observer recibió evento de pedido: " + numeroPedido + " [" + estado + "]");

        String asunto = "";
        String mensaje = "";

        switch (estado) {
            case "CREADO" -> {
                asunto = "Confirmación de Pedido #" + numeroPedido;
                mensaje = "Hola, su pedido ha sido recibido con éxito y está siendo procesado.";
            }
            case "ENVIADO" -> {
                asunto = "Pedido #" + numeroPedido + " en camino";
                mensaje = "¡Excelentes noticias! Su pedido ha sido enviado a su domicilio.";
            }
            case "ENTREGADO" -> {
                asunto = "Pedido #" + numeroPedido + " entregado";
                mensaje = "Su pedido ha sido entregado. ¡Gracias por confiar en SmartLogix!";
            }
        }

        if (!asunto.isEmpty()) {
            notificacionService.enviarNotificacion("EMAIL", clienteEmail, asunto, mensaje);
        }
    }
}
