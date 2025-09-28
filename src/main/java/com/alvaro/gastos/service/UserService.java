package com.alvaro.gastos.service;


import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.UserDTO;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de usuarios.
 * Define las operaciones de negocio relacionadas con los usuarios.
 */
public interface UserService {

    /**
     * Registra un nuevo usuario en la aplicación y, conceptualmente, también en Keycloak.
     *
     * @param userDTO El DTO del usuario con los datos de registro.
     * @return Un ApiResponse indicando el éxito o fracaso del registro,
     * y potencialmente el UserDTO del usuario registrado.
     */
    ApiResponse<UserDTO> registerUser(UserDTO userDTO);

    ApiResponse<UserDTO> updateUser(UserDTO userDTO, Long id);

    ApiResponse<UserDTO> getUserById(Long id);
    ApiResponse<List<UserDTO>> getUsers();

    /**
     * Obtiene los detalles de un usuario por su ID de Keycloak.
     * Esto será útil para cuando la autenticación desde Keycloak nos dé solo el keycloakId.
     *
     * @param keycloakId El ID único del usuario en Keycloak.
     * @return Un ApiResponse con el UserDTO del usuario encontrado o un mensaje de error.
     */
    ApiResponse<UserDTO> getUserByKeycloadId(String keycloakId);

    ApiResponse<List<UserDTO>> searchUsers(String keyword);
    ApiResponse<Void> deleteUser(Long id);

}
