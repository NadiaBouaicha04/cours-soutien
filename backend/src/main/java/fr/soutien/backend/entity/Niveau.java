package fr.soutien.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "niveau")
@Getter
@Setter
@NoArgsConstructor
public class Niveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String libelle;

    // 1 = 6e ... 7 = Terminale, sert au tri
    @Column(nullable = false, unique = true)
    private Integer ordre;

    // tarif du niveau
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal montant;
}
