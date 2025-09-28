package com.alvaro.gastos.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    private String status; //Ej: "success", "error", "warning"
    private String message;
    private LocalDateTime timestamp; //marca el tiempo de la respuesta
    private T data; // el payload de datos real. Ej: un User, List<User>, Category, etc.



    //Constructor para respuesta exitosas
    public ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    //Constructor para respuestas de error o con stado especifico
    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
