package com.smartlogix.ms_envios.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnvioEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publicarEnvioExitoso(Long pedidoId, String tracking) {
        System.out.println(" 🟢 SAGA: Publicando evento de envío exitoso para pedido: " + pedidoId);
        // Aquí mandaríamos un objeto JSON real en un escenario completo
        kafkaTemplate.send("envios-procesados-topic", "Pedido: " + pedidoId + " | Tracking: " + tracking);
    }

    public void publicarEnvioFallido(Long pedidoId, String razon) {
        System.out.println(" 🔴 SAGA: Publicando evento de compensación (fallo) para pedido: " + pedidoId);
        kafkaTemplate.send("envios-fallidos-topic", "Pedido: " + pedidoId + " falló por: " + razon);
    }
}