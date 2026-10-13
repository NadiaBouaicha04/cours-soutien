package fr.soutien.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(
    name = "cours",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cours_salle_creneau",
        columnNames = {"salle_id", "jour", "heure_debut"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Jour jour;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    // restrict par défaut : impossible de supprimer un niveau utilisé
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    // restrict par défaut : impossible de supprimer une salle utilisée
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;
}
