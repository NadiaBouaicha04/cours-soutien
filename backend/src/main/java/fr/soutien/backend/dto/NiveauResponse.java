package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Niveau;

import java.math.BigDecimal;

public record NiveauResponse(Long id, String libelle, Integer ordre, BigDecimal montant) {

    public static NiveauResponse from(Niveau n) {
        return new NiveauResponse(n.getId(), n.getLibelle(), n.getOrdre(), n.getMontant());
    }
}
