package fr.soutien.backend.service;

import fr.soutien.backend.dto.MouvementRequest;
import fr.soutien.backend.dto.MouvementResponse;
import fr.soutien.backend.entity.Mouvement;
import fr.soutien.backend.entity.Role;
import fr.soutien.backend.entity.Utilisateur;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.MouvementRepository;
import fr.soutien.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MouvementService {

    private final MouvementRepository mouvementRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Le gestionnaire enregistre un paiement reçu
    public MouvementResponse ajouter(Long utilisateurId, MouvementRequest req) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable"));

        // Règle : seuls les parents paient
        if (u.getRole() != Role.PARENT) {
            throw new RegleMetierException("Un paiement ne peut être enregistré que pour un parent");
        }

        Mouvement m = new Mouvement();
        m.setDateMouvement(req.date());
        m.setMontant(req.montant());
        m.setLibelle(req.libelle());
        m.setUtilisateur(u);
        return MouvementResponse.from(mouvementRepository.save(m));
    }
}
