package fr.soutien.backend.config;

import fr.soutien.backend.entity.*;
import fr.soutien.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

// Insère des données de test au démarrage, uniquement si la base est vide
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final NiveauRepository niveauRepository;
    private final SalleRepository salleRepository;
    private final CoursRepository coursRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementRepository mouvementRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (niveauRepository.count() > 0) {
            log.info("Données déjà présentes : initialisation ignorée");
            return;
        }

        String[] libelles = {"6e", "5e", "4e", "3e", "2nde", "1re", "Terminale"};
        String[] montants = {"250.00", "250.00", "280.00", "300.00", "320.00", "350.00", "380.00"};
        // la salle A n'a que 2 places : pratique pour tester "cours complet"
        int[] capacites = {2, 10, 12, 12, 15, 15, 20};

        // les 4 créneaux de l'exemple du sujet
        Jour[] jours = {Jour.SAMEDI, Jour.SAMEDI, Jour.DIMANCHE, Jour.DIMANCHE};
        LocalTime[] debuts = {LocalTime.of(10, 0), LocalTime.of(14, 0), LocalTime.of(10, 0), LocalTime.of(14, 0)};

        for (int i = 0; i < libelles.length; i++) {
            Niveau niveau = new Niveau();
            niveau.setLibelle(libelles[i]);
            niveau.setOrdre(i + 1);
            niveau.setMontant(new BigDecimal(montants[i]));
            niveauRepository.save(niveau);

            Salle salle = new Salle();
            salle.setNom("Salle " + (char) ('A' + i));
            salle.setCapacite(capacites[i]);
            salleRepository.save(salle);

            // chaque niveau a un cours sur chacun des 4 créneaux, dans "sa" salle
            for (int c = 0; c < jours.length; c++) {
                Cours cours = new Cours();
                cours.setJour(jours[c]);
                cours.setHeureDebut(debuts[c]);
                cours.setHeureFin(debuts[c].plusHours(2));
                cours.setNiveau(niveau);
                cours.setSalle(salle);
                coursRepository.save(cours);
            }
        }

        Utilisateur gestionnaire = new Utilisateur();
        gestionnaire.setNom("Admin");
        gestionnaire.setPrenom("Paul");
        gestionnaire.setEmail("admin@soutien.fr");
        gestionnaire.setMotDePasse(passwordEncoder.encode("admin123"));
        gestionnaire.setRole(Role.GESTIONNAIRE);
        utilisateurRepository.save(gestionnaire);

        Utilisateur parent = new Utilisateur();
        parent.setNom("Durand");
        parent.setPrenom("Claire");
        parent.setEmail("durand@mail.fr");
        parent.setMotDePasse(passwordEncoder.encode("parent123"));
        parent.setRole(Role.PARENT);
        parent.setModePaiement(ModePaiement.CHEQUE);
        parent.setNombrePaiements(3);
        utilisateurRepository.save(parent);

        Mouvement paiement = new Mouvement();
        paiement.setDateMouvement(LocalDate.of(2026, 9, 20));
        paiement.setMontant(new BigDecimal("100.00"));
        paiement.setLibelle("Chèque n°1");
        paiement.setUtilisateur(parent);
        mouvementRepository.save(paiement);

        log.info("Données de test insérées : 7 niveaux, 7 salles, 28 cours, 2 utilisateurs");
    }
}
