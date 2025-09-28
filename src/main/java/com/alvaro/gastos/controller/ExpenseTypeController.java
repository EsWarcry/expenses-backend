package com.alvaro.gastos.controller;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.entities.ExpenseType;
import com.alvaro.gastos.service.ExpenseTypeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expense-types")
@Validated
@CrossOrigin(origins = {"http://localhost:4200"})

public class ExpenseTypeController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseTypeController.class);

    private final ExpenseTypeService expenseTypeService;

    public ExpenseTypeController(ExpenseTypeService expenseTypeService) {
        this.expenseTypeService = expenseTypeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseType>> createExpenseType(@Valid @RequestBody ExpenseType expenseType){
        logger.info("Solicitud de registro, tipo de gasto: {}", expenseType.getName());

        ApiResponse<ExpenseType> response = expenseTypeService.createExpenseType(expenseType);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created si la creación fue exitosa
        } else if (response.getMessage().contains("ya existe")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict si ya existe un recurso con ese nombre
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request para otros errores de negocio
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseType>> getExpenseTypeById(@PathVariable Long id){
        logger.info("Solicitud para obtener usuario por ID: {}", id);

        ApiResponse<ExpenseType> response = expenseTypeService.getExpenseTypeById(id);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK); //200
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ExpenseType>> getExpenseTypeByName(@PathVariable String name){
        logger.info("Solicitud para obtener tipo de gasto por nombre: {}", name);
        ApiResponse<ExpenseType> response = expenseTypeService.getExpenseTypeByName(name);
        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 Ok
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 not found
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseType>>> getAllExpenseType(){
        logger.info("Solicitud para obtener lista de tipos de gasto");
        ApiResponse<List<ExpenseType>> response = expenseTypeService.getAllExpenseTypes();
        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para actualizar un tipo de gasto existente.
     *
     * @param expenseType el objeto ExpenseType con los datos actualizados.
     * @param id El ID del tipo de gasto a actualizar.
     * @return ResponseEntity con un ApiResponse que contiene el ExpenseType actualizado o un mensaje de error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseType>> updateExpenseType(@Valid @RequestBody ExpenseType expenseType, @PathVariable Long id){
        logger.info("Solicitud para actualizar tipo de gasto con ID {}", id, expenseType.getName());

        ApiResponse<ExpenseType> response = expenseTypeService.updateExpenseType(expenseType, id);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (response.getMessage().contains("ya existe")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else if (response.getMessage().contains("no encontrado")) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping("id")
    public ResponseEntity<ApiResponse<Void>> deleteExpenseType(@PathVariable Long id){
        logger.info("Solicitud para eliminar tipo de gasto con ID: {}", id);

        ApiResponse<Void> response = expenseTypeService.deleteExpenseType(id);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);// 204 No Content para eliminación exitosa (sin cuerpo)
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); //404
        }
    }

    @GetMapping("/searchExpenseType")
    public ResponseEntity<ApiResponse<List<ExpenseType>>> searchExpensesType(@RequestParam String name){
        ApiResponse<List<ExpenseType>> response = expenseTypeService.searchExpensesType(name);
        return ResponseEntity.ok(response);
    }
}
