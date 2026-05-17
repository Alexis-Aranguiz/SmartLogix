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
        String tracking = "SLX-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();

        Envio envio = new Envio();
        envio.setPedidoId(request.getPedidoId());
        envio.setTrackingNumber(tracking);
        envio.setDireccionDestino(request.getDireccionDestino());
        envio.setEstado(EstadoEnvio.PENDIENTE);

        // Guardar en BD primero
        Envio guardado = envioRepository.save(envio);

        // CIRCUIT BREAKER — intentar llamada a API externa
        // Si falla, el fallback devuelve FALLBACK-OFFLINE
        // pero el envío ya está guardado en BD correctamente
        try {
            envioClient.llamarApiExterna(request);
            System.out.println("✅ API courier respondió correctamente");
        } catch (Exception e) {
            System.out.println("⚠️ Circuit Breaker activado: " + e.getMessage());
        }

        // Siempre devolver el envío real guardado en BD
        return mapToResponse(guardado);
    }

    public EnvioResponse obtenerPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        return mapToResponse(envio);
    }

    public EnvioResponse obtenerPorPedidoId(Long pedidoId) {
        Envio envio = envioRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException(
                        "No hay envío para el pedido: " + pedidoId));
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

    private EnvioResponse mapToResponse(Envio envio) {
        return EnvioResponse.builder()
                .id(envio.getId())
                .pedidoId(envio.getPedidoId())
                .trackingNumber(envio.getTrackingNumber())
                .estado(envio.getEstado().name())
                .nombreTransportista(envio.getTransportista() != null
                        ? envio.getTransportista().getNombre()
                        : "Por asignar")
                .fechaCreacion(envio.getFechaCreacion())
                .build();
    }
}