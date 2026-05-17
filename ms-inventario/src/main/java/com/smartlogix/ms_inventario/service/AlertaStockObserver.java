package com.smartlogix.ms_inventario.service;

import com.smartlogix.ms_inventario.model.AlertaStock;
import com.smartlogix.ms_inventario.repository.AlertaStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertaStockObserver implements StockObserver {

    @Autowired
    private AlertaStockRepository alertaStockRepository;

    @Override
    public void update(Long idProducto, String nombreProducto,
                       String tipoAlerta, Integer stockNuevo) {

        if (tipoAlerta.equals("NORMAL")) {
            // Stock volvió a normal — marcar alertas anteriores como leídas
            List<AlertaStock> alertasPendientes = alertaStockRepository
                    .findByIdProducto(idProducto);

            alertasPendientes.forEach(alerta -> {
                if (!alerta.getLeida()) {
                    alerta.setLeida(true);
                    alertaStockRepository.save(alerta);
                }
            });

            System.out.println("✅ Alertas resueltas para: " + nombreProducto);
            return;
        }

        System.out.println("👁️ AlertaStockObserver.update() — " +
                nombreProducto + " [" + tipoAlerta + "]");

        AlertaStock alerta = new AlertaStock();
        alerta.setIdProducto(idProducto);
        alerta.setTipo(tipoAlerta);
        alerta.setMensaje(generarMensaje(tipoAlerta, nombreProducto, stockNuevo));
        alertaStockRepository.save(alerta);
    }

    private String generarMensaje(String tipo, String nombre, Integer stock) {
        return switch (tipo) {
            case "AGOTADO" -> "⚠️ Producto AGOTADO: " + nombre;
            case "BAJO" -> "⚠️ Stock bajo en: " + nombre + " (actual: " + stock + ")";
            case "SOBRESTOCK" -> "⚠️ Sobrestock en: " + nombre + " (actual: " + stock + ")";
            default -> "";
        };
    }
}