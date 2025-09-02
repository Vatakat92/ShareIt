package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "name must not be blank", groups = {Create.class})
    @Size(min = 1, message = "name must not be blank", groups = {Update.class})
    private String name;
    @NotBlank(message = "description must not be blank", groups = {Create.class})
    @Size(min = 1, message = "description must not be blank", groups = {Update.class})
    private String description;
    @NotNull(message = "available must not be null", groups = {Create.class})
    private Boolean available;
    private Long requestId;
}
