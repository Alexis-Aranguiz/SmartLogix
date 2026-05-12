package com.smartlogix.ms_envios.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoEvent {
    private Long pedidoId;
    private String direccionDestino;
    private String estado; // ej: "VALIDADO"
}