package com.smartlogix.ms_envios.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.smartlogix.ms_envios.dto.EnvioRequest;
import com.smartlogix.ms_envios.service.EnvioService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnvioEventListener {

    private final EnvioService envioService;
    private final EnvioEventProducer eventProducer;

    @KafkaListener(topics = "pedidos-validados-topic", groupId = "envios-group")
    public void onPedidoValidado(PedidoEvent event) {
        System.out.println(" 📥 SAGA: Evento recibido de ms-pedidos. Procesando envío para pedido: " + event.getPedidoId());

        try {
            // 1. Convertimos el evento a un Request que nuestro servicio entienda
            EnvioRequest request = new EnvioRequest();
            request.setPedidoId(event.getPedidoId());
            request.setDireccionDestino(event.getDireccionDestino());

            // 2. Ejecutamos la lógica local (Base de datos + Circuit Breaker)
            var response = envioService.crearEnvio(request);

            // 3. Publicamos el éxito para que la Saga continúe
            eventProducer.publicarEnvioExitoso(response.getPedidoId(), response.getTrackingNumber());

        } catch (Exception e) {
            // 4. Si algo catastrófico pasa (ej: base de datos local caída), ejecutamos la compensación
            eventProducer.publicarEnvioFallido(event.getPedidoId(), e.getMessage());
        }
    }
}