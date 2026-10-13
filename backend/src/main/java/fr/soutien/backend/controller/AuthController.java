package fr.soutien.backend.controller;

import fr.soutien.backend.dto.LoginRequest;
import fr.soutien.backend.dto.LoginResponse;
import fr.soutien.backend.dto.UtilisateurResponse;
import fr.soutien.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login : accessible à tous
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    // GET /api/auth/me : l'utilisateur connecté
    // @AuthenticationPrincipal donne accès au token déjà vérifié par Spring Security
    @GetMapping("/me")
    public UtilisateurResponse moi(@AuthenticationPrincipal Jwt jwt) {
        return authService.moi(Long.valueOf(jwt.getSubject()));
    }
}
