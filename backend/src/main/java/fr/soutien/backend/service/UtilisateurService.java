package fr.soutien.backend.service;

import fr.soutien.backend.dto.UtilisateurRequest;
import fr.soutien.backend.dto.UtilisateurResponse;
import fr.soutien.backend.entity.Role;
import fr.soutien.backend.entity.Utilisateur;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.MouvementRepository;
import fr.soutien.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final MouvementRepository mouvementRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UtilisateurResponse> lister() {
        return utilisateurRepository.findAll().stream()
                .map(UtilisateurResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurResponse trouver(Long id) {
        return UtilisateurResponse.from(chercher(id));
    }

    public UtilisateurResponse creer(UtilisateurRequest req) {
        String email = normaliser(req.email());

        // Règle : l'e-mail sert d'identifiant, il doit être unique
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RegleMetierException("Cet e-mail est déjà utilisé");
        }
        // Règle : un nouveau compte doit avoir un mot de passe
        if (req.motDePasse() == null || req.motDePasse().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        Utilisateur u = new Utilisateur();
        appliquer(u, req, email);
        // Le mot de passe est haché, jamais stocké en clair
        u.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        return UtilisateurResponse.from(utilisateurRepository.save(u));
    }

    public UtilisateurResponse modifier(Long id, UtilisateurRequest req) {
        Utilisateur u = chercher(id);
        String email = normaliser(req.email());

        // Règle : si l'e-mail change, le nouveau ne doit pas être déjà pris
        if (!email.equals(u.getEmail()) && utilisateurRepository.existsByEmail(email)) {
            throw new RegleMetierException("Cet e-mail est déjà utilisé");
        }

        appliquer(u, req, email);
        // Mot de passe vide = on garde l'ancien
        if (req.motDePasse() != null && !req.motDePasse().isBlank()) {
            u.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        }
        return UtilisateurResponse.from(u);
    }

    public void supprimer(Long id) {
        Utilisateur u = chercher(id);

        // Règle : on garde la trace des paiements, donc on refuse de
        // supprimer un utilisateur qui a déjà des mouvements
        if (mouvementRepository.existsByUtilisateurId(id)) {
            throw new RegleMetierException("Impossible de supprimer un utilisateur qui a des paiements enregistrés");
        }
        // Ses enfants sont supprimés automatiquement (ON DELETE CASCADE)
        utilisateurRepository.delete(u);
    }

    private Utilisateur chercher(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable"));
    }

    private void appliquer(Utilisateur u, UtilisateurRequest req, String email) {
        u.setNom(req.nom().trim());
        u.setPrenom(req.prenom().trim());
        u.setEmail(email);
        u.setRole(req.role());

        if (req.role() == Role.PARENT) {
            // Règle : un parent doit choisir un mode et un nombre de versements
            if (req.modePaiement() == null || req.nombrePaiements() == null) {
                throw new IllegalArgumentException("Un parent doit avoir un mode et un nombre de paiements");
            }
            u.setModePaiement(req.modePaiement());
            u.setNombrePaiements(req.nombrePaiements());
        } else {
            // Un gestionnaire ne paie pas
            u.setModePaiement(null);
            u.setNombrePaiements(null);
        }
    }

    // "  Durand@Mail.FR " -> "durand@mail.fr" : évite les doublons à cause de la casse
    private String normaliser(String email) {
        return email.trim().toLowerCase();
    }
}
