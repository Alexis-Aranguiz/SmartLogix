package com.smartlogix.ms_notificaciones.service;

import com.smartlogix.ms_notificaciones.factory.NotificacionFactory;
import com.smartlogix.ms_notificaciones.model.Notificacion;
import com.smartlogix.ms_notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private List<NotificacionFactory> factories;

    @Autowired
    private NotificacionRepository notificacionRepository;

    public void enviarNotificacion(String tipo, String destinatario,
                                    String asunto, String mensaje) {
        
        NotificacionFactory factory = factories.stream()
                .filter(f -> f.getTipo().equals(tipo))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de notificación no soportado: " + tipo));


        factory.enviar(destinatario, asunto, mensaje);

        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(tipo);
        notificacion.setDestinatario(destinatario);
        notificacion.setAsunto(asunto);
        notificacion.setMensaje(mensaje);
        notificacion.setEstado("ENVIADO");
        notificacionRepository.save(notificacion);
    }

    public List<Notificacion> listarNotificaciones() {
        return notificacionRepository.findAll();
    }
}