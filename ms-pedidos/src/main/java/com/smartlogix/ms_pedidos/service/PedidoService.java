package com.smartlogix.ms_pedidos.service;

import com.smartlogix.ms_pedidos.client.NotificacionClient;
import com.smartlogix.ms_pedidos.dto.PedidoRequest;
import com.smartlogix.ms_pedidos.event.PedidoCanceladoEvent;
import com.smartlogix.ms_pedidos.event.PedidoConfirmadoEvent;
import com.smartlogix.ms_pedidos.event.PedidoCreadoEvent;
import com.smartlogix.ms_pedidos.facade.PagoFacade;
import com.smartlogix.ms_pedidos.facade.dto.PagoResult;
import com.smartlogix.ms_pedidos.model.Pedido;
import com.smartlogix.ms_pedidos.model.PedidoItem;
import com.smartlogix.ms_pedidos.repository.PedidoRepository;
import com.smartlogix.ms_pedidos.strategy.DescuentoStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
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
    private PagoFacade pagoFacade;

    @Autowired
    private DescuentoStrategyFactory strategyFactory;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Transactional
public Pedido crearPedido(PedidoRequest request) {
    Pedido pedido = new Pedido();
    pedido.setClienteEmail(request.getClienteEmail());
    pedido.setTipoCliente(request.getTipoCliente() != null ? request.getTipoCliente() : "NORMAL");
    pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    pedido.setEstado("CREADO");
    pedido.setTotal(0.0);
    pedido.setItems(new ArrayList<>());

    if (request.getItems() != null && !request.getItems().isEmpty()) {
        List<PedidoItem> items = request.getItems().stream().map(itemReq -> {
            PedidoItem item = new PedidoItem();
            item.setProductoId(itemReq.getProductoId());
            item.setNombreProducto(itemReq.getNombreProducto());
            item.setCantidad(itemReq.getCantidad() != null ? itemReq.getCantidad() : 0);
            item.setPrecioUnitario(itemReq.getPrecioUnitario() != null ? itemReq.getPrecioUnitario() : 0.0);
            item.setPedido(pedido);
            return item;
        }).collect(Collectors.toList());

        pedido.setItems(items);

        Double subtotal = pedido.getItems().stream()
                .mapToDouble(item -> {
                    int cant = item.getCantidad() != null ? item.getCantidad() : 0;
                    double precio = item.getPrecioUnitario() != null ? item.getPrecioUnitario() : 0.0;
                    return cant * precio;
                })
                .sum();

        try {
            Double totalConDescuento = strategyFactory.getStrategy(pedido.getTipoCliente()).aplicarDescuento(subtotal);
            pedido.setTotal(totalConDescuento);
        } catch (Exception e) {
            System.err.println("Error al aplicar estrategia de descuento: " + e.getMessage());
            pedido.setTotal(subtotal);
        }
    }

    PagoResult resultadoPago = pagoFacade.procesarPago(
        pedido.getTotal(),
        request.getMetodoPago() != null ? request.getMetodoPago() : "WEBPAY",
        request.getDatoPago() != null ? request.getDatoPago() : "",
        pedido.getNumeroPedido()
    );

    if (!resultadoPago.isExitoso()) {
        throw new RuntimeException("Pago rechazado: " + resultadoPago.getMensaje());
    }

    pedido.setCodigoTransaccion(resultadoPago.getCodigoTransaccion());
    System.out.println(" Facade: pago procesado — " + resultadoPago.getMensaje());


    Pedido guardado = pedidoRepository.save(pedido);

 
    notificarCambioEstado(guardado);


    try {
        PedidoCreadoEvent evento = PedidoCreadoEvent.builder()
                .numeroPedido(guardado.getNumeroPedido())
                .clienteEmail(guardado.getClienteEmail())
                .total(guardado.getTotal())
                .items(guardado.getItems() != null ? guardado.getItems().stream()
                        .map(i -> new PedidoCreadoEvent.ItemEvento(i.getProductoId(), i.getCantidad()))
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
        kafkaProducerService.enviarPedidoCreado(evento);
    } catch (Exception e) {
        System.err.println("ERROR KAFKA: " + e.getMessage());
    }

    return guardado;
}

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    @Transactional
    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        
        notificarCambioEstado(actualizado);
        return actualizado;
    }

    @Transactional
    public void confirmarPedido(Long id) {
        Pedido pedido = actualizarEstado(id, "CONFIRMADO");
        
        PedidoConfirmadoEvent evento = PedidoConfirmadoEvent.builder()
                .numeroPedido(pedido.getNumeroPedido())
                .clienteEmail(pedido.getClienteEmail())
                .total(pedido.getTotal())
                .build();
        kafkaProducerService.enviarPedidoConfirmado(evento);
    }

    @Autowired
private org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder;

@org.springframework.beans.factory.annotation.Value("${app.inventario.url}")
private String inventarioUrl;

@Transactional
public void cancelarPedido(Long id, String motivo) {
    Pedido pedido = actualizarEstado(id, "CANCELADO");

    // SAGA COMPENSACIÓN — devolver stock de cada item
    if (pedido.getItems() != null) {
        pedido.getItems().forEach(item -> {
            try {
                java.util.Map<String, Integer> body = new java.util.HashMap<>();
                body.put("cantidad", item.getCantidad());

                webClientBuilder.build()
                    .patch()
                    .uri(inventarioUrl + "/api/inventario/productos/" +
                            item.getProductoId() + "/devolver-stock")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response ->
                        System.out.println("✅ SAGA: Stock devuelto para producto " +
                                item.getProductoId()));
            } catch (Exception e) {
                System.err.println("❌ SAGA: Error al devolver stock: " + e.getMessage());
            }
        });
    }

    // Publicar evento de cancelación (Kafka)
    PedidoCanceladoEvent evento = PedidoCanceladoEvent.builder()
            .numeroPedido(pedido.getNumeroPedido())
            .motivo(motivo)
            .clienteEmail(pedido.getClienteEmail())
            .build();
    kafkaProducerService.enviarPedidoCancelado(evento);
}

    private void notificarCambioEstado(Pedido pedido) {
    // Cancelación se maneja por Kafka (PedidoCanceladoEvent)
    if (pedido.getEstado().equals("CANCELADO")) return;
    
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
