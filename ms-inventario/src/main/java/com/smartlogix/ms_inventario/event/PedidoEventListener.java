package com.smartlogix.ms_inventario.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.ms_inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PedidoEventListener {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "pedidos-topic", groupId = "inventario-group")
    public void onPedidoCreado(String mensaje) {
        try {
            System.out.println("📥 SAGA: Evento pedido recibido");

            Map<String, Object> evento = objectMapper.readValue(mensaje, Map.class);
            List<Map<String, Object>> items =
                (List<Map<String, Object>>) evento.get("items");

            if (items == null || items.isEmpty()) {
                System.out.println("⚠️ Pedido sin items, ignorando");
                return;
            }

            items.forEach(item -> {
                try {
                    Long productoId = Long.valueOf(item.get("productoId").toString());
                    Integer cantidad = Integer.valueOf(item.get("cantidad").toString());
                    productoService.descontarStock(productoId, cantidad);
                    System.out.println("✅ Stock descontado: producto "
                            + productoId + " -" + cantidad);
                } catch (Exception e) {
                    System.err.println("❌ Error al descontar stock: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Error procesando evento: " + e.getMessage());
            // No relanzar la excepción — evita el bucle infinito
        }
    }
}