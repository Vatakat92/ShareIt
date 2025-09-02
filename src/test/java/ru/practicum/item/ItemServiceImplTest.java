package ru.practicum.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceImplTest {

    private ItemServiceImpl service;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        var userService = new FakeUserService();
        var itemRepository = new InMemoryItemRepository();
        service = new ItemServiceImpl(itemRepository, userService);
        ownerId = userService.createUser(new UserDto(null, "Owner", "owner@mail.com")).getId();
    }

    @Test
    void createItem_ok() {
        ItemDto created = service.createItem(new ItemDto(null, "Drill", "Desc",
                true, null), ownerId);
        assertNotNull(created.getId());
        assertEquals("Drill", created.getName());
        assertTrue(created.getAvailable());
    }

    @Test
    void createItem_ownerNotFound_404() {
        assertThrows(NoSuchElementException.class,
                () -> service.createItem(new ItemDto(null, "D", "d",
                        true, null), 999L));
    }

    @Test
    void updateItem_forbidden_403() {
        ItemDto created = service.createItem(new ItemDto(null, "Drill", "Desc",
                true, null), ownerId);
        assertThrows(SecurityException.class,
                () -> service.updateItem(created.getId(), new ItemDto(null, "X",
                        null, null, null), ownerId + 1));
    }

    @Test
    void search_items_caseInsensitive_and_onlyAvailable() {
        service.createItem(new ItemDto(null, "Hammer", "Strong hammer",
                true, null), ownerId);
        service.createItem(new ItemDto(null, "HAM Radio", "Device",
                false, null), ownerId);
        service.createItem(new ItemDto(null, "Screwdriver", "small hammer",
                true, null), ownerId);

        var res = service.searchItems("ham");
        assertEquals(2, res.size());
        assertTrue(res.stream().allMatch(ItemDto::getAvailable));
    }

    @Test
    void getAllItemsByOwner_sortedByIdAsc() {
        ItemDto i2 = service.createItem(new ItemDto(null, "B", "b",
                true, null), ownerId);
        ItemDto i1 = service.createItem(new ItemDto(null, "A", "a",
                true, null), ownerId);
        ItemDto i3 = service.createItem(new ItemDto(null, "C", "c",
                true, null), ownerId);

        var list = service.getAllItemsByOwner(ownerId);
        assertEquals(3, list.size());
        assertTrue(list.get(0).getId() < list.get(1).getId() && list.get(1).getId() < list.get(2).getId());
    }

    @Test
    void searchItems_sortedByIdAsc() {
        ItemDto i2 = service.createItem(new ItemDto(null, "xx hammer", "desc2",
                true, null), ownerId);
        ItemDto i1 = service.createItem(new ItemDto(null, "hammer alpha", "desc1",
                true, null), ownerId);
        ItemDto i3 = service.createItem(new ItemDto(null, "yy hammer", "desc3",
                true, null), ownerId);

        var list = service.searchItems("hammer");
        assertEquals(3, list.size());
        assertTrue(list.get(0).getId() < list.get(1).getId() && list.get(1).getId() < list.get(2).getId());
    }

    static class FakeUserService implements UserService {
        private final Map<Long, User> users = new HashMap<>();
        private long seq = 0;

        @Override
        public UserDto createUser(UserDto dto) {
            long id = ++seq;
            users.put(id, new User(id, dto.getName(), dto.getEmail()));
            return new UserDto(id, dto.getName(), dto.getEmail());
        }

        @Override
        public UserDto updateUser(Long id, UserDto dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserDto getUserById(Long id) {
            User u = users.get(id);
            if (u == null) throw new NoSuchElementException("User not found: " + id);
            return new UserDto(u.getId(), u.getName(), u.getEmail());
        }

        @Override
        public java.util.List<UserDto> getAllUsers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteUser(Long id) {
            users.remove(id);
        }

        @Override
        public java.util.List<User> getAllUsersLegacy() {
            return new ArrayList<>(users.values());
        }

        @Override
        public User saveUser(User user) {
            users.put(user.getId(), user);
            return user;
        }
    }
}
