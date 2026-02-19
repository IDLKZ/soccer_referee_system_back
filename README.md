# Soccer Referee System — Backend

REST API бэкенд для системы управления футбольными судьями. Построен на **Ktor** с использованием чистой архитектуры (Clean Architecture).

---

## Стек технологий

| Категория | Технология | Версия |
|---|---|---|
| Язык | Kotlin (JVM 21) | 2.0.21 |
| HTTP фреймворк | Ktor (Netty) | 3.0.1 |
| База данных | PostgreSQL | — |
| ORM | Exposed | 1.0.0-rc-4 |
| Миграции | Flyway | 11.18.0 |
| Connection Pool | HikariCP | 7.0.2 |
| DI | Koin | 4.0.0 |
| Сериализация | Kotlinx Serialization (JSON) | — |
| Документация API | OpenAPI / Swagger (smiley4) | 5.5.0 |
| Аутентификация | JWT (jjwt) | 0.13.0 |
| Валидация | Hibernate Validator | — |
| Кэширование | Redis (Jedis) | 5.2.0 |
| Мониторинг | Micrometer + OpenTelemetry | — |
| Логирование | Logback | 1.5.12 |
| Корутины | kotlinx-coroutines | 1.9.0 |
| Дата/время | kotlinx-datetime | 0.6.1 |

---

## Архитектура

Проект следует принципам **Clean Architecture** с разделением на четыре слоя:

```
┌─────────────────────────────────────────────┐
│              Presentation Layer              │
│         (HTTP Controllers, Routing)          │
├─────────────────────────────────────────────┤
│               Domain Layer                   │
│    (Use Cases, DTOs, Interfaces, Mappers)    │
├─────────────────────────────────────────────┤
│            Infrastructure Layer              │
│   (Datasource Impl, Filters, DB queries)     │
├─────────────────────────────────────────────┤
│                 Core Layer                   │
│   (Tables, Config, DI, Constraints, Utils)   │
└─────────────────────────────────────────────┘
```

### Core
Основа приложения — не зависит ни от чего выше:
- **`core/db/table/`** — определения таблиц Exposed ORM (`BasicLongTable`, `SoftDeleteAtTable`)
- **`core/config/`** — конфигурации (DB, Storage, Flyway, Swagger, Environment)
- **`core/di/`** — модули Koin (DatasourceModule, UseCaseModule, и др.)
- **`core/shared/`** — константы (`ApiRouteConstraints`, `DataConstraints`, `LocalizedMessageConstraints`)
- **`core/exception_handlers/`** — глобальная обработка исключений (400, 403, 404, 500 и др.)

### Domain
Чистая бизнес-логика без зависимостей на фреймворки:
- **`domain/usecase/`** — use case классы (реализуют `UseCaseTransaction`)
- **`domain/dto/`** — Data Transfer Objects (`RDTO` — ответ, `CDTO` — создание/обновление)
- **`domain/mapper/`** — extension функции `ResultRow → DTO`
- **`domain/datasource/`** — интерфейсы (`BaseDbDatasource<T>`, `FileService`, и др.)

### Infrastructure
Реализация интерфейсов domain-слоя:
- **`infrastructure/datasource/db/`** — реализации datasource (наследуют `BaseDbDatasourceImpl<T>`)
- **`infrastructure/datasource/filter/`** — фильтры для запросов (`BaseFilter`, `BasePaginationFilter`)

### Presentation
HTTP-слой — маршруты, контроллеры, OpenAPI-документация:
- **`presentation/http/`** — контроллеры (наследуют `KoinComponent`)
- **`Routing.kt`** — регистрация всех маршрутов
- OpenAPI-документация генерируется автоматически через аннотации smiley4

---

## Структура проекта

