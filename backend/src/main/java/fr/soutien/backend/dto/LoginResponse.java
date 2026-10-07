package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Role;

// Renvoyé après une connexion réussie
public record LoginResponse(String token, Role role, String prenom, String nom) {
}
