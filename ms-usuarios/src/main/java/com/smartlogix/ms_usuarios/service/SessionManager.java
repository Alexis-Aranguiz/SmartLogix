package com.smartlogix.ms_usuarios.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PATRÓN SINGLETON — SessionManager
 * Garantiza UNA SOLA instancia que gestiona todas las sesiones activas.
 * Constructor privado + getInstance() estático.
 */
public class SessionManager {

    // 1. ATRIBUTO ESTÁTICO PRIVADO — la única instancia
    private static SessionManager instance = null;

    // Map de sesiones activas: token → email del usuario
    private final Map<String, String> sesionesActivas = new ConcurrentHashMap<>();

    // 2. CONSTRUCTOR PRIVADO — nadie puede hacer new SessionManager()
    private SessionManager() {
        System.out.println("✅ SessionManager inicializado — instancia única creada");
    }

    // 3. MÉTODO PÚBLICO ESTÁTICO getInstance()
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    // 4. MÉTODOS DE NEGOCIO
    public void registrarSesion(String token, String email) {
        sesionesActivas.put(token, email);
        System.out.println("🔐 Sesión registrada para: " + email);
    }

    public void cerrarSesion(String token) {
        String email = sesionesActivas.remove(token);
        System.out.println("🔓 Sesión cerrada para: " + email);
    }

    public boolean sesionActiva(String token) {
        return sesionesActivas.containsKey(token);
    }

    public String getEmailPorToken(String token) {
        return sesionesActivas.get(token);
    }

    public int totalSesionesActivas() {
        return sesionesActivas.size();
    }
}