package com.smartlogix.ms_envios.client;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.smartlogix.ms_envios.dto.EnvioRequest;
import com.smartlogix.ms_envios.dto.EnvioResponse;

@Component
public class EnvioFallback implements EnvioClient {

    @Override
    public EnvioResponse llamarApiExterna(EnvioRequest request) {
        System.out.println(" ⚠️  Circuit Breaker activado para el pedido: " + request.getPedidoId());
        
        return EnvioResponse.builder()
                .pedidoId(request.getPedidoId())
                .trackingNumber("FALLBACK-OFFLINE")
                .estado("FALLIDO") // Lo pasamos como String para evitar conflictos
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
}