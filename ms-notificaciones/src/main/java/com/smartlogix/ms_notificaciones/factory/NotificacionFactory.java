package com.smartlogix.ms_notificaciones.factory;

public interface NotificacionFactory {
    void enviar(String destinatario, String asunto, String mensaje);
    String getTipo();
}