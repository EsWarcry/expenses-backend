package com.alvaro.gastos.exception;

import com.alvaro.gastos.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase global para el manejo de excepciones en la aplicación.
 * Utiliza @ControllerAdvice para interceptar y manejar excepciones de forma centralizada,
 * devolviendo respuestas en el formato ApiResponse definido por la aplicación.
 */
@ControllerAdvice // Indica que esta clase proporciona manejo de excepciones global en toda la aplicación
public class GlobalExceptionHandler {
    /**
     * Maneja las excepciones de validación que ocurren cuando los argumentos de un método
     * del controlador anotados con @Valid fallan la validación.
     *
     * @param ex La excepción MethodArgumentNotValidException que contiene los errores de validación.
     * @return ResponseEntity con un ApiResponse que encapsula el estado "error",
     * un mensaje general y un mapa de errores específicos por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName =null ;
            String errorMessage = null;

            if (error instanceof FieldError){
                fieldName = ((FieldError) error).getField();
                errorMessage = error.getDefaultMessage();
            } else if(error instanceof ObjectError){
                fieldName = error.getObjectName();
                errorMessage = error.getDefaultMessage();
            }
            if (fieldName != null){
                errors.put(fieldName, errorMessage);
            }

        });

        // Crea una respuesta ApiResponse con estado "error" y los detalles de validación en el campo 'data'
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                "error",
                "Error de validación en los datos proporcionados.",
                errors
        );

        // Devuelve una ResponseEntity con el código de estado HTTP 400 (Bad Request)
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Aquí se pueden añadir más métodos @ExceptionHandler para manejar otros tipos de excepciones
    // por ejemplo, ResourceNotFoundException (si no se encuentra un recurso), UnauthorizedException,
    // o un manejador general para Exception.class para errores inesperados.
    /*
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(
            "error",
            ex.getMessage(),
            null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND); // 404 Not Found
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        // Loguea el error para fines de depuración/monitoreo
        // logger.error("Ocurrió un error inesperado: {}", ex.getMessage(), ex);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
            "error",
            "Ocurrió un error interno en el servidor. Por favor, inténtelo de nuevo más tarde.",
            null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
    */

}
