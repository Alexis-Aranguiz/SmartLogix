package com.smartlogix.ms_pedidos.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockFallidoEvent {
    private String numeroPedido;
    private String mensajeError;
}
