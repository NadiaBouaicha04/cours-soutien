package fr.soutien.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Données envoyées par le front pour créer ou modifier un enfant
public record EnfantRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 50)
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 50)
        String prenom,

        @NotNull(message = "La date de naissance est obligatoire")
        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate dateNaissance,

        @Size(max = 100)
        String etablissement,

        @NotNull(message = "Le niveau est obligatoire")
        Long niveauId
) {
}
