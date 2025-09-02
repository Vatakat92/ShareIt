# ShareIt (Спринт 1) — краткое описание API

## Общее
In-memory реализация Users и Items согласно ТЗ спринта. DTO и мапперы отделяют API-модели от сущностей. Валидация выполняется через Jakarta Bean Validation как на уровне контроллеров, так и на уровне сервисов (метод-валидация с группами для users и items).

## Технологии
- Java, Spring Boot
- Jakarta Bean Validation (JSR-380)
- In-memory repositories
- Build: Maven

## Запуск
- Тесты: `mvn test`
- Приложение: `mvn spring-boot:run` (порт по умолчанию 8080)

## Правила и соглашения
- Заголовок `X-Sharer-User-Id` обязателен для эндпоинтов вещей, где требуется идентификация владельца.
- Email хранится в исходном регистре; уникальность проверяется без учёта регистра (case-insensitive).
- Валидация:
  - Users:
    - Create: `@Validated(Create.class)` на теле `UserDto` в контроллере.
    - Update (PATCH): `@Validated(Update.class)` — частичные обновления, поля опциональны; если переданы, `name` — не пустая строка, `email` — валидный адрес. Уникальность гарантируется сервисом.
  - Items:
    - Create: группа `Create` — обязательны `name`, `description` (не пустые), `available` (не null).
    - Update (PATCH): группа `Update` — поля опциональны (null допустим); если переданы, `name`/`description` — не пустые строки.

## Эндпоинты

### Users
- POST `/users`
  - Тело: `{ name: string (not blank), email: email (not blank) }`
  - 201 Created, `Location: /users/{id}`
  - 400 при невалидном DTO, 409 при дублирующемся email (без учёта регистра)
- PATCH `/users/{id}`
  - Тело (Update group): частичное `{ name?: string, email?: email }`
  - No-op при пустом теле; регистр email сохраняется
  - 404 если пользователь не найден; 400 при невалидном email; 409 при конфликте уникальности
- GET `/users/{id}` — 200 или 404
- GET `/users` — все пользователи, отсортированы по id
- DELETE `/users/{id}` — идемпотентно

### Items
- POST `/items`
  - Заголовки: `X-Sharer-User-Id: <ownerId>`
  - Тело (Create group): `{ name: non-blank, description: non-blank, available: boolean, requestId?: long }`
  - 201 Created, `Location: /items/{id}`
  - 400 при невалидном DTO; 404 если владелец не найден
- PATCH `/items/{itemId}`
  - Заголовки: `X-Sharer-User-Id: <ownerId>`
  - Тело (Update group): частичное; если переданы, `name`/`description` — не пустые строки; `available` — опционально
  - 403 если не владелец; 404 если вещь не найдена; 404 если владелец не найден
- GET `/items/{itemId}` — 200 или 404
- GET `/items` (вещи владельца)
  - Заголовки: `X-Sharer-User-Id: <ownerId>`
  - Возвращает вещи владельца, отсортированные по id
- GET `/items/search?text=<query>`
  - Поиск по `name`/`description`, без учёта регистра
  - Возвращает только доступные вещи
  - Пустой или пробельный `text` возвращает `[]`

## Примечания
- Пакеты `booking/` и `request/` — заготовки для следующих спринтов.