```
src/main/kotlin/
├── Application.kt                    # Точка входа
├── Routing.kt                        # Регистрация маршрутов
│
├── core/
│   ├── config/                       # Конфигурационные классы
│   │   ├── AppDatabaseConfig.kt
│   │   ├── AppEnvironmentConfig.kt
│   │   ├── AppFlyWayConfig.kt
│   │   ├── StorageConfig.kt
│   │   └── SwaggerConfig.kt
│   ├── db/table/                     # Таблицы Exposed ORM
│   │   ├── BasicTable.kt             # BasicLongTable, SoftDeleteAtTable
│   │   ├── file/FileTable.kt
│   │   ├── permission/PermissionTable.kt
│   │   ├── role/RoleTable.kt
│   │   ├── role_permission/RolePermissionTable.kt
│   │   ├── user/UserTable.kt
│   │   └── user_verification_code/UserVerificationCodeTable.kt
│   ├── di/                           # Koin DI модули
│   │   ├── DatasourceModule.kt
│   │   ├── UseCaseModule.kt
│   │   ├── usecase/                  # Модули по доменам
│   │   │   ├── FileUseCaseModule.kt
│   │   │   ├── RoleUseCaseModule.kt
│   │   │   ├── PermissionUseCaseModule.kt
│   │   │   ├── RolePermissionUseCaseModule.kt
│   │   │   └── UserUseCaseModule.kt
│   │   └── application/MainKoinModule.kt
│   ├── exception_handlers/api/       # API исключения
│   └── shared/constraints/           # Константы и ограничения
│
├── domain/
│   ├── datasource/
│   │   ├── db/                       # Интерфейсы datasource
│   │   │   ├── BaseDbDatasource.kt
│   │   │   ├── file/FileDatasource.kt
│   │   │   ├── role/RoleDatasource.kt
│   │   │   ├── permission/PermissionDatasource.kt
│   │   │   ├── role_permission/RolePermissionDatasource.kt
│   │   │   └── user/UserDatasource.kt
│   │   ├── filter/                   # Базовые фильтры + OpenAPI builders
│   │   │   ├── BaseFilter.kt
│   │   │   ├── BasePaginationFilter.kt
│   │   │   ├── BaseQueryParameter.kt
│   │   │   └── OpenApiParameterBuilder.kt
│   │   └── service/file/FileService.kt
│   ├── dto/                          # DTO по доменам
│   │   ├── BaseDTO.kt                # BaseCreateDTO, BaseUpdateDTO, BaseOneCreateDTO
│   │   ├── PaginationMeta.kt
│   │   ├── file/FileDTO.kt
│   │   ├── role/RoleDTO.kt
│   │   ├── permission/PermissionDTO.kt
│   │   ├── role_permission/RolePermissionDTO.kt
│   │   └── user/UserDTO.kt
│   ├── mapper/                       # ResultRow → DTO
│   │   ├── FileMapper.kt
│   │   ├── RoleMapper.kt
│   │   ├── PermissionMapper.kt
│   │   ├── RolePermissionMapper.kt
│   │   └── UserMapper.kt
│   └── usecase/
│       ├── shared/UseCaseTransaction.kt
│       ├── file/
│       │   ├── query/  (PaginateFileUseCase, GetFileByIdUseCase)
│       │   └── command/(CreateFileUseCase, BulkCreateFileUseCase,
│       │                UpdateFileUseCase, DeleteFileUseCase, DeleteBulkFileUseCase)
│       ├── role/
│       │   ├── query/  (PaginateRoleUseCase, GetAllRolesUseCase, GetRoleByIdUseCase)
│       │   └── command/(CreateRoleUseCase, UpdateRoleByIdCase, DeleteRoleByIdCase,
│       │                RestoreRoleByIdCase, BulkCreate/Update/Delete/RestoreRoleUseCase)
│       ├── permission/
│       │   ├── query/  (Paginate, GetAll, GetById, GetByValue)
│       │   └── command/(Create, Update, Delete, BulkCreate/Update/Delete)
│       ├── role_permission/
│       │   ├── query/  (AllRolePermission, PaginateAllRolePermission)
│       │   └── command/(CreateRolePermission, DeleteRolePermission)
│       └── user/
│           ├── query/  (PaginateUserUseCase, GetUserByIdUseCase)
│           └── command/(CreateUserUseCase)
│
├── infrastructure/
│   ├── datasource/db/                # Реализации datasource
│   │   ├── BaseDbDatasourceImpl.kt   # Базовая реализация (CRUD, paginate, bulk)
│   │   ├── file/FileDatasourceImpl.kt
│   │   ├── role/RoleDatasourceImpl.kt
│   │   ├── permission/PermissionDatasourceImpl.kt
│   │   ├── role_permission/RolePermissionDatasourceImpl.kt
│   │   └── user/UserDatasourceImpl.kt
│   └── datasource/filter/            # Конкретные фильтры
│       ├── file/  (FileFilter, FileParameter)
│       ├── role/  (RoleFilter, RoleQueryParameter)
│       ├── permission/
│       ├── role_permission/
│       └── user/  (UserFilter, UserParameter)
│
└── presentation/
    └── http/
        ├── shared/HomeController.kt
        ├── role/RoleController.kt
        ├── permission/PermissionController.kt
        ├── role_permission/RolePermissionController.kt
        ├── file/FileController.kt
        └── user/UserController.kt

src/main/resources/
├── application.yaml                  # Конфигурация приложения
├── db/migration/                     # Flyway SQL миграции
│   ├── V1__create_roles_table.sql
│   ├── V2__create_permissions_table.sql
│   ├── V3__create_role_permissions_table.sql
│   ├── V4__create_files_table.sql
│   └── V5__create_users_table.sql
└── i18n/                             # Файлы локализации (ru, kk, en)
```

