package ru.practicum.item;

import ru.practicum.item.dto.ItemDto;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

@Validated
public interface ItemService {
    @Validated(Create.class)
    ItemDto createItem(@Valid ItemDto dto, @NotNull Long ownerId);

    @Validated(Update.class)
    ItemDto updateItem(@NotNull Long itemId, @Valid ItemDto dto, @NotNull Long ownerId);

    ItemDto getItemById(@NotNull Long itemId);

    List<ItemDto> getAllItemsByOwner(@NotNull Long ownerId);

    List<ItemDto> searchItems(String text);
}
