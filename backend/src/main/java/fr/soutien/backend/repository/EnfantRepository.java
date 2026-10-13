package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Enfant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EnfantRepository extends JpaRepository<Enfant, Long> {

    List<Enfant> findByParentId(Long parentId);

    Optional<Enfant> findByIdAndParentId(Long id, Long parentId);

    boolean existsByNiveauId(Long niveauId);

    @Query("select count(e) from Enfant e join e.cours c where c.id = :coursId")
    long compterInscrits(@Param("coursId") Long coursId);

    @Query("select e from Enfant e join e.cours c where c.id = :coursId order by e.nom, e.prenom")
    List<Enfant> findInscrits(@Param("coursId") Long coursId);

    @Query("""
           select coalesce(sum(e.niveau.montant), 0)
           from Enfant e
           where e.parent.id = :parentId and e.cours is not empty
           """)
    BigDecimal totalDu(@Param("parentId") Long parentId);
}