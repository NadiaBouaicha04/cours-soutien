package fr.soutien.backend.controller;

import fr.soutien.backend.dto.*;
import fr.soutien.backend.service.CoursService;
import fr.soutien.backend.service.EnfantService;
import fr.soutien.backend.service.NiveauService;
import fr.soutien.backend.service.SoldeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class ParentController {

    // TEMPORAIRE : l'id du parent connecté est lu dans un en-tête HTTP.
    // À l'étape JWT, il sera extrait du token de connexion.
    private static final String EN_TETE_PARENT = "X-Parent-Id";

    private final EnfantService enfantService;
    private final CoursService coursService;
    private final SoldeService soldeService;
    private final NiveauService niveauService;

    // GET /api/parent/niveaux
    @GetMapping("/niveaux")
    public List<NiveauResponse> niveaux() {
        return niveauService.lister();
    }

    // GET /api/parent/enfants
    @GetMapping("/enfants")
    public List<EnfantResponse> mesEnfants(@RequestHeader(EN_TETE_PARENT) Long parentId) {
        return enfantService.lister(parentId);
    }

    // POST /api/parent/enfants
    @PostMapping("/enfants")
    @ResponseStatus(HttpStatus.CREATED)
    public EnfantResponse ajouterEnfant(@RequestHeader(EN_TETE_PARENT) Long parentId,
                                        @Valid @RequestBody EnfantRequest req) {
        return enfantService.creer(parentId, req);
    }

    // PUT /api/parent/enfants/{id}
    @PutMapping("/enfants/{id}")
    public EnfantResponse modifierEnfant(@RequestHeader(EN_TETE_PARENT) Long parentId,
                                         @PathVariable Long id,
                                         @Valid @RequestBody EnfantRequest req) {
        return enfantService.modifier(parentId, id, req);
    }

    // DELETE /api/parent/enfants/{id}
    @DeleteMapping("/enfants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerEnfant(@RequestHeader(EN_TETE_PARENT) Long parentId,
                                @PathVariable Long id) {
        enfantService.supprimer(parentId, id);
    }

    // GET /api/parent/cours-disponibles?niveauId=4
    @GetMapping("/cours-disponibles")
    public List<CoursDisponibleResponse> coursDisponibles(@RequestParam Long niveauId) {
        return coursService.coursDisponibles(niveauId);
    }

    // PUT /api/parent/enfants/{id}/cours
    @PutMapping("/enfants/{id}/cours")
    public EnfantResponse inscrire(@RequestHeader(EN_TETE_PARENT) Long parentId,
                                   @PathVariable Long id,
                                   @Valid @RequestBody InscriptionRequest req) {
        return enfantService.inscrire(parentId, id, req.coursId());
    }

    // DELETE /api/parent/enfants/{id}/cours/{coursId}
    @DeleteMapping("/enfants/{id}/cours/{coursId}")
    public EnfantResponse desinscrire(@RequestHeader(EN_TETE_PARENT) Long parentId,
                                      @PathVariable Long id,
                                      @PathVariable Long coursId) {
        return enfantService.desinscrire(parentId, id, coursId);
    }

    // GET /api/parent/solde
    @GetMapping("/solde")
    public SoldeResponse solde(@RequestHeader(EN_TETE_PARENT) Long parentId) {
        return soldeService.solde(parentId);
    }
}
