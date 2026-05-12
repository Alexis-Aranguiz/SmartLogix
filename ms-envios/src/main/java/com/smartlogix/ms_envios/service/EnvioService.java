package com.smartlogix.ms_envios.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartlogix.ms_envios.client.EnvioClient;
import com.smartlogix.ms_envios.dto.EnvioRequest;
import com.smartlogix.ms_envios.dto.EnvioResponse;
import com.smartlogix.ms_envios.model.Envio;
import com.smartlogix.ms_envios.model.EstadoEnvio;
import com.smartlogix.ms_envios.repository.EnvioRepository;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private EnvioClient envioClient;

    @Transactional
    public EnvioResponse crearEnvio(EnvioRequest request) {
        String tracking = "SLX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Envio envio = new Envio();
        envio.setPedidoId(request.getPedidoId());
        envio.setTrackingNumber(tracking);
        envio.setDireccionDestino(request.getDireccionDestino());
        envio.setEstado(EstadoEnvio.PENDIENTE);
        // La fecha de creación se genera sola en la clase Envio (LocalDateTime.now())

        envioRepository.save(envio);

        // Llamada externa protegida por Circuit Breaker
        return envioClient.llamarApiExterna(request);
    }

    public EnvioResponse obtenerPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        return mapToResponse(envio);
    }

    public EnvioResponse obtenerPorPedidoId(Long pedidoId) {
        Envio envio = envioRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("No hay envío para el pedido: " + pedidoId));
        return mapToResponse(envio);
    }

    @Transactional
    public EnvioResponse actualizarEstado(Long id, EstadoEnvio nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        envio.setEstado(nuevoEstado);
        return mapToResponse(envioRepository.save(envio));
    }

    public List<EnvioResponse> listarTodos() {
        return envioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para convertir la Entidad al DTO de respuesta
    private EnvioResponse mapToResponse(Envio envio) {
        return EnvioResponse.builder()
                .id(envio.getId())
                .pedidoId(envio.getPedidoId())
                .trackingNumber(envio.getTrackingNumber())
                .estado(envio.getEstado().name())
                .fechaCreacion(envio.getFechaCreacion()) // <--- AQUÍ PASAMOS LA FECHA
                .build();
    }
}