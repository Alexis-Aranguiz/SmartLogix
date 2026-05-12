package com.smartlogix.ms_envios.dto;

import lombok.Data;

@Data
public class EnvioRequest {
    private Long pedidoId;
    private String direccionDestino;
    private Long transportistaId;
}