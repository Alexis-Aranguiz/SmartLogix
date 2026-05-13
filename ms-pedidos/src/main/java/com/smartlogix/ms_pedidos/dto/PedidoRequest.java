package com.smartlogix.ms_pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoRequest {
    private String clienteEmail;
    private String tipoCliente; // NORMAL o PREMIUM
    private List<ItemRequest> items;
    private String metodoPago;   // TARJETA, TRANSFERENCIA, WEBPAY
    private String datoPago;     // número de tarjeta o cuenta bancaria

    @Data
    public static class ItemRequest {
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private Double precioUnitario;
    }
}
