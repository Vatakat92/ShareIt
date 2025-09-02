package ru.practicum.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public List<Item> findByUserId(long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : storage.values()) {
            if (item.getUserId() != null && item.getUserId() == userId) {
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
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        Item current = storage.get(itemId);
        if (current != null && current.getUserId() != null && current.getUserId() == userId) {
            storage.remove(itemId);
        }
    }
}
