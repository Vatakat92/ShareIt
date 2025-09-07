package ru.practicum.user;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto dto);

    UserDto updateUser(Long id, UserDto dto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    void deleteUser(Long id);

    List<User> getAllUsersLegacy();

    User saveUser(User user);
}
