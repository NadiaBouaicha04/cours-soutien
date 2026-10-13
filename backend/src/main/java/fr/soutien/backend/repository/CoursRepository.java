package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Jour;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoursRepository extends JpaRepository<Cours, Long> {

    // cours d'un niveau (le filtre "non complet" est fait dans le service)
    List<Cours> findByNiveauId(Long niveauId);

    // cours qui ont lieu dans une salle (vérifier la capacité)
    List<Cours> findBySalleId(Long salleId);

    // cours d'une salle un jour donné (vérifier les chevauchements d'horaires)
    List<Cours> findBySalleIdAndJour(Long salleId, Jour jour);

    // empêcher la suppression d'un niveau ou d'une salle utilisés
    boolean existsByNiveauId(Long niveauId);

    boolean existsBySalleId(Long salleId);

    // lit le cours en le verrouillant jusqu'à la fin de la transaction
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cours c where c.id = :id")
    Optional<Cours> findByIdForUpdate(@Param("id") Long id);
}
