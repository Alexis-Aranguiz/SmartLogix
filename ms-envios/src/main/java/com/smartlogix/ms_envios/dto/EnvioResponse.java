package com.smartlogix.ms_envios.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioResponse {
    private Long id;
    private Long pedidoId;
    private String trackingNumber;
    private String estado;
    private String nombreTransportista;
    private LocalDateTime fechaCreacion;
}