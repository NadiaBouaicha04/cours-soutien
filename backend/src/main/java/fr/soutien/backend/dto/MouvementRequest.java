package fr.soutien.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// Paiement reçu, saisi par le gestionnaire
public record MouvementRequest(
        @NotNull(message = "La date est obligatoire")
        LocalDate date,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être positif")
        BigDecimal montant,

        @Size(max = 100)
        String libelle
) {
}
