package ru.practicum.user;

import ru.practicum.user.dto.UserDto;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

@Validated
public interface UserService {
    // DTO-based API
    @Validated(Create.class)
    UserDto createUser(@Valid UserDto dto);

    @Validated(Update.class)
    UserDto updateUser(@NotNull Long id, @Valid UserDto dto);

    UserDto getUserById(@NotNull Long id);

    List<UserDto> getAllUsers();

    void deleteUser(@NotNull Long id);

    // Backward compatibility for existing controller (domain-based)
    List<User> getAllUsersLegacy();

    User saveUser(User user);
}
