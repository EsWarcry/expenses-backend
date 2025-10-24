package com.alvaro.gastos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) // El ID de Keycloak es fundamental, no puede ser nulo y debe ser único
    private String keycloakId; // ID único del usuario en Keycloak (UUID). Crucial para la integración.

    @Column(nullable = false, unique = true)
    private String username; // Nombre de usuario (ej: "juanperez")

    @Column(nullable = false, unique = true) // El email debe ser único y no nulo
    private String email;

    private String firstName; // Nombre del usuario

    private String lastName;

    private String phone;
    private String role;
}