---

## Ключевые паттерны

### BaseDbDatasource
Универсальный интерфейс репозитория с полным набором операций:
```
findByLongId · findOneByFilter · findAllWithFilter · all
count · countWithFilter · paginate
create · update · delete · restore
bulkCreate · bulkUpdate · bulkDelete · bulkRestore
```
`BaseDbDatasourceImpl` реализует все методы. Для join-запросов наследник переопределяет `baseJoinQuery()`.

### Soft Delete
Таблицы на базе `SoftDeleteAtTable` поддерживают мягкое удаление через `deleted_at`. `BaseDbDatasourceImpl` автоматически обрабатывает `hardDelete` и `showDeleted` флаги.

### Use Cases
Все use case наследуют `UseCaseTransaction()` и выполняются внутри `tx { }` блока (Exposed транзакция). Поддерживается `before` хук для pre-transaction логики (например, валидация).

### DTO Binding
`BaseCreateDTO<T>` автоматически маппит поля DTO в колонки таблицы через рефлексию по именам свойств. FK-поля (`Long`) автоматически оборачиваются в `EntityID`.

### Фильтрация и пагинация
`BaseFilter<T>` строит SQL-условия из параметров запроса. `BaseQueryParameter` десериализует `Parameters` Ktor в типизированный фильтр через рефлексию конструктора.

### OpenAPI
`fromFilter()`, `fromFormField()`, `okWrapped<T>()`, `okPaginationWrapped<T>()` — extension-функции для автоматической генерации OpenAPI-схем из Kotlin-типов.

---

## API Endpoints

### Роли `/api/roles`
| Метод | Путь | Описание |
|---|---|---|
| GET | `/paginate` | Пагинация ролей с правами |
| GET | `/all` | Все роли с фильтрацией |
| GET | `/get/{id}` | Роль по ID |
| POST | `/create` | Создать роль |
| PUT | `/update/{id}` | Обновить роль |
| DELETE | `/delete/{id}` | Удалить (soft/hard) |
| PATCH | `/restore/{id}` | Восстановить |
| POST | `/bulk/create` | Массовое создание |
| PUT | `/bulk/update` | Массовое обновление |
| DELETE | `/bulk/delete` | Массовое удаление |
| PUT | `/bulk/restore` | Массовое восстановление |

