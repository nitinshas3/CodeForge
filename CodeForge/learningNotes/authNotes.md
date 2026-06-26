in this we will be using login through email and password , not username and password

## 🟢 Entity Layer
- **@Builder (Lombok):** Auto‑generates Builder Pattern → build objects fluently, supports immutability (no setters needed).
- **@GeneratedValue:** Tells DB to auto‑generate primary key values (like auto‑increment).
- **@Enumerated:** Defines how enums are stored → `EnumType.STRING` (store as text), `EnumType.ORDINAL` (store as index 0,1,…).
- **@Column:** Adds DB constraints (nullable, unique, length, etc.).

---

## 🟢 Repository Layer
- **Spring Data JPA Method Naming:**
    - Methods like `findByEmail()`, `existsByUsername()` → Spring auto‑generates SQL.
    - Field name in method **must exactly match entity field** (typos break startup).
- **Best use:** Simple CRUD and filtering.
- **@Query:** For complex queries (joins, aggregations, custom conditions).

---

## Optional<T>
- Represents a value that may or may not exist.
- Avoids `null` → forces explicit handling (`isPresent()`, `orElse()`, `orElseThrow()`).

---

## 🟢 DTOs (Data Transfer Objects)
- Created per API request/response, not per entity.
- Carry only required fields → prevent exposing sensitive data.
- **Flow:**  
  Client ⇄ DTO ⇄ Service ⇄ Entity ⇄ Database
- **Examples:** `RegisterRequest`, `LoginRequest`, `UserResponse`, `UpdateProfileRequest`.

---

##  Layered Architecture Flow
1. **Controller** → Receives HTTP requests and returns responses.
2. **DTO** → Carries only the data required by that API.
3. **Service** → Contains business logic.
4. **Mapper** → Converts between DTO and Entity.
5. **Entity** → Represents the database table.
6. **Repository** → Communicates with the database using JPA/Hibernate.
7. **Database** → Stores the actual data.  

For DTO should we mention `@Entity`?  
**No. Never.**

- DTOs are not database tables.
- So till now figured out:
  - **Entity** → what all we will be saving.
  - **Repository layer** → for custom searching and validation.
  - **DTO** → what all the requests may come.
  - Next → **Mapper**, mapping those DTOs to Entity.

---

## 🟢 Mapper
- Mapper converts DTO to Entity.
- It has functions to convert all DTOs to Entity (like `LoginRequest` DTO → Entity function, `RegisterRequest` DTO → Entity function, etc.).

Example DTO:

public class RegisterRequest {
    private String email;
    private String password;
public class RegisterRequest {
}

No annotation.
Why? 
Because we never ask Spring to manage it.
It is created automatically when a request comes.
Example:
@PostMapping("/register")
public void register(@RequestBody RegisterRequest request) {

} 
Spring creates the DTO from the JSON request.
So no @Component needed.
