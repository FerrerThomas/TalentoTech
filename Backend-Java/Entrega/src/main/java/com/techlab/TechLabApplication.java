package com.techlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechLabApplication.class, args);
        System.out.println("=============================================================");
        System.out.println("   TECHLAB E-COMMERCE - SIBELIUS COFFEE & SPECIALTY TEA      ");
        System.out.println("   API RESTful y Frontend Web activos en:                   ");
        System.out.println("   http://localhost:8080/                                    ");
        System.out.println("   Documentación de Endpoints en README.md                   ");
        System.out.println("=============================================================");
    }
}
