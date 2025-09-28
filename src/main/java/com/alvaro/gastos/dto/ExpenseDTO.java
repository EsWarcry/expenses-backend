package com.alvaro.gastos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) para la entidad Expense.
 * Este objeto se utiliza para transferir datos de gastos entre las capas de la aplicación
 * (principalmente entre el backend y el frontend).
 *
 * Incluye anotaciones de validación para asegurar que los datos recibidos del frontend sean válidos.
 *
 */

@Data
public class ExpenseDTO {

    private Long id;

    @NotNull(message = "El ID de usuario no puede ser nulo")
    private Long userId;  //ID del usuario que registra el gasto.

    @NotNull(message = "La fecha del gasto no puede ser nula")
    @PastOrPresent(message = "La fecha del gasto no puede ser futura")
    private LocalDate expenseDate; //fecha en la que ocurrio el gasto.

    @NotNull(message = "La cantidad del gasto no puede ser nula")
    @DecimalMin(value = "0.01", message = "El monto del gasto debe ser mayor que cero")
    private Double amount;  // monto del gasto.

    @DecimalMin(value = "0.0", message = "El kilometraje no pueder negativo")
    private Double mileage; // El kilometraje es opcional

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String description; //Descripcion opcional del gasto.

    @URL(message = "El formato de la URL de la imagen no es válido")
    @Size(max = 1024, message = "La URL de la imagen no puede exceder los 1024 caracteres")
    private String imageUrl;

    @NotNull(message = "El ID del tipo de gasto no puede ser nulo")
    private Long expenseTypeId; //Id del tipo de gasto

    private String expenseTypeName;

    // La fecha de creación del registro. No necesita @NotNull si se autogenera en el backend para la entrada.
    // Pero si se devuelve al frontend, sí se mostrará.
    private LocalDateTime createdAt; // Fecha y hora en que se registró el gasto en el sistema


}
