package fr.soutien.backend.controller;

import fr.soutien.backend.dto.*;
import fr.soutien.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Espace gestionnaire
// TEMPORAIRE : ouvert à tous. À l'étape JWT, réservé au rôle GESTIONNAIRE.
@RestController
@RequestMapping("/api/gestion")
@RequiredArgsConstructor
public class GestionController {

    private final UtilisateurService utilisateurService;
    private final MouvementService mouvementService;
    private final SoldeService soldeService;
    private final SalleService salleService;
    private final NiveauService niveauService;
    private final CoursService coursService;

    // ================= Comptes utilisateurs =================

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
    public UtilisateurResponse creerUtilisateur(@Valid @RequestBody UtilisateurRequest req) {
        return utilisateurService.creer(req);
    }

    @PutMapping("/utilisateurs/{id}")
    public UtilisateurResponse modifierUtilisateur(@PathVariable Long id, @Valid @RequestBody UtilisateurRequest req) {
        return utilisateurService.modifier(id, req);
    }

    @DeleteMapping("/utilisateurs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerUtilisateur(@PathVariable Long id) {
        utilisateurService.supprimer(id);
    }

    // ================= Paiements (mouvements) =================

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

    // ================= Salles =================

    @GetMapping("/salles")
    public List<SalleResponse> salles() {
        return salleService.lister();
    }

    @PostMapping("/salles")
    @ResponseStatus(HttpStatus.CREATED)
    public SalleResponse creerSalle(@Valid @RequestBody SalleRequest req) {
        return salleService.creer(req);
    }

    @PutMapping("/salles/{id}")
    public SalleResponse modifierSalle(@PathVariable Long id, @Valid @RequestBody SalleRequest req) {
        return salleService.modifier(id, req);
    }

    @DeleteMapping("/salles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerSalle(@PathVariable Long id) {
        salleService.supprimer(id);
    }

    // ================= Niveaux =================

    @GetMapping("/niveaux")
    public List<NiveauResponse> niveaux() {
        return niveauService.lister();
    }

    @PostMapping("/niveaux")
    @ResponseStatus(HttpStatus.CREATED)
    public NiveauResponse creerNiveau(@Valid @RequestBody NiveauRequest req) {
        return niveauService.creer(req);
    }

    @PutMapping("/niveaux/{id}")
    public NiveauResponse modifierNiveau(@PathVariable Long id, @Valid @RequestBody NiveauRequest req) {
        return niveauService.modifier(id, req);
    }

    @DeleteMapping("/niveaux/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerNiveau(@PathVariable Long id) {
        niveauService.supprimer(id);
    }

    // ================= Cours =================

    @GetMapping("/cours")
    public List<CoursResponse> cours() {
        return coursService.lister();
    }

    @PostMapping("/cours")
    @ResponseStatus(HttpStatus.CREATED)
    public CoursResponse creerCours(@Valid @RequestBody CoursRequest req) {
        return coursService.creer(req);
    }

    @PutMapping("/cours/{id}")
    public CoursResponse modifierCours(@PathVariable Long id, @Valid @RequestBody CoursRequest req) {
        return coursService.modifier(id, req);
    }

    @DeleteMapping("/cours/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerCours(@PathVariable Long id) {
        coursService.supprimer(id);
    }

    // Récapitulatif des cours avec la liste des inscrits
    @GetMapping("/cours/recap")
    public List<CoursRecapResponse> recap() {
        return coursService.recap();
    }
}
