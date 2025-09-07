package ru.practicum.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public List<User> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public User save(User user) {
        long id = idSequence.incrementAndGet();
        user.setId(id);
        storage.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        storage.put(id, user);
        return user;
    }
}
