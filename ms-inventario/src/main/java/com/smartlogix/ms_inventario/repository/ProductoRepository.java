package com.smartlogix.ms_inventario.repository;

import com.smartlogix.ms_inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findBySku(String sku);

    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosStockCritico();

    @Query("SELECT p FROM Producto p WHERE p.stock >= p.stockMaximo AND p.activo = true")
    List<Producto> findProductosSobrestock();
}