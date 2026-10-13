package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Mouvement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MouvementResponse(Long id, LocalDate date, BigDecimal montant, String libelle) {

    public static MouvementResponse from(Mouvement m) {
        return new MouvementResponse(m.getId(), m.getDateMouvement(), m.getMontant(), m.getLibelle());
    }
}
