package ru.practicum.item;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public List<Item> findByUserId(long userId) {
        return storage.values().stream()
                .filter(i -> i.getOwner() != null && Objects.equals(i.getOwner().getId(), userId))
                .toList();
    }

    @Override
    public Item save(Item item) {
        item.setId(nextId(item.getId()));
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> findAll() {
        return List.copyOf(storage.values());
    }

    private long nextId(Long currentId) {
        if (currentId == null) {
            return idSequence.incrementAndGet();
        }
        idSequence.updateAndGet(curr -> Math.max(curr, currentId));
        return currentId;
    }
}
