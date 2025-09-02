package ru.practicum.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    public InMemoryUserRepository() {
        // немного тестовых данных
        save(preset(1L, "mail1@example.com", "Akakiy Akakievich #1"));
        save(preset(2L, "mail2@example.com", "Akakiy Akakievich #2"));
        save(preset(3L, "mail3@example.com", "Akakiy Akakievich #3"));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idSequence.incrementAndGet());
        } else {
            // выравниваем последовательность, если руками проставили id
            idSequence.updateAndGet(curr -> Math.max(curr, user.getId()));
        }
        storage.put(user.getId(), user);
        return user;
    }

    private User preset(Long id, String email, String name) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setName(name);
        return u;
    }
}
