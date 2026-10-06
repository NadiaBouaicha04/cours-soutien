package fr.soutien.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record NiveauRequest(
        @NotBlank(message = "Le libellé est obligatoire")
        @Size(max = 20)
        String libelle,

        @NotNull(message = "L'ordre est obligatoire")
        @Positive(message = "L'ordre doit être supérieur à 0")
        Integer ordre,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être positif")
        BigDecimal montant
) {
}
