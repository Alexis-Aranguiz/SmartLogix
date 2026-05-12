package com.smartlogix.ms_pedidos.strategy;

import org.springframework.stereotype.Component;

@Component("NORMAL")
public class DescuentoClienteNormal implements DescuentoStrategy {
    @Override
    public Double aplicarDescuento(Double total) {
        return total; // Sin descuento para clientes normales
    }
}
