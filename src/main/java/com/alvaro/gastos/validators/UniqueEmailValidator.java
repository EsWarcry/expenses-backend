package com.alvaro.gastos.validators;

import com.alvaro.gastos.repository.UserRepository;
import com.alvaro.gastos.validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired
    private UserRepository userRepository;

    public boolean isValid(String email, ConstraintValidatorContext context){
        if (email == null || email.isEmpty()){
            return true;
        }
        return !userRepository.findByEmail(email).isPresent();
    }
}
