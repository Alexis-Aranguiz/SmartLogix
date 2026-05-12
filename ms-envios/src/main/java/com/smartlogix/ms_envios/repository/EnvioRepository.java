package com.smartlogix.ms_envios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartlogix.ms_envios.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    // Necesario para el método obtenerPorPedidoId del service
    Optional<Envio> findByPedidoId(Long pedidoId);
}