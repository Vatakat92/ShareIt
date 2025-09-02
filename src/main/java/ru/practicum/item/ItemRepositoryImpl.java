package ru.practicum.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public List<Item> findByUserId(long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : storage.values()) {
            if (item.getOwner() != null && item.getOwner().getId() != null && item.getOwner().getId() == userId) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            long id = idSequence.incrementAndGet();
            item.setId(id);
        }
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(storage.values());
    }
}
