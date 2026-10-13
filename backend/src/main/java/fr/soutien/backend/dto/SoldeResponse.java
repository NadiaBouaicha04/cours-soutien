package fr.soutien.backend.dto;

import java.math.BigDecimal;
import java.util.List;

// Réponse de GET /api/parent/solde
public record SoldeResponse(
        BigDecimal totalDu,
        BigDecimal totalPaye,
        BigDecimal soldeRestant,
        List<MouvementResponse> mouvements
) {
}
