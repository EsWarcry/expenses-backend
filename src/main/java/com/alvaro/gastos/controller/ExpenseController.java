package com.alvaro.gastos.controller;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.ExpenseDTO;
import com.alvaro.gastos.response.ExpenseResponse;
import com.alvaro.gastos.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de gastos.
 * Expone los endpoints para crear, obtener, actualizar y eliminar registros de gastos.
 */
@RestController
@RequestMapping("/api/v1/expenses")
@Validated
@CrossOrigin(origins = {"http://localhost:4200"})
public class ExpenseController {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDTO>> createExpense(
            @RequestPart("expense") ExpenseDTO expenseDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        logger.info("Solicitud para crear gasto para User ID: {}", expenseDTO.getUserId());

        try {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path uploadDir = Paths.get("D:/Proyectos/Gastos/uploads");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path destination = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Guardar la URL accesible en el DTO
                expenseDTO.setImageUrl("/uploads/" + fileName);
            }

            ApiResponse<ExpenseDTO> response = expenseService.createExpense(expenseDTO);

            if ("success".equals(response.getStatus())) {
                return new ResponseEntity<>(response, HttpStatus.CREATED); //201
            } else {
                // Diferenciar el tipo de error para devolver el HttpStatus correcto
                if (response.getMessage().contains("no encontrado")) {
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); //404
                } else if (response.getMessage().contains("Error interno")) {
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); //500
                }
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); //400
            }

        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); //500
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> getExpenseById(@PathVariable Long id) {
        logger.info("Solicitud para obtener gasto por ID {}", id);
        ApiResponse<ExpenseDTO> response = expenseService.getExpenseById(id);

        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 ok
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 not found
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getExpensesByUserId(@PathVariable Long userId) {
        logger.info("Solicitud para obtener gasto por usuario ID {}", userId);

        ApiResponse<List<ExpenseDTO>> response = expenseService.getExpensesByUserId(userId);

        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/user/keycloak/{keycloakId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpensesByKeycloakId(@PathVariable String keycloakId) {
        logger.info("Solicitud para obtener gasto por usuario ID {}", keycloakId);

        ApiResponse<ExpenseResponse> response = expenseService.getExpensesByKeycloakId(keycloakId);

        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/user/keycloak/{keycloakId}/month/{month}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpesesByUserAndMonth(@PathVariable String keycloakId, @PathVariable int month) {
        logger.info("Solicitud para obtener lista de gastos por usuario {}", keycloakId);

        ApiResponse<ExpenseResponse> response = expenseService.getExpensesByUserAndMonth(keycloakId, month);

        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/keycloak/{keycloakId}/range")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpensesByUserAndDateRange(@PathVariable String keycloakId,
                                                                         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Solicitud para obtener gastos por rango de fechas. keycloakId={}, startDate={}, endDate={}", keycloakId, startDate, endDate);
        ApiResponse<ExpenseResponse> response = expenseService.getExpensesByUserAndDateRange(keycloakId, startDate, endDate);
        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Endpoint para obtener todos los registros de gastos de la aplicación.
     * (Puede requerir permisos de administrador en un sistema real).
     *
     * @return ResponseEntity con un ApiResponse que contiene una lista de todos los ExpenseDTOs.
     */
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getAllExpenses() {

        logger.info("Solicitud para obtener lista todos los gastos.");
        ApiResponse<List<ExpenseDTO>> response = expenseService.getAllExpenses();
        return new ResponseEntity<>(response, HttpStatus.OK); // Siempre 200 OK, incluso si la lista está vacía
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        logger.info("Solicitud de actualizar gasto con ID {}", id);

        ApiResponse<ExpenseDTO> response = expenseService.updateExpense(id, expenseDTO);

        if ("success".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // El servicio ya devuelve mensajes específicos (no encontrado, usuario/tipo no válido, error interno)
            if (response.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found si el gasto o alguna de sus relaciones no existe
            } else if (response.getMessage().contains("Error interno")) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error para errores inesperados
            }
            // Para otros errores (ej. validaciones de negocio más complejas), se mantiene BAD_REQUEST
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request para otros errores
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        logger.info("Solicitud para eliminar gasto con ID: {}", id);
        ApiResponse<Void> response = expenseService.deleteExpense(id);

        if ("success".equals(response.getStatus())) {
            // Cuando la eliminación es exitosa, se devuelve un 204 No Content SIN cuerpo.
            return ResponseEntity.noContent().build();
        } else {
            // Para errores (por ejemplo, recurso no encontrado o error interno), se devuelve el ApiResponse con el error.
            if (response.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found si no se encontró para eliminar
            } else if (response.getMessage().contains("Error interno")) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error para errores inesperados
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request para otros errores
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            //String uploadDir = "uploads/expenses/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("D:/Proyectos/Gastos/uploads/");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }


            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imagen");
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportExpesesToExcel(@RequestBody List<ExpenseDTO> expenses) {

        byte[] excelFile = expenseService.exportExpensesToExcel(expenses);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gastos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.seheet"))
                .body(excelFile);
    }

}
