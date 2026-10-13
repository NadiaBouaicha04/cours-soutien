package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Jour;

import java.time.LocalTime;

// Un cours proposé au parent, avec le nombre de places restantes
public record CoursDisponibleResponse(
        Long id,
        Jour jour,
        LocalTime heureDebut,
        LocalTime heureFin,
        String salle,
        long placesRestantes
) {

    public static CoursDisponibleResponse from(Cours c, long placesRestantes) {
        return new CoursDisponibleResponse(
                c.getId(),
                c.getJour(),
                c.getHeureDebut(),
                c.getHeureFin(),
                c.getSalle().getNom(),
                placesRestantes
        );
    }
}
