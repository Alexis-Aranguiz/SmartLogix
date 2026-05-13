package com.smartlogix.ms_pedidos.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoResult {
    private boolean exitoso;
    private String codigoTransaccion;
    private String mensaje;
    private String metodoPago;
}