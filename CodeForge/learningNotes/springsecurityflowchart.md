# ⚡ End-to-End Authentication Workflow

```text
Login Request
   │
   ▼
UsernamePasswordAuthenticationFilter
(Builds Authentication object with email + raw password)
   │
   ▼
AuthenticationManager
(Starts authentication process)
   │
   ▼
AuthenticationProvider
(Checks if it can authenticate the request)
   │
   ▼
CustomUserDetailsService.loadUserByUsername(email)
   │
   ▼
UserDetails
(User + hashed password + roles)
   │
   ▼
PasswordEncoder.matches(rawPassword, hashedPassword)
   │
   ▼
Authentication Successful
(Returns authenticated Authentication object)
   │
   ▼
SecurityContextHolder
(Stores authenticated user)
   │
   ▼
JwtService.generateToken(user)
   │
   ▼
JWT Sent to Client
   │
   ▼
──────────────────────────────────────────────────────
Subsequent Requests
   │
   ▼
JwtAuthenticationFilter
   │
   ├── Extract JWT
   ├── Validate Token
   ├── Load UserDetails
   └── Store Authentication in SecurityContextHolder
   │
   ▼
Controller
```

# 🔐 Spring Security + JWT Authentication Flow

```text
SecurityConfig
(Main security configuration / wires everything together)

                               
        ┌───────────────────────┬───────────┴────────────┬─────────────────────┐
        │                       │                        │
        ▼                       ▼                        ▼
PasswordEncoder         AuthenticationManager      SecurityFilterChain
(BCrypt hashing)      (Starts authentication)   (Security rules & filters)
                                │
                                ▼
                    AuthenticationProvider
        (Uses UserDetailsService + PasswordEncoder)
                                │
                                ▼
                  CustomUserDetailsService
        (Implements UserDetailsService)
        (Overrides loadUserByUsername())
        (Loads user from DB using email)
                                │
                                ▼
                       UserRepository
        (Extends JpaRepository)
        (Communicates with Database)
                                │
                                ▼
                  User (implements UserDetails)
        (Database Entity)
        (Spring Security's authenticated user object)
```

---

# 🛡️ JWT Flow

```text
JwtAuthenticationFilter
(Runs before every protected request)
(Extracts JWT from Authorization Header)
(Calls JwtService for validation)
(Stores authenticated user in SecurityContext)
                                │
                                ▼
                           JwtService
(Generate JWT)
(Extract Email)
(Extract Claims)
(Validate JWT)
(Check Expiration)
```

# 🔑 Login Authentication Flow

```text
Client
   │
   ▼
AuthController
   │
   ▼
AuthService
   │
   ▼
AuthenticationManager.authenticate(email, password)
   │
   ▼
AuthenticationProvider
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
PasswordEncoder.matches(rawPassword, hashedPassword)
   │
   ▼
Authentication Successful
   │
   ▼
JwtService.generateToken(user)
   │
   ▼
JWT Returned to Client
```

# 🛡️ JWT Request Authentication Flow

```text
Client
   │
   ▼
GET /problems
   │
   ▼
JwtAuthenticationFilter
   │
   ├── Read Authorization Header
   ├── Extract JWT
   ├── JwtService.extractEmail()
   ├── CustomUserDetailsService.loadUserByUsername()
   ├── JwtService.isTokenValid()
   └── Store User in SecurityContextHolder
   │
   ▼
Controller
```