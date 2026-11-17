package com.dsatracker.controller;

import com.dsatracker.model.User;
import com.dsatracker.repository.UserRepository;
import com.dsatracker.security.CurrentUser;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public record RegisterRequest(@NotBlank @Size(max = 80) String name,
                                  @NotBlank @Email @Size(max = 150) String email,
                                  @NotBlank @Size(min = 6, max = 100) String password) {}
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final CurrentUser me;

    public AuthController(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, CurrentUser me) {
        this.users = users; this.encoder = encoder; this.authManager = authManager; this.me = me;
    }

    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        User u = new User();
        u.setName(r.name().trim()); u.setEmail(email); u.setPasswordHash(encoder.encode(r.password()));
        users.save(u);
        return Map.of("message", "Account created");
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest r, HttpServletRequest req, HttpServletResponse res) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(r.email().trim().toLowerCase(), r.password()));
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
        new HttpSessionSecurityContextRepository().saveContext(ctx, req, res);   // stores login in the session
        return Map.of("name", me.get().getName());
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        SecurityContextHolder.clearContext();
        return Map.of("message", "Logged out");
    }

    @GetMapping("/me")
    public Map<String, String> me() { User u = me.get(); return Map.of("name", u.getName(), "email", u.getEmail()); }
}