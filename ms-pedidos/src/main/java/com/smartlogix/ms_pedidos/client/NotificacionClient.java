package com.smartlogix.ms_pedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-notificaciones", url = "${app.notificaciones.url:http://localhost:8085}")
public interface NotificacionClient {

    @PostMapping("/api/notificaciones/pedido-evento")
    void enviarEventoPedido(@RequestBody Map<String, Object> evento);
}
