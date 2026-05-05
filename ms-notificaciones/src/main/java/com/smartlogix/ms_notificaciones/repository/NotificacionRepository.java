package com.smartlogix.ms_notificaciones.repository;

import com.smartlogix.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByTipo(String tipo);
    List<Notificacion> findByEstado(String estado);
}