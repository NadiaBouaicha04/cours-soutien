package fr.soutien.backend.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    // Clé secrète qui signe les tokens (lue dans application.properties)
    @Value("${app.jwt.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API sans cookie de session : la protection CSRF est inutile
            .csrf(csrf -> csrf.disable())
            // Aucune session côté serveur : chaque requête porte son token
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Qui a le droit d'accéder à quoi
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/parent/**").hasRole("PARENT")
                .requestMatchers("/api/gestion/**").hasRole("GESTIONNAIRE")
                .anyRequest().authenticated()
            )
            // Vérifier le token JWT présent dans l'en-tête Authorization
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(roleDepuisLeToken()))
            );
        return http.build();
    }

    // Transforme le champ "role" du token (ex. PARENT) en droit Spring "ROLE_PARENT",
    // utilisé par hasRole("PARENT") ci-dessus
    private JwtAuthenticationConverter roleDepuisLeToken() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("role");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    private SecretKey cleSecrete() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    // Fabrique les tokens (utilisé par JwtService à la connexion)
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(cleSecrete()));
    }

    // Vérifie les tokens reçus (signature + expiration)
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(cleSecrete())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Hachage des mots de passe (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
