package com.smartlogix.ms_inventario.controller;

import com.smartlogix.ms_inventario.dto.ProductoRequest;
import com.smartlogix.ms_inventario.dto.ProductoResponse;
import com.smartlogix.ms_inventario.model.AlertaStock;
import com.smartlogix.ms_inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"})

public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.crearProducto(request));
    }

    @PatchMapping("/productos/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");
        return ResponseEntity.ok(productoService.actualizarStock(id, nuevoStock));
    }
    @PatchMapping("/productos/{id}/devolver-stock")
public ResponseEntity<ProductoResponse> devolverStock(
        @PathVariable Long id,
        @RequestBody Map<String, Integer> body) {
    Integer cantidad = body.get("cantidad");
    return ResponseEntity.ok(productoService.devolverStock(id, cantidad));
}

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaStock>> alertasPendientes() {
        return ResponseEntity.ok(productoService.obtenerAlertasPendientes());
    }
    @DeleteMapping("/productos/{id}")
public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
    productoService.eliminarProducto(id);
    return ResponseEntity.noContent().build();
}
}