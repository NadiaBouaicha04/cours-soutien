package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NiveauRepository extends JpaRepository<Niveau, Long> {

    List<Niveau> findAllByOrderByOrdreAsc();
}