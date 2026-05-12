package com.smartlogix.ms_envios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartlogix.ms_envios.dto.EnvioRequest;
import com.smartlogix.ms_envios.dto.EnvioResponse;
import com.smartlogix.ms_envios.model.EstadoEnvio;
import com.smartlogix.ms_envios.service.EnvioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping
    public ResponseEntity<EnvioResponse> crearEnvio(@RequestBody EnvioRequest request) {
        return new ResponseEntity<>(envioService.crearEnvio(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioResponse> obtenerPorPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(envioService.obtenerPorPedidoId(pedidoId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioResponse> actualizarEstado(
            @PathVariable Long id, 
            @RequestParam EstadoEnvio nuevoEstado) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, nuevoEstado));
    }

    @GetMapping
    public ResponseEntity<List<EnvioResponse>> listarTodos() {
        return ResponseEntity.ok(envioService.listarTodos());
    }
}