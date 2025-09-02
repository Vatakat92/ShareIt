package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private static final Comparator<Item> BY_ID = Comparator.comparing(Item::getId);

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto createItem(ItemDto dto, Long ownerId) {
        log.info("Create item: ownerId={} name={}", ownerId, dto != null ? dto.getName() : null);
        validateNewItem(dto, ownerId);

        User owner = UserMapper.toUser(userService.getUserById(ownerId));
        Item item = ItemMapper.toItem(dto, owner);

        item = itemRepository.save(item);
        log.info("Item created: id={} ownerId={}", item.getId(), ownerId);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto dto, Long ownerId) {
        log.info("Update item: id={} ownerId={} name={} available={}",
                itemId, ownerId, dto != null ? dto.getName() : null, dto != null ? dto.getAvailable() : null);

        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));

        checkOwner(existing, ownerId);
        validateItemDtoForUpdate(dto);

        updateItemFields(existing, dto);
        itemRepository.save(existing);

        log.info("Item updated: id={}", itemId);
        return ItemMapper.toItemDto(existing);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Get item: id={}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        log.info("Get all items by owner: {}", ownerId);
        return itemRepository.findByUserId(ownerId).stream()
                .sorted(BY_ID)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Search items by text: '{}'", text);
        if (text == null || text.isBlank()) return List.of();

        String query = text.toLowerCase(Locale.ROOT);
        return itemRepository.findAll().stream()
                .filter(item -> matches(item, query))
                .sorted(BY_ID)
                .map(ItemMapper::toItemDto)
                .toList();
    }


    private void validateNewItem(ItemDto dto, Long ownerId) {
        // Method-level validation handles dto and fields; ensure owner exists
        userService.getUserById(ownerId);
    }

    private void validateItemDtoForUpdate(ItemDto dto) {
        // Method-level validation with Update group checks payload shape; no-op here
    }

    private void checkOwner(Item item, Long ownerId) {
        Long actualOwnerId = item.getOwner() != null ? item.getOwner().getId() : null;
        if (!Objects.equals(actualOwnerId, ownerId)) {
            log.warn("Forbidden operation by user {} on item {}", ownerId, item.getId());
            throw new SecurityException("Only owner can perform this operation");
        }
    }

    private void updateItemFields(Item existing, ItemDto dto) {
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existing.setAvailable(dto.getAvailable());
    }

    private static boolean matches(Item item, String query) {
        String name = item.getName();
        String desc = item.getDescription();
        return Boolean.TRUE.equals(item.getAvailable()) &&
                ((name != null && name.toLowerCase(Locale.ROOT).contains(query)) ||
                        (desc != null && desc.toLowerCase(Locale.ROOT).contains(query)));
    }
}
