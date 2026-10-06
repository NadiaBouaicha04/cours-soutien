package fr.soutien.backend.dto;

import fr.soutien.backend.entity.ModePaiement;
import fr.soutien.backend.entity.Role;
import jakarta.validation.constraints.*;

// Données envoyées par le gestionnaire pour créer ou modifier un compte
public record UtilisateurRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 50)
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 50)
        String prenom,

        @NotBlank(message = "L'e-mail est obligatoire")
        @Email(message = "L'e-mail n'est pas valide")
        @Size(max = 150)
        String email,

        // obligatoire à la création, facultatif à la modification
        // (vide = on garde l'ancien mot de passe)
        @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
        String motDePasse,

        @NotNull(message = "Le rôle est obligatoire")
        Role role,

        // obligatoires pour un parent, ignorés pour un gestionnaire
        ModePaiement modePaiement,

        @Min(value = 1, message = "Au moins 1 versement")
        @Max(value = 6, message = "Au maximum 6 versements")
        Integer nombrePaiements
) {
}
