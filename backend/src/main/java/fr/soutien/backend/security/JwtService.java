package fr.soutien.backend.security;

import fr.soutien.backend.entity.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

// Fabrique les tokens JWT
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    public String genererToken(Utilisateur utilisateur) {
        Instant maintenant = Instant.now();

        // Le contenu du token
        JwtClaimsSet contenu = JwtClaimsSet.builder()
                .issuer("cours-soutien")                                       // qui l'a émis
                .issuedAt(maintenant)                                          // quand
                .expiresAt(maintenant.plus(expirationMinutes, ChronoUnit.MINUTES)) // jusqu'à quand
                .subject(utilisateur.getId().toString())                       // QUI : l'id de l'utilisateur
                .claim("email", utilisateur.getEmail())
                .claim("role", utilisateur.getRole().name())                   // PARENT ou GESTIONNAIRE
                .build();

        // Signature avec l'algorithme HS256 et la clé secrète
        JwsHeader entete = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(entete, contenu)).getTokenValue();
    }
}
