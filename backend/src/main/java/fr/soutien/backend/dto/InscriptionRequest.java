package fr.soutien.backend.dto;

import jakarta.validation.constraints.NotNull;

// Corps de PUT /api/parent/enfants/{id}/cours
public record InscriptionRequest(
        @NotNull(message = "Le cours est obligatoire")
        Long coursId
) {
}
