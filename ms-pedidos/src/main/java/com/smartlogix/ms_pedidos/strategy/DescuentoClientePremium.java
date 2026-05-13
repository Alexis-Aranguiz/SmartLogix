package com.smartlogix.ms_pedidos.strategy;

import org.springframework.stereotype.Component;

@Component("PREMIUM")
public class DescuentoClientePremium implements DescuentoStrategy {
    @Override
    public Double aplicarDescuento(Double total) {
        return total * 0.90; // 10% de descuento para clientes premium
    }
}
