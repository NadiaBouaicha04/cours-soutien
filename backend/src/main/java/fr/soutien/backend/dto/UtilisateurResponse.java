package fr.soutien.backend.dto;

import fr.soutien.backend.entity.ModePaiement;
import fr.soutien.backend.entity.Role;
import fr.soutien.backend.entity.Utilisateur;

// Jamais de mot de passe dans une réponse
public record UtilisateurResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role,
        ModePaiement modePaiement,
        Integer nombrePaiements
) {

    public static UtilisateurResponse from(Utilisateur u) {
        return new UtilisateurResponse(
                u.getId(),
                u.getNom(),
                u.getPrenom(),
                u.getEmail(),
                u.getRole(),
                u.getModePaiement(),
                u.getNombrePaiements()
        );
    }
}
