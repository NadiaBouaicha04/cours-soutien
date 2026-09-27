package fr.soutien.backend.service;

import fr.soutien.backend.dto.CoursDisponibleResponse;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoursService {

    private final CoursRepository coursRepository;
    private final EnfantRepository enfantRepository;

    // Règle : un cours complet ne doit pas être proposé à l'inscription
    public List<CoursDisponibleResponse> coursDisponibles(Long niveauId) {
        return coursRepository.findByNiveauId(niveauId).stream()
                .map(c -> CoursDisponibleResponse.from(
                        c,
                        c.getSalle().getCapacite() - enfantRepository.compterInscrits(c.getId())))
                .filter(c -> c.placesRestantes() > 0)
                // tri en Java : l'ordre de l'enum Jour (LUNDI -> DIMANCHE), puis l'heure
                .sorted(Comparator.comparing(CoursDisponibleResponse::jour)
                        .thenComparing(CoursDisponibleResponse::heureDebut))
                .toList();
    }
}
