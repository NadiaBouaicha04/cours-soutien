package fr.soutien.backend.service;

import fr.soutien.backend.dto.CoursDisponibleResponse;
import fr.soutien.backend.dto.CoursRecapResponse;
import fr.soutien.backend.dto.CoursRequest;
import fr.soutien.backend.dto.CoursResponse;
import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Niveau;
import fr.soutien.backend.entity.Salle;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.NiveauRepository;
import fr.soutien.backend.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoursService {

    // Tri des cours : niveau (6e -> Terminale), puis jour, puis heure
    private static final Comparator<Cours> ORDRE_COURS =
            Comparator.comparing((Cours c) -> c.getNiveau().getOrdre())
                    .thenComparing(Cours::getJour)
                    .thenComparing(Cours::getHeureDebut);

    private final CoursRepository coursRepository;
    private final EnfantRepository enfantRepository;
    private final NiveauRepository niveauRepository;
    private final SalleRepository salleRepository;

    // ================= Espace parent =================

    // Règle : un cours complet ne doit pas être proposé à l'inscription
    @Transactional(readOnly = true)
    public List<CoursDisponibleResponse> coursDisponibles(Long niveauId) {
        return coursRepository.findByNiveauId(niveauId).stream()
                .map(c -> CoursDisponibleResponse.from(
                        c,
                        c.getSalle().getCapacite() - enfantRepository.compterInscrits(c.getId())))
                .filter(c -> c.placesRestantes() > 0)
                .sorted(Comparator.comparing(CoursDisponibleResponse::jour)
                        .thenComparing(CoursDisponibleResponse::heureDebut))
                .toList();
    }

    // ================= Espace gestionnaire =================

    @Transactional(readOnly = true)
    public List<CoursResponse> lister() {
        return coursRepository.findAll().stream()
                .sorted(ORDRE_COURS)
                .map(c -> CoursResponse.from(c, enfantRepository.compterInscrits(c.getId())))
                .toList();
    }

    public CoursResponse creer(CoursRequest req) {
        Cours cours = new Cours();
        appliquer(cours, req);
        verifierChevauchement(cours);
        // même salle + même jour + même heure de début -> contrainte unique -> 409
        return CoursResponse.from(coursRepository.save(cours), 0);
    }

    public CoursResponse modifier(Long id, CoursRequest req) {
        Cours cours = chercher(id);
        long inscrits = enfantRepository.compterInscrits(id);

        // Règle : on ne change pas le niveau d'un cours qui a des inscrits
        // (ils ne correspondraient plus au niveau du cours)
        if (inscrits > 0 && !cours.getNiveau().getId().equals(req.niveauId())) {
            throw new RegleMetierException("Impossible de changer le niveau d'un cours qui a des inscrits");
        }

        appliquer(cours, req);

        // Règle : la nouvelle salle doit pouvoir accueillir les inscrits actuels
        if (cours.getSalle().getCapacite() < inscrits) {
            throw new RegleMetierException("La salle choisie est trop petite pour les " + inscrits + " inscrits");
        }

        verifierChevauchement(cours);
        return CoursResponse.from(cours, inscrits);
    }

    public void supprimer(Long id) {
        Cours cours = chercher(id);

        // Règle : on ne supprime pas un cours qui a des inscrits
        // (il faut d'abord les désinscrire)
        if (enfantRepository.compterInscrits(id) > 0) {
            throw new RegleMetierException("Impossible de supprimer un cours qui a des inscrits");
        }
        coursRepository.delete(cours);
    }

    // Récapitulatif : chaque cours avec la liste de ses inscrits
    @Transactional(readOnly = true)
    public List<CoursRecapResponse> recap() {
        return coursRepository.findAll().stream()
                .sorted(ORDRE_COURS)
                .map(c -> CoursRecapResponse.from(c, enfantRepository.findInscrits(c.getId())))
                .toList();
    }

    // ================= Outils =================

    private Cours chercher(Long id) {
        return coursRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Cours introuvable"));
    }

    private void appliquer(Cours cours, CoursRequest req) {
        // Règle : l'heure de fin doit être après l'heure de début
        if (!req.heureFin().isAfter(req.heureDebut())) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }

        Niveau niveau = niveauRepository.findById(req.niveauId())
                .orElseThrow(() -> new RessourceIntrouvableException("Niveau introuvable"));
        Salle salle = salleRepository.findById(req.salleId())
                .orElseThrow(() -> new RessourceIntrouvableException("Salle introuvable"));

        cours.setJour(req.jour());
        cours.setHeureDebut(req.heureDebut());
        cours.setHeureFin(req.heureFin());
        cours.setNiveau(niveau);
        cours.setSalle(salle);
    }

    // Règle : deux cours ne peuvent pas occuper la même salle au même moment.
    // La contrainte unique ne bloque que la même heure de DÉBUT ;
    // ici on bloque aussi les chevauchements (10h-12h et 11h-13h).
    private void verifierChevauchement(Cours cours) {
        for (Cours autre : coursRepository.findBySalleIdAndJour(cours.getSalle().getId(), cours.getJour())) {
            if (autre.getId().equals(cours.getId())) {
                continue; // on ne se compare pas à soi-même (cas de la modification)
            }
            boolean chevauche = cours.getHeureDebut().isBefore(autre.getHeureFin())
                    && autre.getHeureDebut().isBefore(cours.getHeureFin());
            if (chevauche) {
                throw new RegleMetierException("La salle est déjà occupée sur ce créneau");
            }
        }
    }
}
