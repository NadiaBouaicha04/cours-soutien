package fr.soutien.backend.service;

import fr.soutien.backend.dto.SoldeResponse;
import fr.soutien.backend.entity.Mouvement;
import fr.soutien.backend.repository.EnfantRepository;
import fr.soutien.backend.repository.MouvementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoldeServiceTest {

    @Mock
    private EnfantRepository enfantRepository;
    @Mock
    private MouvementRepository mouvementRepository;

    @InjectMocks
    private SoldeService soldeService;

    @Test
    @DisplayName("Solde = total dû - total payé")
    void solde_calcule() {
        Mouvement cheque = new Mouvement();
        cheque.setId(1L);
        cheque.setDateMouvement(LocalDate.of(2026, 9, 20));
        cheque.setMontant(new BigDecimal("100.00"));
        cheque.setLibelle("Chèque n°1");

        when(enfantRepository.totalDu(2L)).thenReturn(new BigDecimal("300.00"));
        when(mouvementRepository.totalPaye(2L)).thenReturn(new BigDecimal("100.00"));
        when(mouvementRepository.findByUtilisateurIdOrderByDateMouvementDesc(2L)).thenReturn(List.of(cheque));

        SoldeResponse solde = soldeService.solde(2L);

        // isEqualByComparingTo : compare les valeurs (200 = 200.00), pas l'écriture
        assertThat(solde.totalDu()).isEqualByComparingTo("300");
        assertThat(solde.totalPaye()).isEqualByComparingTo("100");
        assertThat(solde.soldeRestant()).isEqualByComparingTo("200");
        assertThat(solde.mouvements()).hasSize(1);
    }

    @Test
    @DisplayName("Aucun enfant inscrit et aucun paiement : solde à 0")
    void solde_vide() {
        when(enfantRepository.totalDu(2L)).thenReturn(BigDecimal.ZERO);
        when(mouvementRepository.totalPaye(2L)).thenReturn(BigDecimal.ZERO);
        when(mouvementRepository.findByUtilisateurIdOrderByDateMouvementDesc(2L)).thenReturn(List.of());

        SoldeResponse solde = soldeService.solde(2L);

        assertThat(solde.soldeRestant()).isEqualByComparingTo("0");
        assertThat(solde.mouvements()).isEmpty();
    }
}
