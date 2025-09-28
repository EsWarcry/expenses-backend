package com.alvaro.gastos.dto;

import com.alvaro.gastos.validation.UniqueEmail;
import com.alvaro.gastos.validation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    // ID de la base de datos (útil para actualizar o devolver datos)
    private Long id;

    // keycloakId puede ser necesario si el frontend interactúa directamente con Keycloak
    // o para ciertas operaciones de backend que lo requieran desde la interfaz.
    private String keycloakId;

    @NotBlank(message = "El nombre de usuario no puede estar vacío") // No debe ser nulo ni solo espacios
    @Size(min = 9, max = 9, message = "El nombre de usuario debe tener 9 caracteres") // Longitud
    private String username; // Nombre de usuario

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido") // Valida formato de email
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email; // Correo electrónico

    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String firstName; // Nombre

    @Size(max = 50, message = "El apellido no puede exceder los 50 caracteres")
    private String lastName; // Apellido

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phone; // Teléfono
}
