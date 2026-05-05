package com.smartlogix.ms_usuarios.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SessionManager {

    private static SessionManager instance = null;
    private final Map<String, String> sesionesActivas = new ConcurrentHashMap<>();

    private SessionManager() {
        System.out.println("SessionManager inicializado — instancia única creada");
    }

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

    public void registrarSesion(String token, String email) {
        sesionesActivas.put(token, email);
        System.out.println("Sesión registrada para: " + email);
    }

    public void cerrarSesion(String token) {
        String email = sesionesActivas.remove(token);
        System.out.println("Sesión cerrada para: " + email);
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