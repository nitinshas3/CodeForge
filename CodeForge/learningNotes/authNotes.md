

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

# 🟢 config class

* `PasswordEncoder` is an interface used to hash and verify passwords.

* We return `new BCryptPasswordEncoder()` because it's a secure implementation of that interface.

* Declaring the return type as `PasswordEncoder` keeps the code flexible, while `BCryptPasswordEncoder` provides the actual hashing logic. We have other implementations as well.

* A `@Bean` method is like a factory: it tells Spring how to create and register an object in the IoC container.

* Usually, the return type is an interface (e.g., `PasswordEncoder`) so your code stays flexible.

* Inside the method, you return a specific implementation (e.g., `new BCryptPasswordEncoder()`), which Spring will use whenever that interface is injected.

---

# 🟢 jwt

We are entering JWT.

* So there are 2 things:

  * **JwtService** → For generating tokens, validating tokens, checking expiry, etc.
  * **JwtFilter** → Takes the incoming request and calls `JwtService` to validate it, like a security guard, and decides whether to allow the request or not.

* `JwtFilter` just extends the abstract class `OncePerRequestFilter` and implements its own logic. It simply sends the token to `JwtService`; `JwtService` does all the work.

* JWT is common, not only for authentication. That's why it is placed in the `common` package and not the `auth` package.

* So up till now, what all we did:

  * First, Entity.
  * Then Repository setup with some custom searching and validation.
  * Then DTOs, like what all requests we can get (`RegisterRequest`, `AuthResponse`, etc.).
  * Then Mapper. Mapper only converts DTO to User, like it builds half the object using the Builder Pattern.
  * Later, that object is sent to the Service, which adds the remaining fields like encoded password, role, timestamps, etc.
  * Then that object is stored in the Repository.
  * Then come the Register and Login methods in the Service.
  * They take DTO requests and return `AuthResponse`, which contains the JWT token.
  * So now, coming to JWT, there are mainly 2 classes: `JwtFilter` and `JwtService`, with `JwtService` doing most of the work.

---

### Login Flow

```
Email + Password

↓

Verified

↓

JwtService.generateToken()

↓

Header

Payload

Signature

↓

JWT

↓

Client
```

---

### Next Request Flow

```
Client

↓

JWT

↓

JwtFilter

↓

JwtService.extractEmail()

↓

JwtService.isTokenValid()

↓

Controller
```

---

### Biggest Realization

Now notice.

`JwtService` doesn't know HTTP exists.

It only knows:

```
Token

↓

Generate

Extract

Validate
```

Nothing else.

`JwtFilter` doesn't know cryptography exists.

It only knows:

```
Receive Request

↓

Extract Token

↓

Ask JwtService

↓

Continue Request
```

This separation is why Spring Security is so clean.

---

### ⚡ Summary Workflow

* Secret (Base64 string) → `"c2VjcmV0"`.

* So, in `application.properties`, the secret has to be Base64 encoded.

* Decode → Raw bytes `[115, 101, 99, 114, 101, 116]`.

* `hmacShaKeyFor()` → Wraps the bytes into a `SecretKey` object for HMAC.

* `signWith()` → Uses HS256 to generate the signature from the Header + Payload + SecretKey (default is HS256).

* JWT → Header (`alg = HS256`), Payload (claims), Signature (hash result).

So:

* `.decode()` = Unwrap Base64 text → Raw bytes.

* `hmacShaKeyFor()` = Convert raw bytes → Usable HMAC key object.

* `.signWith()` = Actually generates the signature using that key.

# 🟢 userdetailservice

* First login → Spring Security uses `UserDetailsService` to check the database for the user's info (email, password hash, roles). That's the one time the DB is hit.

* Subsequent requests → The client just sends the JWT in the header. Your JWT filter parses the token, verifies the signature, and checks the expiry/subject. No DB lookup is needed because all the required information is already inside the token.

* 👉 So the DB is only involved at login, and after that the JWT itself is enough to validate requests.

---

```text
Client
│
├── Email
└── Password
│
▼
AuthController
│
▼
AuthService.login()
│
▼
authenticationManager.authenticate(
    email,
    password
)
│
▼
CustomUserDetailsService.loadUserByUsername(email)
│
▼
UserRepository.findByEmail(email)
│
▼
User (implements UserDetails)
│
▼
Spring PasswordEncoder.matches()
│
├── Wrong Password ❌ Exception
│
└── Correct Password ✅
│
▼
Back to AuthService
│
▼
UserRepository.findByEmail(email)
│
▼
JwtService.generateToken(user)
│
▼
Return JWT
```

---

* `UserDetailsService` → Fetches the user from the database.

* `PasswordEncoder` → Checks the password.

* If the password is correct → Spring wraps the `UserDetails` into an `Authentication` object.

* That `Authentication` object lives in the `SecurityContext` → Used for authorization checks (`hasRole`, `@PreAuthorize`, etc.).

* With JWT → After login, you don't call the database again; you just rebuild the `Authentication` from the token. this thing check once ok
