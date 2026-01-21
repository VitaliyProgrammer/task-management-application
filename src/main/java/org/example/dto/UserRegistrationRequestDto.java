package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.validation.PasswordMatch;

@Data
@PasswordMatch
public class UserRegistrationRequestDto {

    @Email(message = "{email.invalid}")
    @NotBlank(message = "{email.notBlank}")
    private String email;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, message = "{password.size}")
    private String password;

    @NotBlank(message = "{repeatPassword.notBlank}")
    private String repeatPassword;

    @NotBlank(message = "{firstName.notBlank}")
    private String firstName;

    @NotBlank(message = "{lastName.notBlank}")
    private String lastName;
}
