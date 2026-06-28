⚡ End‑to‑End Workflow
Login request → hits filter (UsernamePasswordAuthenticationFilter).

Filter builds an Authentication object (with username + raw password).

Passes it to AuthenticationManager.

Manager loops through its list of AuthenticationProviders.

A provider (like DaoAuthenticationProvider) picks it up:

Calls UserDetailsService.loadUserByUsername.

Gets UserDetails (username, hashed password, roles).

Uses PasswordEncoder.matches(raw, hashed) to verify.

If OK → returns a fully authenticated Authentication object.

Manager gives this back to the filter.

Filter puts it into SecurityContextHolder → user is now authenticated.

If you’re using JWT → you generate a token here and send it to the client.

Subsequent requests → JWT filter validates token, rebuilds Authentication, puts it back into SecurityContext.