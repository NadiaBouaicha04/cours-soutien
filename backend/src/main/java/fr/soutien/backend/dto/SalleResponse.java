package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Salle;

public record SalleResponse(Long id, String nom, Integer capacite) {

    public static SalleResponse from(Salle s) {
        return new SalleResponse(s.getId(), s.getNom(), s.getCapacite());
    }
}
