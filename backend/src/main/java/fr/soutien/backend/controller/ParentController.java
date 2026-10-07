package fr.soutien.backend.controller;

import fr.soutien.backend.dto.*;
import fr.soutien.backend.service.CoursService;
import fr.soutien.backend.service.EnfantService;
import fr.soutien.backend.service.NiveauService;
import fr.soutien.backend.service.SoldeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Espace parent : réservé au rôle PARENT (voir SecurityConfig)
@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class ParentController {

    private final EnfantService enfantService;
    private final CoursService coursService;
    private final SoldeService soldeService;
    private final NiveauService niveauService;

    // L'id du parent connecté est lu dans le token ("sub"), jamais envoyé par le front
    private Long parentId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    @GetMapping("/niveaux")
    public List<NiveauResponse> niveaux() {
        return niveauService.lister();
    }

    @GetMapping("/enfants")
    public List<EnfantResponse> mesEnfants(@AuthenticationPrincipal Jwt jwt) {
        return enfantService.lister(parentId(jwt));
    }

    @PostMapping("/enfants")
    @ResponseStatus(HttpStatus.CREATED)
    public EnfantResponse ajouterEnfant(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody EnfantRequest req) {
        return enfantService.creer(parentId(jwt), req);
    }

    @PutMapping("/enfants/{id}")
    public EnfantResponse modifierEnfant(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable Long id,
                                         @Valid @RequestBody EnfantRequest req) {
        return enfantService.modifier(parentId(jwt), id, req);
    }

    @DeleteMapping("/enfants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerEnfant(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        enfantService.supprimer(parentId(jwt), id);
    }

    @GetMapping("/cours-disponibles")
    public List<CoursDisponibleResponse> coursDisponibles(@RequestParam Long niveauId) {
        return coursService.coursDisponibles(niveauId);
    }

    @PutMapping("/enfants/{id}/cours")
    public EnfantResponse inscrire(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long id,
                                   @Valid @RequestBody InscriptionRequest req) {
        return enfantService.inscrire(parentId(jwt), id, req.coursId());
    }

    @DeleteMapping("/enfants/{id}/cours/{coursId}")
    public EnfantResponse desinscrire(@AuthenticationPrincipal Jwt jwt,
                                      @PathVariable Long id,
                                      @PathVariable Long coursId) {
        return enfantService.desinscrire(parentId(jwt), id, coursId);
    }

    @GetMapping("/solde")
    public SoldeResponse solde(@AuthenticationPrincipal Jwt jwt) {
        return soldeService.solde(parentId(jwt));
    }
}
