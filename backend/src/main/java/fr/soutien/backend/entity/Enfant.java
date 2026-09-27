package fr.soutien.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.HashSet;
import java.util.Set;

import java.time.LocalDate;

@Entity
@Table(
    name = "enfant",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_enfant_parent_identite",
        columnNames = {"parent_id", "nom", "prenom", "date_naissance"}
    ),
    indexes = {
        @Index(name = "idx_enfant_parent", columnList = "parent_id"),
        @Index(name = "idx_enfant_cours", columnList = "cours_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Enfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Column(length = 100)
    private String etablissement;

    // supprimer le parent supprime ses enfants
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Utilisateur parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

        @ManyToMany
    @JoinTable(
        name = "enfant_cours",
        joinColumns = @JoinColumn(name = "enfant_id"),
        inverseJoinColumns = @JoinColumn(name = "cours_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Cours> cours = new HashSet<>();
}
