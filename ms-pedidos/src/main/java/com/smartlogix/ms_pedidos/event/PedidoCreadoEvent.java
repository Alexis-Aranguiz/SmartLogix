package com.smartlogix.ms_pedidos.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoCreadoEvent {
    private String numeroPedido;
    private String clienteEmail;
    private Double total;
    private List<ItemEvento> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemEvento {
        private Long productoId;
        private Integer cantidad;
    }
}
