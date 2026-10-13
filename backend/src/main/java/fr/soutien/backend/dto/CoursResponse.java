package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Jour;

import java.time.LocalTime;

// Un cours vu par le gestionnaire, avec son taux de remplissage
public record CoursResponse(
        Long id,
        Jour jour,
        LocalTime heureDebut,
        LocalTime heureFin,
        Long niveauId,
        String niveau,
        Long salleId,
        String salle,
        Integer capacite,
        long nombreInscrits
) {

    public static CoursResponse from(Cours c, long nombreInscrits) {
        return new CoursResponse(
                c.getId(),
                c.getJour(),
                c.getHeureDebut(),
                c.getHeureFin(),
                c.getNiveau().getId(),
                c.getNiveau().getLibelle(),
                c.getSalle().getId(),
                c.getSalle().getNom(),
                c.getSalle().getCapacite(),
                nombreInscrits
        );
    }
}
