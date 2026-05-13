package com.smartlogix.ms_pedidos.service;

import com.smartlogix.ms_pedidos.event.PedidoCanceladoEvent;
import com.smartlogix.ms_pedidos.event.PedidoConfirmadoEvent;
import com.smartlogix.ms_pedidos.event.PedidoCreadoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "pedidos-topic";

    public void enviarPedidoCreado(PedidoCreadoEvent evento) {
        System.out.println("Kafka: Produciendo evento PedidoCreado para #" + evento.getNumeroPedido());
        kafkaTemplate.send(TOPIC, evento.getNumeroPedido(), evento);
    }

    public void enviarPedidoConfirmado(PedidoConfirmadoEvent evento) {
        System.out.println("Kafka: Confirmando pedido #" + evento.getNumeroPedido() + " en el bus de eventos.");
        kafkaTemplate.send("pedido-confirmado-topic", evento.getNumeroPedido(), evento);
    }

    public void enviarPedidoCancelado(PedidoCanceladoEvent evento) {
        System.out.println("Kafka: Cancelando pedido #" + evento.getNumeroPedido() + " por fallo en la saga.");
        kafkaTemplate.send("pedido-cancelado-topic", evento.getNumeroPedido(), evento);
    }
}