package fr.soutien.backend.service;

import fr.soutien.backend.dto.EnfantRequest;
import fr.soutien.backend.dto.EnfantResponse;
import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Enfant;
import fr.soutien.backend.entity.Niveau;
import fr.soutien.backend.entity.Utilisateur;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.NiveauRepository;
import fr.soutien.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnfantService {

    // Règle du sujet : un seul cours par enfant.
    // Passer à true pour autoriser plusieurs cours (la base le permet déjà).
    private static final boolean PLUSIEURS_COURS_AUTORISES = false;

    private final EnfantRepository enfantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NiveauRepository niveauRepository;
    private final CoursRepository coursRepository;

    @Transactional(readOnly = true)
    public List<EnfantResponse> lister(Long parentId) {
        return enfantRepository.findByParentId(parentId).stream()
                .map(EnfantResponse::from)
                .toList();
    }

    public EnfantResponse creer(Long parentId, EnfantRequest req) {
        Utilisateur parent = utilisateurRepository.findById(parentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Parent introuvable"));

        Enfant enfant = new Enfant();
        enfant.setParent(parent);
        appliquer(enfant, req);
        return EnfantResponse.from(enfantRepository.save(enfant));
    }

    public EnfantResponse modifier(Long parentId, Long enfantId, EnfantRequest req) {
        Enfant enfant = trouverEnfant(parentId, enfantId);
        appliquer(enfant, req);

        // Règle : si le niveau a changé, on retire les cours qui ne correspondent plus
        enfant.getCours().removeIf(c -> !c.getNiveau().getId().equals(enfant.getNiveau().getId()));

        // Pas besoin de save() : l'entité est suivie par JPA et sera
        // mise à jour automatiquement à la fin de la transaction
        return EnfantResponse.from(enfant);
    }

    public void supprimer(Long parentId, Long enfantId) {
        enfantRepository.delete(trouverEnfant(parentId, enfantId));
    }

    public EnfantResponse inscrire(Long parentId, Long enfantId, Long coursId) {
        Enfant enfant = trouverEnfant(parentId, enfantId);

        // Verrou sur le cours : protège la dernière place contre deux inscriptions simultanées
        Cours cours = coursRepository.findByIdForUpdate(coursId)
                .orElseThrow(() -> new RessourceIntrouvableException("Cours introuvable"));

        // Règle : le cours doit correspondre au niveau de l'enfant
        if (!cours.getNiveau().getId().equals(enfant.getNiveau().getId())) {
            throw new RegleMetierException("Ce cours ne correspond pas au niveau de l'enfant");
        }

        // Déjà inscrit à ce cours : rien à faire
        boolean dejaInscrit = enfant.getCours().stream().anyMatch(c -> c.getId().equals(coursId));
        if (dejaInscrit) {
            return EnfantResponse.from(enfant);
        }

        // Règle : pas plus d'inscrits que la capacité de la salle
        long inscrits = enfantRepository.compterInscrits(coursId);
        if (inscrits >= cours.getSalle().getCapacite()) {
            throw new RegleMetierException("Le cours est complet");
        }

        // Règle : un seul cours par enfant -> le nouveau cours remplace l'ancien
        if (!PLUSIEURS_COURS_AUTORISES) {
            enfant.getCours().clear();
        }

        enfant.getCours().add(cours);
        return EnfantResponse.from(enfant);
    }

    public EnfantResponse desinscrire(Long parentId, Long enfantId, Long coursId) {
        Enfant enfant = trouverEnfant(parentId, enfantId);

        boolean retire = enfant.getCours().removeIf(c -> c.getId().equals(coursId));
        if (!retire) {
            throw new RessourceIntrouvableException("L'enfant n'est pas inscrit à ce cours");
        }
        return EnfantResponse.from(enfant);
    }

    // Cherche l'enfant EN VÉRIFIANT qu'il appartient bien au parent
    private Enfant trouverEnfant(Long parentId, Long enfantId) {
        return enfantRepository.findByIdAndParentId(enfantId, parentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Enfant introuvable"));
    }

    // Copie les données de la requête dans l'entité
    private void appliquer(Enfant enfant, EnfantRequest req) {
        Niveau niveau = niveauRepository.findById(req.niveauId())
                .orElseThrow(() -> new RessourceIntrouvableException("Niveau introuvable"));

        enfant.setNom(req.nom().trim());
        enfant.setPrenom(req.prenom().trim());
        enfant.setDateNaissance(req.dateNaissance());
        enfant.setEtablissement(req.etablissement());
        enfant.setNiveau(niveau);
    }
}
