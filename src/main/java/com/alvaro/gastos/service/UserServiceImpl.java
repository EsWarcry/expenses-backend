package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.UserDTO;
import com.alvaro.gastos.entities.User;
import com.alvaro.gastos.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    // Inyección de dependencia del UserRepository a través del constructor
    public UserServiceImpl(UserRepository userRepository, KeycloakUserService keycloakUserService, EmailService emailService) {
        this.userRepository = userRepository;
        this.keycloakUserService = keycloakUserService;
        this.emailService = emailService;
    }


    @Transactional
    @Override
    public ApiResponse<UserDTO> registerUser(UserDTO userDTO) {

        try {
            String keycloakId = keycloakUserService.createUser(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    userDTO.getFirstName(),
                    userDTO.getLastName()
            );

            String tempPassword = generateRandomPassword();
            keycloakUserService.setTemporaryPassword(keycloakId, tempPassword);

            User user = new User();
            user.setKeycloakId(keycloakId);
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPhone(userDTO.getPhone());

            User saveUser = userRepository.save(user);
            logger.info("Usuario registrado y guardado en la BD con ID: {}", saveUser.getId());

            emailService.sendTemporaryPassword(user.getEmail(), user.getUsername(), tempPassword);

            UserDTO responseDTO = new UserDTO();
            responseDTO.setId(saveUser.getId());
            responseDTO.setKeycloakId(saveUser.getKeycloakId());
            responseDTO.setUsername(saveUser.getUsername());
            responseDTO.setEmail(saveUser.getEmail());
            responseDTO.setFirstName(saveUser.getFirstName());
            responseDTO.setLastName(saveUser.getLastName());
            responseDTO.setPhone(saveUser.getPhone());
            return new  ApiResponse<>("Usuario registrado exitosamente.", responseDTO);

        } catch (Exception e){
            logger.error("Error al registrar el usuario en la BD: {}", e.getMessage(), e);
            return new ApiResponse<>("error", "Error interno al registrar el usuario.", null);
        }

    }

    private String generateRandomPassword(){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i<12; i++){
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public ApiResponse<UserDTO> updateUser(UserDTO userDTO, Long id) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()){
            logger.warn("Intento de actualizar usuario no encontrado con el ID {}", id);
            return new ApiResponse<>("Error", "Usuario no encontrado", null);
        }

        User userToUpdate = userOptional.get();

        userToUpdate.setUsername(userDTO.getUsername());
        userToUpdate.setEmail(userDTO.getEmail());
        userToUpdate.setFirstName(userDTO.getFirstName());
        userToUpdate.setLastName(userDTO.getLastName());
        userToUpdate.setPhone(userDTO.getPhone());

        try{
            User userUpdate =  userRepository.save(userToUpdate);
            logger.info("Usuario con ID {} actualizado exitosamente.", userUpdate.getId());

            UserDTO responseDTO = new UserDTO();
            responseDTO.setId(userUpdate.getId());
            responseDTO.setKeycloakId(userUpdate.getKeycloakId());
            responseDTO.setUsername(userUpdate.getUsername());
            responseDTO.setEmail(userUpdate.getEmail());
            responseDTO.setFirstName(userUpdate.getFirstName());
            responseDTO.setLastName(userUpdate.getLastName());
            responseDTO.setPhone(userUpdate.getPhone());

            return new ApiResponse<>("Usuario actualizado exitosamente.", responseDTO);

        }catch (Exception e){
            logger.warn("Error al actualizar el usuario en la BD: {}", e.getMessage());
            return new ApiResponse<>("error", "Error interno al actualizar el usuario.", null);
        }

    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<UserDTO> getUserById(Long id) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()){
            User user = userOptional.get();

            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setKeycloakId(user.getKeycloakId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setPhone(user.getPhone());

            return new ApiResponse<>("Usuario encontrado.", userDTO);
        }else {
            logger.warn("Usuario no encontrado con ID: {}", id);
            return new ApiResponse<>("error", "Usuario no encontrado.", null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UserDTO>> getUsers() {
        List<User> userList = userRepository.findAll();

        if (!userList.isEmpty()){
            List<UserDTO> userDTOS = new ArrayList<>();

            for (User user: userList){
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setKeycloakId(user.getKeycloakId());
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                userDTO.setFirstName(user.getFirstName());
                userDTO.setLastName(user.getLastName());
                userDTO.setPhone(user.getPhone());
                userDTOS.add(userDTO);
            }
            return new ApiResponse<>("Usuarios encontrados.", userDTOS);
        } else {
            logger.warn("No se encontraron usuarios en la base de datos.");
            return new ApiResponse<>("info", "No se encontraron usuairos.", null);
        }
    }
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<UserDTO> getUserByKeycloadId(String keycloakId) {

        Optional<User> userOptional = userRepository.findByKeycloakId(keycloakId);

        if (userOptional.isPresent()){
            User user = userOptional.get();
            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setKeycloakId(user.getKeycloakId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setPhone(user.getPhone());
            return new ApiResponse<>("Usuario encontrado por Keycloak ID.", userDTO);
        } else {
            logger.warn("Usuario no encontrado con Keycloak ID: {}", keycloakId);
            return new ApiResponse<>("error", "Usuario no encontrado por Keycloak ID.", null);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<UserDTO>> searchUsers(String keyword) {
        logger.info("solicitud de busqueda de usuario con keyword: {}", keyword);
        List<User> userList = userRepository.searchUsers(keyword);
        if (userList.isEmpty()){
            return new ApiResponse<>("info", "No se encontraron usuarios que coincidan con la busqueda", null);
        }

        List<UserDTO> userDTOs = userList.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setPhone(user.getPhone());
            return userDTO;
        }).toList();

        return new ApiResponse<>("success", "Busqueda exitosa", userDTOs);
    }

    @Override
    public ApiResponse<Void> deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()){
            userRepository.deleteById(user.get().getId());
            return new ApiResponse<>("success", "Usuario eliminado con exito", null);
        }
        return new ApiResponse<>("error", "Usuario no encontrado", null);
    }
}
