package fr.soutien.backend.service;

import fr.soutien.backend.dto.MouvementResponse;
import fr.soutien.backend.dto.SoldeResponse;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.MouvementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoldeService {

    private final EnfantRepository enfantRepository;
    private final MouvementRepository mouvementRepository;

    // Le solde n'est pas stocké : il est calculé à chaque demande
    public SoldeResponse solde(Long parentId) {
        BigDecimal totalDu = enfantRepository.totalDu(parentId);
        BigDecimal totalPaye = mouvementRepository.totalPaye(parentId);

        List<MouvementResponse> mouvements = mouvementRepository
                .findByUtilisateurIdOrderByDateMouvementDesc(parentId).stream()
                .map(MouvementResponse::from)
                .toList();

        return new SoldeResponse(totalDu, totalPaye, totalDu.subtract(totalPaye), mouvements);
    }
}
