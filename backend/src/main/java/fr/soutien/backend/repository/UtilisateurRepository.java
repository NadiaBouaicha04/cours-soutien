package fr.soutien.backend.repository;

import fr.soutien.backend.entity.Role;
import fr.soutien.backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Utilisateur> findByRoleOrderByNomAscPrenomAsc(Role role);
}