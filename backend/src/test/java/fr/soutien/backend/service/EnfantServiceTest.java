package fr.soutien.backend.service;

import fr.soutien.backend.dto.EnfantResponse;
import fr.soutien.backend.entity.*;
import fr.soutien.backend.exception.RegleMetierException;
import fr.soutien.backend.exception.RessourceIntrouvableException;
import fr.soutien.backend.repository.CoursRepository;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.NiveauRepository;
import fr.soutien.backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnfantServiceTest {

    // Les faux repositories (mocks)
    @Mock
    private EnfantRepository enfantRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private NiveauRepository niveauRepository;
    @Mock
    private CoursRepository coursRepository;

    // Le vrai service, dans lequel Mockito injecte les mocks
    @InjectMocks
    private EnfantService enfantService;

    private static final Long PARENT_ID = 2L;

    private Niveau troisieme;
    private Niveau sixieme;
    private Enfant lea;

    // Exécuté avant CHAQUE test : on repart de données propres
    @BeforeEach
    void preparer() {
        troisieme = niveau(4L, "3e");
        sixieme = niveau(1L, "6e");

        lea = new Enfant();
        lea.setId(10L);
        lea.setNom("Durand");
        lea.setPrenom("Léa");
        lea.setDateNaissance(LocalDate.of(2012, 4, 15));
        lea.setNiveau(troisieme);
    }

    @Test
    @DisplayName("Inscription réussie : bon niveau et place libre")
    void inscrire_bonNiveauEtPlaceLibre() {
        Cours cours13 = cours(13L, troisieme, 12);
        when(enfantRepository.findByIdAndParentId(10L, PARENT_ID)).thenReturn(Optional.of(lea));
        when(coursRepository.findByIdForUpdate(13L)).thenReturn(Optional.of(cours13));
        when(enfantRepository.compterInscrits(13L)).thenReturn(3L);

        EnfantResponse resultat = enfantService.inscrire(PARENT_ID, 10L, 13L);

        assertThat(lea.getCours()).containsExactly(cours13);
        assertThat(resultat.cours()).hasSize(1);
        assertThat(resultat.cours().get(0).id()).isEqualTo(13L);
    }

    @Test
    @DisplayName("Refus : le cours n'est pas du niveau de l'enfant")
    void inscrire_mauvaisNiveau() {
        Cours coursDe6e = cours(1L, sixieme, 2);
        when(enfantRepository.findByIdAndParentId(10L, PARENT_ID)).thenReturn(Optional.of(lea));
        when(coursRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(coursDe6e));

        assertThatThrownBy(() -> enfantService.inscrire(PARENT_ID, 10L, 1L))
                .isInstanceOf(RegleMetierException.class)
                .hasMessage("Ce cours ne correspond pas au niveau de l'enfant");

        assertThat(lea.getCours()).isEmpty();
    }

    @Test
    @DisplayName("Refus : le cours est complet")
    void inscrire_coursComplet() {
        Cours cours13 = cours(13L, troisieme, 12);
        when(enfantRepository.findByIdAndParentId(10L, PARENT_ID)).thenReturn(Optional.of(lea));
        when(coursRepository.findByIdForUpdate(13L)).thenReturn(Optional.of(cours13));
        when(enfantRepository.compterInscrits(13L)).thenReturn(12L); // 12 inscrits sur 12 places

        assertThatThrownBy(() -> enfantService.inscrire(PARENT_ID, 10L, 13L))
                .isInstanceOf(RegleMetierException.class)
                .hasMessage("Le cours est complet");

        assertThat(lea.getCours()).isEmpty();
    }

    @Test
    @DisplayName("Refus : l'enfant n'appartient pas au parent")
    void inscrire_enfantDunAutreParent() {
        when(enfantRepository.findByIdAndParentId(10L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enfantService.inscrire(99L, 10L, 13L))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessage("Enfant introuvable");

        // le cours n'a même pas été cherché : on s'arrête dès le premier contrôle
        verify(coursRepository, never()).findByIdForUpdate(anyLong());
    }

    @Test
    @DisplayName("Un seul cours par enfant : le nouveau remplace l'ancien")
    void inscrire_remplaceAncienCours() {
        Cours cours13 = cours(13L, troisieme, 12);
        Cours cours14 = cours(14L, troisieme, 12);
        lea.getCours().add(cours13); // Léa suit déjà le cours 13

        when(enfantRepository.findByIdAndParentId(10L, PARENT_ID)).thenReturn(Optional.of(lea));
        when(coursRepository.findByIdForUpdate(14L)).thenReturn(Optional.of(cours14));
        when(enfantRepository.compterInscrits(14L)).thenReturn(0L);

        enfantService.inscrire(PARENT_ID, 10L, 14L);

        assertThat(lea.getCours()).containsExactly(cours14);
    }

    // ---------- Outils pour fabriquer les données de test ----------

    private Niveau niveau(Long id, String libelle) {
        Niveau n = new Niveau();
        n.setId(id);
        n.setLibelle(libelle);
        return n;
    }

    private Cours cours(Long id, Niveau niveau, int capacite) {
        Salle salle = new Salle();
        salle.setId(id);
        salle.setNom("Salle " + id);
        salle.setCapacite(capacite);

        Cours c = new Cours();
        c.setId(id);
        c.setJour(Jour.SAMEDI);
        c.setHeureDebut(LocalTime.of(10, 0));
        c.setHeureFin(LocalTime.of(12, 0));
        c.setNiveau(niveau);
        c.setSalle(salle);
        return c;
    }
}
