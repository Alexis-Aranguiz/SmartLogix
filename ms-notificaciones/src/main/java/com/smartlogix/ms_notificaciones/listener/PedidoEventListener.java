package com.smartlogix.ms_notificaciones.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.ms_notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PedidoEventListener {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "pedido-confirmado-topic", groupId = "notificaciones-group")
    public void onPedidoConfirmado(String mensaje) {
        try {
            Map<String, Object> evento = objectMapper.readValue(mensaje, Map.class);
            String email = evento.get("clienteEmail").toString();
            String numeroPedido = evento.get("numeroPedido").toString();
            Object total = evento.get("total");

            System.out.println("👁️ Observer: Pedido confirmado recibido #" + numeroPedido);

            notificacionService.enviarNotificacion(
                "EMAIL",
                email,
                "✅ Pedido #" + numeroPedido + " confirmado",
                "Tu pedido #" + numeroPedido + " ha sido confirmado. Total: $" + total
            );
        } catch (Exception e) {
            System.err.println("❌ Error procesando pedido confirmado: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "pedido-cancelado-topic", groupId = "notificaciones-group")
    public void onPedidoCancelado(String mensaje) {
        try {
            Map<String, Object> evento = objectMapper.readValue(mensaje, Map.class);
            String numeroPedido = evento.get("numeroPedido").toString();
            String motivo = evento.get("motivo") != null
                    ? evento.get("motivo").toString()
                    : "Sin especificar";

            System.out.println("👁️ Observer: Pedido cancelado recibido #" + numeroPedido);

            // Buscar email del pedido — por ahora notificamos al encargado
            // Cuando tengamos integración completa, vendría en el evento
            String email = evento.get("clienteEmail") != null
        ? evento.get("clienteEmail").toString()
        : "encargado@smartlogix.cl";

            notificacionService.enviarNotificacion(
                "EMAIL",
                email,
                "❌ Pedido #" + numeroPedido + " cancelado",
                "Tu pedido #" + numeroPedido + " fue cancelado. Motivo: " + motivo +
                ". Si tienes dudas contáctanos."
            );

        } catch (Exception e) {
            System.err.println("❌ Error procesando pedido cancelado: " + e.getMessage());
        }
    }
}