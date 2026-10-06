package fr.soutien.backend.service;

import fr.soutien.backend.dto.SalleRequest;
import fr.soutien.backend.dto.SalleResponse;
import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Salle;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;
    private final CoursRepository coursRepository;
    private final EnfantRepository enfantRepository;

    @Transactional(readOnly = true)
    public List<SalleResponse> lister() {
        return salleRepository.findAll(Sort.by("nom")).stream()
                .map(SalleResponse::from)
                .toList();
    }

    public SalleResponse creer(SalleRequest req) {
        Salle salle = new Salle();
        salle.setNom(req.nom().trim());
        salle.setCapacite(req.capacite());
        return SalleResponse.from(salleRepository.save(salle));
    }

    public SalleResponse modifier(Long id, SalleRequest req) {
        Salle salle = chercher(id);

        // Règle : on ne peut pas réduire la capacité en dessous du nombre
        // d'inscrits d'un des cours qui ont lieu dans cette salle
        for (Cours cours : coursRepository.findBySalleId(id)) {
            long inscrits = enfantRepository.compterInscrits(cours.getId());
            if (req.capacite() < inscrits) {
                throw new RegleMetierException(
                        "Capacité trop petite : un cours de cette salle a déjà " + inscrits + " inscrits");
            }
        }

        salle.setNom(req.nom().trim());
        salle.setCapacite(req.capacite());
        return SalleResponse.from(salle);
    }

    public void supprimer(Long id) {
        Salle salle = chercher(id);

        // Règle : une salle utilisée par un cours ne peut pas être supprimée
        if (coursRepository.existsBySalleId(id)) {
            throw new RegleMetierException("Impossible de supprimer une salle utilisée par des cours");
        }
        salleRepository.delete(salle);
    }

    private Salle chercher(Long id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Salle introuvable"));
    }
}
