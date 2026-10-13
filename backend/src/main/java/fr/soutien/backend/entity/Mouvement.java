package fr.soutien.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "mouvement",
    indexes = @Index(name = "idx_mouvement_utilisateur", columnList = "utilisateur_id")
)
@Getter
@Setter
@NoArgsConstructor
public class Mouvement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateMouvement;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal montant;

    // ex : "Chèque n°1234"
    @Column(length = 100)
    private String libelle;

    // restrict par défaut : on garde la trace des paiements
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}
