package com.smartlogix.ms_envios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // Importante

@SpringBootApplication
@EnableFeignClients // Agrega esto
public class MsEnviosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsEnviosApplication.class, args);
    }
}