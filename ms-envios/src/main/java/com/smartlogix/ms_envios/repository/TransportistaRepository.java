package com.smartlogix.ms_envios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartlogix.ms_envios.model.Transportista;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
}