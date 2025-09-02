package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "name must not be blank", groups = {Create.class})
    @Size(min = 1, message = "name must not be blank", groups = {Update.class})
    private String name;
    @NotBlank(message = "email must not be blank", groups = {Create.class})
    @Email(message = "email must be a well-formed email address", groups = {Create.class, Update.class})
    private String email;
}
