package fr.soutien.backend.controller;

import fr.soutien.backend.dto.*;
import fr.soutien.backend.service.MouvementService;
import fr.soutien.backend.service.SoldeService;
import fr.soutien.backend.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Espace gestionnaire (partie A : comptes et paiements)
// TEMPORAIRE : ouvert à tous. À l'étape JWT, réservé au rôle GESTIONNAIRE.
@RestController
@RequestMapping("/api/gestion")
@RequiredArgsConstructor
public class GestionController {

    private final UtilisateurService utilisateurService;
    private final MouvementService mouvementService;
    private final SoldeService soldeService;

    // ---------- Comptes utilisateurs ----------

    @GetMapping("/utilisateurs")
    public List<UtilisateurResponse> utilisateurs() {
        return utilisateurService.lister();
    }

    @GetMapping("/utilisateurs/{id}")
    public UtilisateurResponse utilisateur(@PathVariable Long id) {
        return utilisateurService.trouver(id);
    }

    @PostMapping("/utilisateurs")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse creer(@Valid @RequestBody UtilisateurRequest req) {
        return utilisateurService.creer(req);
    }

    @PutMapping("/utilisateurs/{id}")
    public UtilisateurResponse modifier(@PathVariable Long id, @Valid @RequestBody UtilisateurRequest req) {
        return utilisateurService.modifier(id, req);
    }

    @DeleteMapping("/utilisateurs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
    }

    // ---------- Paiements (mouvements) ----------

    // Solde + liste des mouvements d'un utilisateur
    @GetMapping("/utilisateurs/{id}/mouvements")
    public SoldeResponse mouvements(@PathVariable Long id) {
        utilisateurService.trouver(id); // 404 si l'utilisateur n'existe pas
        return soldeService.solde(id);
    }

    @PostMapping("/utilisateurs/{id}/mouvements")
    @ResponseStatus(HttpStatus.CREATED)
    public MouvementResponse ajouterMouvement(@PathVariable Long id, @Valid @RequestBody MouvementRequest req) {
        return mouvementService.ajouter(id, req);
    }
}
