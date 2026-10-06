package fr.soutien.backend.service;

import fr.soutien.backend.dto.NiveauRequest;
import fr.soutien.backend.dto.NiveauResponse;
import fr.soutien.backend.entity.Niveau;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NiveauService {

    private final NiveauRepository niveauRepository;
    private final CoursRepository coursRepository;
    private final EnfantRepository enfantRepository;

    // niveaux dans l'ordre scolaire, pour la liste déroulante
    @Transactional(readOnly = true)
    public List<NiveauResponse> lister() {
        return niveauRepository.findAllByOrderByOrdreAsc().stream()
                .map(NiveauResponse::from)
                .toList();
    }

    // libellé ou ordre déjà utilisé -> la base refuse (contrainte unique) -> 409
    public NiveauResponse creer(NiveauRequest req) {
        Niveau niveau = new Niveau();
        appliquer(niveau, req);
        return NiveauResponse.from(niveauRepository.save(niveau));
    }

    // Changer le montant change automatiquement le solde des parents,
    // puisque le solde est calculé (jamais stocké)
    public NiveauResponse modifier(Long id, NiveauRequest req) {
        Niveau niveau = chercher(id);
        appliquer(niveau, req);
        return NiveauResponse.from(niveau);
    }

    public void supprimer(Long id) {
        Niveau niveau = chercher(id);

        // Règle : un niveau utilisé par un cours ou un enfant ne peut pas être supprimé
        if (coursRepository.existsByNiveauId(id) || enfantRepository.existsByNiveauId(id)) {
            throw new RegleMetierException("Impossible de supprimer un niveau utilisé par des cours ou des enfants");
        }
        niveauRepository.delete(niveau);
    }

    private Niveau chercher(Long id) {
        return niveauRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Niveau introuvable"));
    }

    private void appliquer(Niveau niveau, NiveauRequest req) {
        niveau.setLibelle(req.libelle().trim());
        niveau.setOrdre(req.ordre());
        niveau.setMontant(req.montant());
    }
}
