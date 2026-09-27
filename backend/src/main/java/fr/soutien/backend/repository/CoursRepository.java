package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Cours;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoursRepository extends JpaRepository<Cours, Long> {

    List<Cours> findByNiveauId(Long niveauId);

    boolean existsByNiveauId(Long niveauId);

    boolean existsBySalleId(Long salleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cours c where c.id = :id")
    Optional<Cours> findByIdForUpdate(@Param("id") Long id);
}