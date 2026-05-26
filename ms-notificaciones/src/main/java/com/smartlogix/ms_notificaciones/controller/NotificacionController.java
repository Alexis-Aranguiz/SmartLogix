package com.smartlogix.ms_notificaciones.controller;

import com.smartlogix.ms_notificaciones.listener.PedidoEventListener;
import com.smartlogix.ms_notificaciones.listener.StockEventListener;
import com.smartlogix.ms_notificaciones.model.Notificacion;
import com.smartlogix.ms_notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"})
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private StockEventListener stockEventListener;

    @Autowired
    private PedidoEventListener pedidoEventListener;

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }
 
    @PostMapping("/stock-evento")
    public ResponseEntity<?> recibirEventoStock(@RequestBody Map<String, Object> evento) {
        Long idProducto = Long.valueOf(evento.get("idProducto").toString());
        String nombreProducto = evento.get("nombreProducto").toString();
        String tipoAlerta = evento.get("tipoAlerta").toString();
        Integer stockNuevo = Integer.valueOf(evento.get("stockNuevo").toString());


        stockEventListener.onStockActualizado(idProducto, nombreProducto,
                tipoAlerta, stockNuevo);

        return ResponseEntity.ok("Notificación procesada");
    }

    @PostMapping("/pedido-evento")
public ResponseEntity<?> recibirEventoPedido(@RequestBody Map<String, Object> evento) {
    try {
        String estado = evento.get("estado").toString();
        String numeroPedido = evento.get("numeroPedido").toString();
        String clienteEmail = evento.get("clienteEmail") != null
                ? evento.get("clienteEmail").toString()
                : "encargado@smartlogix.cl";

        if (estado.equals("CANCELADO")) {
            notificacionService.enviarNotificacion(
                "EMAIL",
                clienteEmail,
                "❌ Pedido #" + numeroPedido + " cancelado",
                "Tu pedido #" + numeroPedido + " fue cancelado. El stock ha sido devuelto."
            );
        } else if (estado.equals("CONFIRMADO")) {
            notificacionService.enviarNotificacion(
                "EMAIL",
                clienteEmail,
                "✅ Pedido #" + numeroPedido + " confirmado",
                "Tu pedido #" + numeroPedido + " ha sido confirmado."
            );
        } else {
            notificacionService.enviarNotificacion(
                "EMAIL",
                clienteEmail,
                "📦 Pedido #" + numeroPedido + " actualizado",
                "Tu pedido #" + numeroPedido + " cambió al estado: " + estado
            );
        }
        return ResponseEntity.ok("Notificación de pedido procesada");
    } catch (Exception e) {
        return ResponseEntity.ok("Error procesando notificación: " + e.getMessage());
    }
}
}