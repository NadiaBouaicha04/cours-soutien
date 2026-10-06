package fr.soutien.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SalleRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 50)
        String nom,

        @NotNull(message = "La capacité est obligatoire")
        @Positive(message = "La capacité doit être supérieure à 0")
        Integer capacite
) {
}
