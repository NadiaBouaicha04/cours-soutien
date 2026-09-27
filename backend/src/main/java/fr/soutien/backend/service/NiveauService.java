package fr.soutien.backend.service;

import fr.soutien.backend.dto.NiveauResponse;
import fr.soutien.backend.repository.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NiveauService {

    private final NiveauRepository niveauRepository;

    // niveaux dans l'ordre scolaire, pour la liste déroulante
    public List<NiveauResponse> lister() {
        return niveauRepository.findAllByOrderByOrdreAsc().stream()
                .map(NiveauResponse::from)
                .toList();
    }
}
