package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Jour;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CoursRequest(
        @NotNull(message = "Le jour est obligatoire")
        Jour jour,

        @NotNull(message = "L'heure de début est obligatoire")
        LocalTime heureDebut,

        @NotNull(message = "L'heure de fin est obligatoire")
        LocalTime heureFin,

        @NotNull(message = "Le niveau est obligatoire")
        Long niveauId,

        @NotNull(message = "La salle est obligatoire")
        Long salleId
) {
}
