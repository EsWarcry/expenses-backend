package com.alvaro.gastos.controller;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.RequestContext;
import com.alvaro.gastos.dto.UserDTO;
import com.alvaro.gastos.service.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:4200"})
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    // Inyección de dependencia del UserService a través del constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * Recibe un UserDTO del frontend y lo pasa al servicio para su procesamiento.
     *
     * @param userDTO El UserDTO con los datos del nuevo usuario.
     * @return ResponseEntity con un ApiResponse que contiene el resultado del registro.
     */
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody UserDTO userDTO){
        logger.info("Solicitud de registro de usuario recibida para username: {}", userDTO.getUsername() );

        ApiResponse<UserDTO> response = userService.registerUser(userDTO);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created si el registro fue exitoso
        }else {
            if (response.getMessage().contains("ya está en uso")){
                return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict si el recurso ya existe
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request por defecto para errores de negocio del servicio
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable Long id){
        logger.info("Solicitud de actualizacion de dastos del usuario: {}", userDTO.getUsername());

        RequestContext.setCurrentId(id.intValue());

        try {
            ApiResponse<UserDTO> response = userService.updateUser(userDTO, id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de errores
            ApiResponse<UserDTO> errorResponse = new ApiResponse<>(
                    "error",
                    "Ocurrió un error al actualizar el usuario: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            RequestContext.clear();
        }


        /*if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            if (response.getMessage().contains("ya está en uso")){
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }*/
    }

    @ModelAttribute
    public void setCurrentId(@RequestParam(value = "id", required = false) Integer id ){
        if (id != null){
            RequestContext.setCurrentId(id);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id){
        logger.info("Solicitud para obtener usuario por ID: {}", id);
        ApiResponse<UserDTO> response = userService.getUserById(id);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK, si el usuario fue encontrado
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not found si el usuario no fue encontrado.
        }
    }
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers(){
        logger.info("Solicitud para obtener lista de usuarios");

        ApiResponse<List<UserDTO>> response = userService.getUsers();
        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK, si el usuario fue encontrado
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not found si el usuario no fue encontrado.
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(@RequestParam String keyword){
        logger.info("Solicitud para busqueda de usuario");
        ApiResponse<List<UserDTO>> response = userService.searchUsers(keyword);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long id){
        logger.info("Solicitud para eleminar un usuario ID: {}", id);
        ApiResponse<Void> response = userService.deleteUser(id);
        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users/keycloak/{keycloadId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByKeycloakId(@PathVariable String keycloadId){
        logger.info("Solicitud para obtener usuario por keycloak ID: {}", keycloadId);
        ApiResponse<UserDTO> response = userService.getUserByKeycloadId(keycloadId);

        if ("success".equals(response.getStatus())){
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK, si el usuario fue encontrado
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not found si el usuario no fue encontrado.
        }
    }

    /*private ResponseEntity<?> validation(BindingResult result){
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }*/




}
