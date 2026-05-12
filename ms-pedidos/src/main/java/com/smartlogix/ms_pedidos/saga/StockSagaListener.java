package com.smartlogix.ms_pedidos.saga;

import com.smartlogix.ms_pedidos.event.StockConfirmadoEvent;
import com.smartlogix.ms_pedidos.event.StockFallidoEvent;
import com.smartlogix.ms_pedidos.repository.PedidoRepository;
import com.smartlogix.ms_pedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockSagaListener {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @KafkaListener(topics = "stock-confirmado-topic", groupId = "pedidos-group")
    public void handleStockConfirmado(StockConfirmadoEvent event) {
        System.out.println("SAGA: Procesando confirmación de stock para el pedido #" + event.getNumeroPedido());
        pedidoRepository.findByNumeroPedido(event.getNumeroPedido())
                .ifPresent(p -> pedidoService.confirmarPedido(p.getId()));
    }

    @KafkaListener(topics = "stock-fallido-topic", groupId = "pedidos-group")
    public void handleStockFallido(StockFallidoEvent event) {
        System.out.println("SAGA: Procesando fallo de stock para el pedido #" + event.getNumeroPedido());
        pedidoRepository.findByNumeroPedido(event.getNumeroPedido())
                .ifPresent(p -> pedidoService.cancelarPedido(p.getId(), event.getMensajeError()));
    }
}
