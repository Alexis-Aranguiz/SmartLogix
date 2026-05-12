package com.smartlogix.ms_pedidos.service;

import com.smartlogix.ms_pedidos.client.NotificacionClient;
import com.smartlogix.ms_pedidos.event.PedidoCanceladoEvent;
import com.smartlogix.ms_pedidos.event.PedidoConfirmado;
import com.smartlogix.ms_pedidos.event.PedidoCreadoEvent;
import com.smartlogix.ms_pedidos.model.Pedido;
import com.smartlogix.ms_pedidos.repository.PedidoRepository;
import com.smartlogix.ms_pedidos.strategy.DescuentoStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private NotificacionClient notificacionClient;

    @Autowired
    private DescuentoStrategyFactory strategyFactory;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public Pedido crearPedido(Pedido pedido) {
        pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pedido.setEstado("CREADO");
        
        if (pedido.getItems() != null) {
            pedido.getItems().forEach(item -> item.setPedido(pedido));
            
            // Calcular subtotal sumando (cantidad * precioUnitario) de cada ítem
            Double subtotal = pedido.getItems().stream()
                    .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                    .sum();
            
            // Aplicar estrategia de descuento según tipo de cliente
            Double totalConDescuento = strategyFactory.getStrategy(pedido.getTipoCliente()).aplicarDescuento(subtotal);
            pedido.setTotal(totalConDescuento);
        }
        
        Pedido guardado = pedidoRepository.save(pedido);
        
        // 1. Notificación Síncrona (Feign)
        notificarCambioEstado(guardado);

        // 2. Notificación Asíncrona (Kafka para Inventario)
        PedidoCreadoEvent evento = PedidoCreadoEvent.builder()
                .numeroPedido(guardado.getNumeroPedido())
                .clienteEmail(guardado.getClienteEmail())
                .total(guardado.getTotal())
                .items(guardado.getItems().stream()
                        .map(i -> new PedidoCreadoEvent.ItemEvento(i.getProductoId(), i.getCantidad()))
                        .collect(Collectors.toList()))
                .build();
        kafkaProducerService.enviarPedidoCreado(evento);

        return guardado;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        
        notificarCambioEstado(actualizado);
        return actualizado;
    }

    public void confirmarPedido(Long id) {
        Pedido pedido = actualizarEstado(id, "CONFIRMADO");
        
        PedidoConfirmado evento = PedidoConfirmado.builder()
                .numeroPedido(pedido.getNumeroPedido())
                .clienteEmail(pedido.getClienteEmail())
                .total(pedido.getTotal())
                .build();
        kafkaProducerService.enviarPedidoConfirmado(evento);
    }

    public void cancelarPedido(Long id, String motivo) {
        Pedido pedido = actualizarEstado(id, "CANCELADO");
        
        PedidoCanceladoEvent evento = PedidoCanceladoEvent.builder()
                .numeroPedido(pedido.getNumeroPedido())
                .motivo(motivo)
                .build();
        kafkaProducerService.enviarPedidoCancelado(evento);
    }

    private void notificarCambioEstado(Pedido pedido) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("numeroPedido", pedido.getNumeroPedido());
            evento.put("clienteEmail", pedido.getClienteEmail());
            evento.put("estado", pedido.getEstado());
            
            notificacionClient.enviarEventoPedido(evento);
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
    }
}
