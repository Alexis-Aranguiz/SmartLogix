package com.smartlogix.ms_pedidos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroPedido;
    private String clienteEmail;
    private Double total;
    private String estado; // CREADO, ENVIADO, ENTREGADO
    private String tipoCliente; // NORMAL, PREMIUM

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemPedido> items = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @PrePersist
    protected void onCreate() { fechaCreacion = new Date(); }
}
