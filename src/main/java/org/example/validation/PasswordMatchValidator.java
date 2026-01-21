package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.dto.UserRegistrationRequestDto;

public class PasswordMatchValidator implements
        ConstraintValidator<PasswordMatch, UserRegistrationRequestDto> {

    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true;
        }

        if (dto.getPassword() == null || dto.getRepeatPassword() == null) {
            return true;
        }
        return dto.getPassword().equals(dto.getRepeatPassword());
    }
}
