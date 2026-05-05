package com.smartlogix.ms_notificaciones.factory;

import org.springframework.stereotype.Component;

@Component
public class EmailNotificacion implements NotificacionFactory {

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        System.out.println("   EMAIL enviado a: " + destinatario);
        System.out.println("   Asunto: " + asunto);
        System.out.println("   Mensaje: " + mensaje);
    }

    @Override
    public String getTipo() {
        return "EMAIL";
    }
}