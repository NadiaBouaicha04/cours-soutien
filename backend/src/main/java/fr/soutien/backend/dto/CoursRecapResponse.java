package fr.soutien.backend.dto;

import fr.soutien.backend.entity.Cours;
import fr.soutien.backend.entity.Enfant;
import fr.soutien.backend.entity.Jour;

import java.time.LocalTime;
import java.util.List;

// Récapitulatif d'un cours avec la liste de ses inscrits
public record CoursRecapResponse(
        Long id,
        Jour jour,
        LocalTime heureDebut,
        LocalTime heureFin,
        String niveau,
        String salle,
        Integer capacite,
        int nombreInscrits,
        List<Inscrit> inscrits
) {

    public record Inscrit(Long id, String nom, String prenom, String etablissement, String parent) {

        public static Inscrit from(Enfant e) {
            String parent = e.getParent().getPrenom() + " " + e.getParent().getNom();
            return new Inscrit(e.getId(), e.getNom(), e.getPrenom(), e.getEtablissement(), parent);
        }
    }

    public static CoursRecapResponse from(Cours c, List<Enfant> enfants) {
        List<Inscrit> inscrits = enfants.stream().map(Inscrit::from).toList();
        return new CoursRecapResponse(
                c.getId(),
                c.getJour(),
                c.getHeureDebut(),
                c.getHeureFin(),
                c.getNiveau().getLibelle(),
                c.getSalle().getNom(),
                c.getSalle().getCapacite(),
                inscrits.size(),
                inscrits
        );
    }
}
