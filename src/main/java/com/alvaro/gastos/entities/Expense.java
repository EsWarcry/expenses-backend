package com.alvaro.gastos.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) //esta columna no puede ser nula.
    private LocalDate expenseDate;

    @CreationTimestamp // Esta anotación de Hibernate automáticamente establece la fecha y hora de creación
    @Column(nullable = false, updatable = false) // No puede ser nula y no se puede actualizar después de la creación
    private LocalDateTime createdAt; // Fecha y hora en que se registró el gasto en el sistema

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = true) //El kilometraje puede ser opcional para algunos tipos de gastos.
    private Double mileage;

    @Column(nullable = true)// Descripcion opcional del gasto.
    private String description;

    @Column(length = 1024, nullable = true) // Campo para almacenar la URL de una imagen asociada al gasto
    private String imageUrl; // URL de la imagen del recibo o factura (opcional)

    @ManyToOne // Define una relación de muchos a uno: muchos gastos pueden pertenecer a un solo usuario
    @JoinColumn(name = "user_id", nullable = false) // Especifica la columna de clave externa en la tabla 'expenses' que apunta al ID de la tabla 'app_users'
    private User user;

    @ManyToOne // Define una relación de muchos a uno con ExpenseType
    @JoinColumn(name = "expense_type_id", nullable = false) // Especifica la columna de clave externa
    private ExpenseType expenseType;
}
