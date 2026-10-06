package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Enfant;
import fr.soutien.backend.entity.Jour;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

// Données renvoyées au front pour un enfant
public record EnfantResponse(
        Long id,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String etablissement,
        Long niveauId,
        String niveau,
        List<CoursResume> cours
) {

    // Résumé d'un cours suivi
    public record CoursResume(Long id, Jour jour, LocalTime heureDebut, LocalTime heureFin, String salle) {
    }

    // Conversion entité -> DTO
    public static EnfantResponse from(Enfant e) {
        List<CoursResume> cours = e.getCours().stream()
                .map(c -> new CoursResume(c.getId(), c.getJour(), c.getHeureDebut(), c.getHeureFin(), c.getSalle().getNom()))
                .sorted(Comparator.comparing(CoursResume::jour).thenComparing(CoursResume::heureDebut))
                .toList();

        return new EnfantResponse(
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getDateNaissance(),
                e.getEtablissement(),
                e.getNiveau().getId(),
                e.getNiveau().getLibelle(),
                cours
        );
    }
}
