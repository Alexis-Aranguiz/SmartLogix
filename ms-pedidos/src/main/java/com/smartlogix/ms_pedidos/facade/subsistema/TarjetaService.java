package com.smartlogix.ms_pedidos.facade.subsistema;

import org.springframework.stereotype.Component;

/**
 * SUBSISTEMA 1 — Procesamiento de tarjetas
 * Lógica compleja de validación, tokenización y cargo
 * El cliente (PedidoService) nunca interactúa con esto directamente
 */
@Component
public class TarjetaService {

    public String validarTarjeta(String numeroTarjeta) {
        System.out.println("💳 Validando tarjeta: ****" +
                numeroTarjeta.substring(numeroTarjeta.length() - 4));
        return "TARJETA_VALIDA";
    }

    public String tokenizarTarjeta(String numeroTarjeta) {
        System.out.println("🔐 Tokenizando tarjeta...");
        return "TOKEN_" + System.currentTimeMillis();
    }

    public boolean cobrar(String token, Double monto) {
        System.out.println("💰 Cobrando $" + monto + " con token: " + token);
        return true;
    }
}