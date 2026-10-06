package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Mouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MouvementRepository extends JpaRepository<Mouvement, Long> {

    List<Mouvement> findByUtilisateurIdOrderByDateMouvementDesc(Long utilisateurId);

    boolean existsByUtilisateurId(Long utilisateurId);

    @Query("""
           select coalesce(sum(m.montant), 0)
           from Mouvement m
           where m.utilisateur.id = :utilisateurId
           """)
    BigDecimal totalPaye(@Param("utilisateurId") Long utilisateurId);
}