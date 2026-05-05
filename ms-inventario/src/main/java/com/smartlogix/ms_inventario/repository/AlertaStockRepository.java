package com.smartlogix.ms_inventario.repository;

import com.smartlogix.ms_inventario.model.AlertaStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {
    List<AlertaStock> findByLeidaFalse();
    List<AlertaStock> findByIdProducto(Long idProducto);
}