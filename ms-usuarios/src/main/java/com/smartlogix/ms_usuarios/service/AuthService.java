package com.smartlogix.ms_usuarios.service;

import com.smartlogix.ms_usuarios.dto.LoginRequest;
import com.smartlogix.ms_usuarios.dto.LoginResponse;
import com.smartlogix.ms_usuarios.model.Usuario;
import com.smartlogix.ms_usuarios.repository.UsuarioRepository;
import com.smartlogix.ms_usuarios.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // PATRÓN SINGLETON: Spring crea UNA sola instancia de esta clase
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());

        return new LoginResponse(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }
}