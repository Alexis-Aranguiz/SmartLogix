package com.smartlogix.ms_pedidos.facade;

import com.smartlogix.ms_pedidos.facade.dto.PagoResult;
import com.smartlogix.ms_pedidos.facade.subsistema.TarjetaService;
import com.smartlogix.ms_pedidos.facade.subsistema.TransferenciaService;
import com.smartlogix.ms_pedidos.facade.subsistema.WebPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PATRÓN FACADE — PagoFacade
 * ══════════════════════════════════════════════════════
 * Actúa como el "ejecutivo del banco":
 * El cliente (PedidoService) solo llama procesarPago()
 * La fachada coordina internamente con los subsistemas
 * sin que el cliente sepa cómo funciona cada uno.
 *
 * PedidoService NO conoce TarjetaService, TransferenciaService
 * ni WebPayService — solo conoce PagoFacade.
 * ══════════════════════════════════════════════════════
 */
@Component
public class PagoFacade {

    // Subsistemas complejos — el cliente nunca los ve directamente
    @Autowired
    private TarjetaService tarjetaService;

    @Autowired
    private TransferenciaService transferenciaService;

    @Autowired
    private WebPayService webPayService;

    /**
     * Método ÚNICO que expone la fachada al cliente
     * Toda la complejidad queda oculta aquí dentro
     */
    public PagoResult procesarPago(Double monto, String metodoPago,
                                    String datoPago, String ordenCompra) {
        System.out.println("🏪 Facade: procesando pago de $" + monto +
                " con método: " + metodoPago);

        return switch (metodoPago.toUpperCase()) {
            case "TARJETA" -> procesarTarjeta(monto, datoPago);
            case "TRANSFERENCIA" -> procesarTransferencia(monto, datoPago);
            case "WEBPAY" -> procesarWebPay(monto, ordenCompra);
            default -> new PagoResult(false, null,
                    "Método de pago no soportado: " + metodoPago, metodoPago);
        };
    }

    // ── MÉTODOS PRIVADOS — subsistemas ocultos ─────────────────────

    private PagoResult procesarTarjeta(Double monto, String numeroTarjeta) {
        try {
            // Coordina 3 pasos del subsistema transparentemente
            String validacion = tarjetaService.validarTarjeta(numeroTarjeta);
            if (!"TARJETA_VALIDA".equals(validacion)) {
                return new PagoResult(false, null, "Tarjeta inválida", "TARJETA");
            }
            String token = tarjetaService.tokenizarTarjeta(numeroTarjeta);
            boolean cobrado = tarjetaService.cobrar(token, monto);

            return cobrado
                ? new PagoResult(true, token, "Pago con tarjeta exitoso", "TARJETA")
                : new PagoResult(false, null, "Error al cobrar tarjeta", "TARJETA");
        } catch (Exception e) {
            return new PagoResult(false, null, "Error: " + e.getMessage(), "TARJETA");
        }
    }

    private PagoResult procesarTransferencia(Double monto, String numeroCuenta) {
        try {
            // Coordina 3 pasos del subsistema transparentemente
            boolean cuentaValida = transferenciaService.verificarCuenta(numeroCuenta);
            if (!cuentaValida) {
                return new PagoResult(false, null, "Cuenta bancaria inválida", "TRANSFERENCIA");
            }
            boolean fondosSuficientes = transferenciaService.verificarFondos(numeroCuenta, monto);
            if (!fondosSuficientes) {
                return new PagoResult(false, null, "Fondos insuficientes", "TRANSFERENCIA");
            }
            String codigoTrf = transferenciaService.ejecutarTransferencia(numeroCuenta, monto);

            return new PagoResult(true, codigoTrf, "Transferencia exitosa", "TRANSFERENCIA");
        } catch (Exception e) {
            return new PagoResult(false, null, "Error: " + e.getMessage(), "TRANSFERENCIA");
        }
    }

    private PagoResult procesarWebPay(Double monto, String ordenCompra) {
        try {
            // Coordina 3 pasos del subsistema transparentemente
            String tokenWebpay = webPayService.iniciarTransaccion(monto, ordenCompra);
            boolean confirmado = webPayService.confirmarTransaccion(tokenWebpay);
            if (!confirmado) {
                return new PagoResult(false, null, "WebPay no confirmó la transacción", "WEBPAY");
            }
            String codigoAuth = webPayService.obtenerCodigoAutorizacion(tokenWebpay);

            return new PagoResult(true, codigoAuth, "Pago WebPay exitoso", "WEBPAY");
        } catch (Exception e) {
            return new PagoResult(false, null, "Error: " + e.getMessage(), "WEBPAY");
        }
    }
}