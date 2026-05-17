package com.smartlogix.ms_pedidos.controller;

import com.smartlogix.ms_pedidos.dto.PedidoRequest;
import com.smartlogix.ms_pedidos.model.Pedido;
import com.smartlogix.ms_pedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody PedidoRequest pedidoRequest) {
        return ResponseEntity.ok(pedidoService.crearPedido(pedidoRequest));
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, nuevoEstado));
    }
    @PatchMapping("/{id}/cancelar")
public ResponseEntity<Pedido> cancelarPedido(
        @PathVariable Long id,
        @RequestParam(defaultValue = "Cancelado por administrador") String motivo) {
    pedidoService.cancelarPedido(id, motivo);
    return ResponseEntity.ok(pedidoService.obtenerPorId(id));
}
}
