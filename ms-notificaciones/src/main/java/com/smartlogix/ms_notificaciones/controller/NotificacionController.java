package com.smartlogix.ms_notificaciones.controller;

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
@CrossOrigin(origins = "http://localhost:5173")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private StockEventListener stockEventListener;

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
}