### Права `/api/permissions`
Аналогично ролям (без bulk restore).

### Роли-Права `/api/role-permissions`
| Метод | Путь | Описание |
|---|---|---|
| GET | `/paginate` | Пагинация |
| GET | `/all` | Все связи |
| POST | `/create` | Привязать право к роли |
| DELETE | `/delete/{id}` | Удалить связь |

### Файлы `/api/file`
| Метод | Путь | Описание |
|---|---|---|
| GET | `/paginate` | Список файлов |
| GET | `/get/{id}` | Метаданные файла |
| POST | `/create` | Загрузить один файл |
| POST | `/bulk/create` | Загрузить несколько файлов |
| PUT | `/update/{id}` | Заменить файл |
| DELETE | `/delete/{id}` | Удалить файл |
| DELETE | `/bulk/delete` | Массовое удаление |

### Пользователи `/api/users`
| Метод | Путь | Описание |
|---|---|---|
| GET | `/paginate` | Пагинация пользователей |
| GET | `/get/{id}` | Пользователь по ID (с ролью и аватаром) |
| POST | `/create` | Создать пользователя |

---

## База данных

### Схема

```
roles ──────────────────────────────────────────┐
  id, title_ru/kk/en, value*, isSystem,          │
  isAdministrative, created_at, updated_at,       │
  deleted_at                                      │
                                                  │
permissions                                       │
  id, title_ru/kk/en, value*,                    │
  created_at, updated_at                          │
        │                                         │
        └──── role_permissions ─────────────────┘
               id, role_id→roles,
               permission_id→permissions

files
  id, original_name, unique_name, full_path,
  directory, extension, mimeType,
  stored_local, file_size_byte,
  created_at, updated_at

users
  id, role_id→roles (NULL), image_id→files (NULL),
  username*, phone*, email*,
  first_name, last_name, patronymic,
  password_hash, birth_date, gender,
  is_active, is_verified,
  created_at, updated_at, deleted_at

user_verification_codes
  id, user_id→users,
  code, type, expired_at, used_at,
  is_used, is_active,
  created_at, updated_at

* — уникальный индекс
```

---

## Запуск

### Требования
- JDK 21+
- PostgreSQL 14+
- Gradle 8+

### Настройка

1. Создать базу данных:
```sql
CREATE DATABASE soccer_referee_db;
```

2. Настроить `src/main/resources/application.yaml`:
```yaml
database:
  jdbcUrl: "jdbc:postgresql://localhost:5432/soccer_referee_db"
  username: "your_user"
  password: "your_password"
```

3. Запустить приложение (миграции применяются автоматически):
```bash
./gradlew run
```

### Сборка JAR
```bash
./gradlew buildFatJar
java -jar build/libs/soccer_referee_system_back-all.jar
```

---

## Документация API

После запуска доступна по адресу:
- **Swagger UI**: `http://localhost:8000/swagger`
- **OpenAPI JSON**: `http://localhost:8000/api.json`

---

## Локализация

Поддерживаемые языки: **ru** (русский), **kk** (казахский), **en** (английский).

Язык ответа задаётся через заголовок:
```
Accept-Language: ru
```

---

## Конфигурация хранилища файлов

```yaml
storage:
  useStorage: true
  uploadDir: "uploads"           # Локальная папка хранения
  accessDir: "/uploads"          # HTTP путь доступа к файлам
  maxFileSizeMb: 10
  allowedExtensions: [jpg, jpeg, png, gif, pdf, doc, docx, xls, xlsx]
  forbiddenExtensions: [exe, bat, sh, cmd, ps1, jar]
```

При `useStorage: true` файлы доступны напрямую по HTTP: `http://localhost:8000/uploads/{filename}`.
