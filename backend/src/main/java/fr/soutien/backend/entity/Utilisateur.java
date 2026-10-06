package fr.soutien.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // hash BCrypt, jamais le mot de passe en clair
    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // null pour un gestionnaire
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ModePaiement modePaiement;

    // 1 (comptant) à 6, null pour un gestionnaire
    private Integer nombrePaiements;
}
