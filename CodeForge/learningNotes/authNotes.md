

> In this project we will be using **login through email and password**, not username and password.

---

## 🟢 Entity Layer

* **@Builder (Lombok):** Auto-generates Builder Pattern → build objects fluently, supports immutability (no setters needed).
* **@GeneratedValue:** Tells DB to auto-generate primary key values (like auto-increment).
* **@Enumerated:** Defines how enums are stored → `EnumType.STRING` (store as text), `EnumType.ORDINAL` (store as index 0,1,…).
* **@Column:** Adds DB constraints (nullable, unique, length, etc.).

---

## 🟢 Repository Layer

* **Spring Data JPA Method Naming:**

    * Methods like `findByEmail()`, `existsByEmail()` → Spring auto-generates SQL.
    * Field name in method **must exactly match entity field** (typos break startup).
* **Best use:** Simple CRUD and filtering.
* **@Query:** For complex queries (joins, aggregations, custom conditions).

---

## Optional<T>

* Represents a value that may or may not exist.
* Avoids `null` → forces explicit handling (`isPresent()`, `orElse()`, `orElseThrow()`).

---

## 🟢 DTOs (Data Transfer Objects)

* Created per API request/response, not per entity.
* Carry only required fields → prevent exposing sensitive data.

**Flow:**
Client ⇄ DTO ⇄ Service ⇄ Entity ⇄ Database

**Examples:** `RegisterRequest`, `LoginRequest`, `UserResponse`, `UpdateProfileRequest`.

---

## Layered Architecture Flow

1. **Controller** → Receives HTTP requests and returns responses.
2. **DTO** → Carries only the data required by that API.
3. **Service** → Contains business logic.
4. **Mapper** → Converts between DTO and Entity.
5. **Entity** → Represents the database table.
6. **Repository** → Communicates with the database using JPA/Hibernate.
7. **Database** → Stores the actual data.

For DTO should we mention `@Entity`?

**No. Never.**

* DTOs are not database tables.
* So till now figured out:

    * **Entity** → What all we will be saving.
    * **Repository layer** → For custom searching and validation.
    * **DTO** → What all the requests may come.
    * Next → **Mapper**, mapping those DTOs to Entity.

---

## 🟢 Mapper

* Mapper converts DTO to Entity.
* It has functions to convert all DTOs to Entity (like `LoginRequest` DTO → Entity function, `RegisterRequest` DTO → Entity function, etc.).

Example DTO:

```java
public class RegisterRequest {
    private String email;
    private String password;
}
```

No annotation.

Why?

Because we never ask Spring to manage it.

Example:

```java
@PostMapping("/register")
public void register(@RequestBody RegisterRequest request) {

}
```

Spring creates the DTO from the JSON request.

So no `@Component` needed.

---

## 🟢 AuthService

* There are **3 types of Dependency Injection**:

    * Constructor Injection ✅ (Preferred)
    * Field Injection
    * Setter Injection

* Constructor Injection is preferred as it makes the dependencies `final`.

* `@Autowired` is used for Field Injection or for Constructor and Setter Injection when there is more than one constructor or setter.

* If there is only one constructor or setter, Spring automatically performs the injection, so `@Autowired` is not required.

* We use Lombok's `@RequiredArgsConstructor`.

* `@RequiredArgsConstructor` automatically generates a constructor for all fields marked as `final` or annotated with `@NonNull`.

* Spring automatically performs Constructor Injection using this generated constructor.

---

Client

↓

RegisterRequest

↓

AuthService.register()

↓

1. Check email exists?
2. Convert DTO → Entity
3. Encrypt password
4. Set role
5. Set timestamps
6. Save user
7. Generate AuthResponse

---

* Using the Builder Pattern in the mapper, we create an object with only the information available from the DTO.

* The remaining fields (encrypted password, role, timestamps, etc.) are set later inside the Service before saving it.

* This keeps responsibilities clear:

    * **Mapper** → Converts DTO to Entity.
    * **Service** → Applies business logic and completes the Entity.
    * **Repository** → Saves the fully prepared Entity to the database.
