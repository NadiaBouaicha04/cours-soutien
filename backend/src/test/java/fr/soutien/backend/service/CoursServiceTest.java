package fr.soutien.backend.service;

import fr.soutien.backend.dto.CoursDisponibleResponse;
import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Jour;
import fr.soutien.backend.entity.Niveau;
import fr.soutien.backend.entity.Salle;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.NiveauRepository;
import fr.soutien.backend.repository.SalleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoursServiceTest {

    @Mock
    private CoursRepository coursRepository;
    @Mock
    private EnfantRepository enfantRepository;
    @Mock
    private NiveauRepository niveauRepository;
    @Mock
    private SalleRepository salleRepository;

    @InjectMocks
    private CoursService coursService;

    @Test
    @DisplayName("Un cours complet n'est pas proposé, les autres sont triés par jour")
    void coursDisponibles_filtreLesComplets() {
        Niveau troisieme = new Niveau();
        troisieme.setId(4L);

        Cours dimanche = cours(15L, Jour.DIMANCHE, troisieme, 10);
        Cours samediComplet = cours(13L, Jour.SAMEDI, troisieme, 12);
        Cours samedi = cours(14L, Jour.SAMEDI, troisieme, 12);

        when(coursRepository.findByNiveauId(4L)).thenReturn(List.of(dimanche, samediComplet, samedi));
        when(enfantRepository.compterInscrits(15L)).thenReturn(3L);   // 10 - 3 = 7 places
        when(enfantRepository.compterInscrits(13L)).thenReturn(12L);  // 12 - 12 = 0 : complet
        when(enfantRepository.compterInscrits(14L)).thenReturn(0L);   // 12 places

        List<CoursDisponibleResponse> resultat = coursService.coursDisponibles(4L);

        // le cours 13 (complet) a disparu ; samedi avant dimanche
        assertThat(resultat).extracting(CoursDisponibleResponse::id).containsExactly(14L, 15L);
        assertThat(resultat).extracting(CoursDisponibleResponse::placesRestantes).containsExactly(12L, 7L);
    }

    private Cours cours(Long id, Jour jour, Niveau niveau, int capacite) {
        Salle salle = new Salle();
        salle.setId(id);
        salle.setNom("Salle " + id);
        salle.setCapacite(capacite);

        Cours c = new Cours();
        c.setId(id);
        c.setJour(jour);
        c.setHeureDebut(LocalTime.of(10, 0));
        c.setHeureFin(LocalTime.of(12, 0));
        c.setNiveau(niveau);
        c.setSalle(salle);
        return c;
    }
}
