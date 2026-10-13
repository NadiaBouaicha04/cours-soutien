package fr.soutien.backend.service;

import fr.soutien.backend.dto.LoginRequest;
import fr.soutien.backend.dto.LoginResponse;
import fr.soutien.backend.dto.UtilisateurResponse;
import fr.soutien.backend.entity.Utilisateur;
import fr.soutien.backend.exception.IdentifiantsInvalidesException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.UtilisateurRepository;
import fr.soutien.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();

        // 1. Retrouver l'utilisateur par son e-mail
        Utilisateur u = utilisateurRepository.findByEmail(email)
                .orElseThrow(IdentifiantsInvalidesException::new);

        // 2. Comparer le mot de passe saisi avec le hash stocké
        if (!passwordEncoder.matches(req.motDePasse(), u.getMotDePasse())) {
            throw new IdentifiantsInvalidesException();
        }

        // 3. Fabriquer le token
        return new LoginResponse(jwtService.genererToken(u), u.getRole(), u.getPrenom(), u.getNom());
    }

    // Utilisateur connecté (à partir de l'id contenu dans le token)
    public UtilisateurResponse moi(Long utilisateurId) {
        return utilisateurRepository.findById(utilisateurId)
                .map(UtilisateurResponse::from)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable"));
    }
}
