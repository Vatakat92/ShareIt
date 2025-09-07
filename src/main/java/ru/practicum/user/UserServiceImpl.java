package ru.practicum.user;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.user.dto.UserDto;
import ru.practicum.exception.ConflictException;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@Validated
public class UserServiceImpl implements UserService {
    private static final Comparator<User> BY_ID = Comparator.comparing(User::getId);
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public UserDto createUser(UserDto dto) {
        log.info("Create user: email={}", dto != null ? dto.getEmail() : null);
        validateNewEmail(dto);
        User user = UserMapper.toUser(dto);
        long id = seq.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        log.info("User created: id={} email={}", id, user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        log.info("Update user: id={} email={} name={}", id,
                dto != null ? dto.getEmail() : null,
                dto != null ? dto.getName() : null);

        User existing = users.get(id);
        if (existing == null) {
            log.warn("User not found: {}", id);
            throw new NoSuchElementException("User not found: " + id);
        }

        if (dto == null) {
            log.error("Null UserDto passed to updateUser for id={}", id);
            throw new IllegalArgumentException("UserDto must not be null");
        }

        if (dto.getEmail() == null && dto.getName() == null) {
            return UserMapper.toUserDto(existing);
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            ensureEmailUnique(dto.getEmail(), id);
            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        log.info("User updated: id={}", id);
        return UserMapper.toUserDto(existing);
    }


    @Override
    public UserDto getUserById(Long id) {
        log.info("Get user: id={}", id);
        User user = users.get(id);
        if (user == null) {
            log.warn("User not found: {}", id);
            throw new NoSuchElementException("User not found: " + id);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Get all users");
        return users.values().stream()
                .sorted(BY_ID)
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Delete user: id={}", id);
        if (users.remove(id) == null) {
            log.warn("User not found for delete: {}", id);
            throw new NoSuchElementException("User not found: " + id);
        }
    }

    @Override
    public List<User> getAllUsersLegacy() {
        return users.values().stream()
                .sorted(BY_ID)
                .toList();
    }

    @Override
    public User saveUser(User user) {
        UserDto dto = UserMapper.toUserDto(user);
        UserDto saved = createUser(dto);
        return UserMapper.toUser(saved);
    }

    private void validateNewEmail(UserDto dto) {
        if (dto == null) throw new IllegalArgumentException("UserDto is null");
        String email = dto.getEmail();
        ensureEmailUnique(email, null);
        dto.setEmail(email);
    }

    private void ensureEmailUnique(String email, Long selfId) {
        boolean exists = users.values().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()) && !Objects.equals(u.getId(), selfId));
        if (exists) throw new ConflictException("Email must be unique");
    }
}
