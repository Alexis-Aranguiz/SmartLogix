package com.smartlogix.ms_envios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(unique = true)
    private String trackingNumber;

    private String direccionDestino;

    // Aquí está la corrección: Usamos el Enum en lugar de String
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

   

    private LocalDateTime fechaCreacion = LocalDateTime.now();
}