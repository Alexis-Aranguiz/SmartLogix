package com.smartlogix.ms_pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponse {
    private Long id;
    private String numeroPedido;
    private String clienteEmail;
    private Double total;
    private String estado;
    private String tipoCliente;
    private Date fechaCreacion;
    private List<ItemResponse> items;

    @Data
    public static class ItemResponse {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private Double precioUnitario;
    }
}
