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

    @Override
    public List<User> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public User save(User user) {
        user.setId(nextId(user.getId()));
        storage.put(user.getId(), user);
        return user;
    }

    private long nextId(Long currentId) {
        if (currentId == null) {
            return idSequence.incrementAndGet();
        }
        idSequence.updateAndGet(curr -> Math.max(curr, currentId));
        return currentId;
    }

}
