# Analyse du besoin

## Acteurs

- **Parent** : inscrit ses enfants à un cours et consulte ce qu'il doit payer.
- **Gestionnaire** : employé de l'association, administre comptes, paiements et cours.

Les comptes sont créés par le gestionnaire (pas d'inscription libre des parents).

## Fonctionnalités

### Parent

- Se connecter (e-mail + mot de passe)
- Consulter son tableau de bord : enfants, solde, mouvements
- Ajouter / modifier un enfant et l'inscrire à un cours
- Consulter son solde et la liste de ses paiements

### Gestionnaire

- Se connecter
- Créer, modifier, supprimer des comptes utilisateurs
- Consulter les mouvements d'un compte et enregistrer un paiement
- Gérer les salles, les niveaux et les cours
- Afficher le récapitulatif des cours avec les enfants inscrits

## Règles métier

- Un enfant est inscrit à un seul cours au maximum.
- Le niveau du cours doit correspondre au niveau de l'enfant.
- Un cours = un créneau fixe + une salle + un niveau.
- Places disponibles = capacité de la salle - nombre d'inscrits.
- Un cours complet n'est pas proposé à l'inscription.
- Chaque niveau a un tarif.
- Paiement en 1 à 6 versements, par chèque, espèces ou virement.
- Pas de paiement en ligne : le gestionnaire saisit les paiements reçus (mouvements).

## Choix de conception

- **Solde calculé, non stocké** : somme des tarifs des niveaux des enfants inscrits - somme des mouvements.
- **Mode et nombre de versements** : choisis une fois par parent, pas par enfant.
- **Mouvements liés au parent**, pas à un enfant.
- **Changement de cours autorisé** s'il reste de la place.
- **Dernière place** : vérification côté serveur au moment de l'inscription.
- **Suppression d'un parent** : refusée s'il a des mouvements (trace comptable).
  EOF
