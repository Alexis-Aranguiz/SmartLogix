package com.smartlogix.ms_pedidos.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DescuentoStrategyFactory {

    @Autowired
    private Map<String, DescuentoStrategy> estrategias;

    public DescuentoStrategy getStrategy(String tipoCliente) {
        return estrategias.getOrDefault(tipoCliente.toUpperCase(), estrategias.get("NORMAL"));
    }
}
