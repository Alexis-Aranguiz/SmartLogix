package com.smartlogix.ms_usuarios.controller;

import com.smartlogix.ms_usuarios.dto.LoginRequest;
import com.smartlogix.ms_usuarios.dto.LoginResponse;
import com.smartlogix.ms_usuarios.model.Usuario;
import com.smartlogix.ms_usuarios.repository.UsuarioRepository;
import com.smartlogix.ms_usuarios.service.AuthService;
import com.smartlogix.ms_usuarios.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
public ResponseEntity<?> logout(@RequestBody(required = false) Map<String, String> body) {
    if (body != null && body.containsKey("token")) {
        String token = body.get("token");
        authService.logout(token);
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }
    return ResponseEntity.badRequest().body("Token requerido");
}

    @GetMapping("/sesiones-activas")
    public ResponseEntity<?> sesionesActivas() {
        int total = SessionManager.getInstance().totalSesionesActivas();
        return ResponseEntity.ok("Sesiones activas: " + total);
    }

    @PostMapping("/registro")
public ResponseEntity<?> registro(@RequestBody Usuario usuario) {
    usuario.setId(null);
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuarioRepository.save(usuario);
    return ResponseEntity.ok("Usuario creado correctamente");
}
}