package com.alvaro.gastos.validators;

import com.alvaro.gastos.dto.RequestContext;
import com.alvaro.gastos.dto.UserDTO;
import com.alvaro.gastos.entities.User;
import com.alvaro.gastos.repository.UserRepository;
import com.alvaro.gastos.validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, UserDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext context) {
        // Obtener el ID del usuario actual de la variable ThreadLocal
        Integer userId = RequestContext.getCurrentId();

        // Validar username
        Optional<User> existingUserByUsername = userRepository.findByUsername(userDTO.getUsername());
        if (existingUserByUsername.isPresent()) {
            // Si el ID del usuario en la base de datos no es el mismo que el del hilo,
            // significa que el username ya está en uso por otro usuario.
            if (!existingUserByUsername.get().getId().equals(userId)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("El nombre de usuario ya existe")
                        .addPropertyNode("username")
                        .addConstraintViolation();
                return false;
            }
        }

        // Validar email
        Optional<User> existingUserByEmail = userRepository.findByEmail(userDTO.getEmail());
        if (existingUserByEmail.isPresent()) {
            // Si el ID del usuario en la base de datos no es el mismo que el del hilo,
            // significa que el email ya está en uso por otro usuario.
            if (!existingUserByEmail.get().getId().equals(userId)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("El correo electrónico ya existe")
                        .addPropertyNode("email")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
