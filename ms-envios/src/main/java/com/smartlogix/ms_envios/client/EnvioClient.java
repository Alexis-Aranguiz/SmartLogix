package com.smartlogix.ms_envios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartlogix.ms_envios.dto.EnvioRequest;
import com.smartlogix.ms_envios.dto.EnvioResponse;

@FeignClient(name = "ms-externo-courier", url = "http://localhost:9999", fallback = EnvioFallback.class)
public interface EnvioClient {
    @PostMapping("/api/externa/despacho")
    EnvioResponse llamarApiExterna(@RequestBody EnvioRequest request);
}