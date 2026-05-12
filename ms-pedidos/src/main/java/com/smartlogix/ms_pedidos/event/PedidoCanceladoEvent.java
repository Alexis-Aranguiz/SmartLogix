package com.smartlogix.ms_pedidos.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoCanceladoEvent {
    private String numeroPedido;
    private String motivo;
}
