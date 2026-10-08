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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SecurityConfig {

    // Clé secrète qui signe les tokens (lue dans application.properties)
    @Value("${app.jwt.secret}")
    private String secret;

    // Adresse du front-end autorisée à appeler l'API (CORS)
    @Value("${app.cors.origine:http://localhost:3000}")
    private String origineFront;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Autoriser les appels venant du front-end (voir corsConfigurationSource)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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

    // CORS : le navigateur n'autorise une page de http://localhost:3000 à appeler
    // l'API de http://localhost:8080 que si l'API le permet explicitement
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origineFront));                         // qui
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // quels verbes
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));        // quels en-têtes

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // Hachage des mots de passe (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
