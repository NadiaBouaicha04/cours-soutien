package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, Long> {
}