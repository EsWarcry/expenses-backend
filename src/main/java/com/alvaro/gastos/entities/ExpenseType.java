package com.alvaro.gastos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Table(name = "expense_types")
public class ExpenseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tipo de gasto no puede estar en blanco")
    @Size(max = 100, message = "El nombre del tipo de gasto no puede exceder los 100 caracteres")
    @Column(nullable = false, unique = true)
    private String name;

}
