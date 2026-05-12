package com.smartlogix.ms_envios.model;

public enum EstadoEnvio {
    PENDIENTE,
    PROCESANDO,
    DESPACHADO,
    EN_CAMINO,
    ENTREGADO,
    FALLIDO, // <--- REVISA QUE ESTÉ ESCRITO ASÍ
    CANCELADO
}