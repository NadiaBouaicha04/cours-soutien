# Conception de l'API REST

## Principes

- Échanges en **JSON**, authentification par **token JWT** (en-tête `Authorization: Bearer <token>`).
- Les URL désignent des **ressources** (noms), le **verbe HTTP** indique l'action.
- On n'expose jamais les entités : on échange des **DTO** (pas de mot de passe dans les réponses).
- Les URL parent ne contiennent jamais l'id du parent : il est déduit du token.

| Verbe    | Action               |
|----------|----------------------|
| `GET`    | lire                 |
| `POST`   | créer                |
| `PUT`    | modifier             |
| `DELETE` | supprimer            |

## Codes de retour

| Code  | Signification            | Exemple                               |
|-------|--------------------------|---------------------------------------|
| `200` | succès avec données      | lecture d'une liste                   |
| `201` | ressource créée          | ajout d'un enfant                     |
| `204` | succès sans données      | suppression                           |
| `400` | données invalides        | 7 versements, champ obligatoire vide  |
| `401` | non authentifié          | token absent ou expiré                |
| `403` | accès interdit           | un parent appelle une URL gestion     |
| `404` | introuvable              | enfant inexistant                     |
| `409` | règle métier non respectée | cours complet, créneau déjà pris    |

Format uniforme des erreurs :

```json
{ "status": 409, "message": "Le cours est complet" }
```

## Zones de sécurité

| Préfixe            | Accès                 |
|--------------------|-----------------------|
| `/api/auth/**`     | public                |
| `/api/parent/**`   | rôle `PARENT`         |
| `/api/gestion/**`  | rôle `GESTIONNAIRE`   |

## Endpoints

### Authentification

| Verbe  | URL               | Description                                   |
|--------|-------------------|-----------------------------------------------|
| `POST` | `/api/auth/login` | e-mail + mot de passe → token JWT             |
| `GET`  | `/api/auth/me`    | utilisateur connecté (nom, prénom, rôle)      |

### Espace parent

| Verbe    | URL                                   | Description                              |
|----------|---------------------------------------|------------------------------------------|
| `GET`    | `/api/parent/tableau-de-bord`         | enfants, solde, derniers mouvements      |
| `GET`    | `/api/parent/enfants`                 | liste de mes enfants                     |
| `POST`   | `/api/parent/enfants`                 | ajouter un enfant                        |
| `PUT`    | `/api/parent/enfants/{id}`            | modifier un enfant                       |
| `DELETE` | `/api/parent/enfants/{id}`            | supprimer un enfant                      |
| `GET`    | `/api/parent/cours-disponibles?niveauId={id}` | cours non complets d'un niveau   |
| `PUT`    | `/api/parent/enfants/{id}/cours`      | inscrire / changer de cours              |
| `DELETE` | `/api/parent/enfants/{id}/cours`      | désinscrire                              |
| `PUT`    | `/api/parent/paiement`                | mode et nombre de versements             |
| `GET`    | `/api/parent/solde`                   | solde + liste des mouvements             |
| `GET`    | `/api/parent/niveaux`                 | liste des niveaux                        |

### Espace gestionnaire

| Verbe                | URL                                        | Description                     |
|----------------------|--------------------------------------------|---------------------------------|
| `GET` `POST`         | `/api/gestion/utilisateurs`                | liste / création                |
| `GET` `PUT` `DELETE` | `/api/gestion/utilisateurs/{id}`           | détail / modification / suppression |
| `GET` `POST`         | `/api/gestion/utilisateurs/{id}/mouvements`| historique / ajout d'un paiement |
| `GET` `POST`         | `/api/gestion/salles`                      | liste / création                |
| `PUT` `DELETE`       | `/api/gestion/salles/{id}`                 | modification / suppression      |
| `GET` `POST`         | `/api/gestion/niveaux`                     | liste / création                |
| `PUT` `DELETE`       | `/api/gestion/niveaux/{id}`                | modification / suppression      |
| `GET` `POST`         | `/api/gestion/cours`                       | liste / création                |
| `PUT` `DELETE`       | `/api/gestion/cours/{id}`                  | modification / suppression      |
| `GET`                | `/api/gestion/cours/recap`                 | cours avec la liste des inscrits |

## Exemples d'échanges

### Connexion

```json
// POST /api/auth/login
{ "email": "durand@mail.fr", "motDePasse": "secret123" }

// 200
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "role": "PARENT", "prenom": "Claire" }
```

### Ajout d'un enfant

```json
// POST /api/parent/enfants
{
  "nom": "Durand",
  "prenom": "Léa",
  "dateNaissance": "2012-04-15",
  "etablissement": "Collège Montaigne",
  "niveauId": 4
}

// 201
{
  "id": 10,
  "nom": "Durand",
  "prenom": "Léa",
  "dateNaissance": "2012-04-15",
  "etablissement": "Collège Montaigne",
  "niveau": "3e",
  "cours": null
}
```

### Cours disponibles

```json
// GET /api/parent/cours-disponibles?niveauId=4
[
  { "id": 7, "jour": "SAMEDI", "heureDebut": "10:00", "heureFin": "12:00",
    "salle": "Salle C", "placesRestantes": 3 }
]
```

### Inscription à un cours

```json
// PUT /api/parent/enfants/10/cours
{ "coursId": 7 }

// 409 si le cours est devenu complet
{ "status": 409, "message": "Le cours est complet" }
```

### Solde

```json
// GET /api/parent/solde
{
  "totalDu": 600.00,
  "totalPaye": 300.00,
  "soldeRestant": 300.00,
  "mouvements": [
    { "date": "2026-09-25", "montant": 300.00, "libelle": "Virement" }
  ]
}
```

### Enregistrement d'un paiement (gestionnaire)

```json
// POST /api/gestion/utilisateurs/3/mouvements
{ "date": "2026-10-20", "montant": 100.00, "libelle": "Chèque n°1234" }

// 201
{ "id": 15, "date": "2026-10-20", "montant": 100.00, "libelle": "Chèque n°1234" }
```

### Récapitulatif des cours

```json
// GET /api/gestion/cours/recap
[
  {
    "id": 7,
    "jour": "SAMEDI",
    "heureDebut": "10:00",
    "heureFin": "12:00",
    "niveau": "3e",
    "salle": "Salle C",
    "capacite": 12,
    "nombreInscrits": 2,
    "inscrits": [
      { "id": 10, "nom": "Durand", "prenom": "Léa", "etablissement": "Collège Montaigne" },
      { "id": 11, "nom": "Martin", "prenom": "Hugo", "etablissement": "Collège Rabelais" }
    ]
  }
]
```

## Écrans et endpoints utilisés

| Écran                               | Endpoints                                                    |
|-------------------------------------|--------------------------------------------------------------|
| Connexion                           | `POST /api/auth/login`                                       |
| Parent : tableau de bord            | `GET /api/parent/tableau-de-bord`                            |
| Parent : enfant + inscription       | `GET/POST/PUT /api/parent/enfants`, `GET /api/parent/niveaux`, `GET /api/parent/cours-disponibles`, `PUT /api/parent/enfants/{id}/cours` |
| Parent : solde + mouvements         | `GET /api/parent/solde`                                      |
| Gestion : liste des utilisateurs    | `GET /api/gestion/utilisateurs`                              |
| Gestion : mise à jour utilisateur   | `POST/PUT/DELETE /api/gestion/utilisateurs`                  |
| Gestion : ajout mouvement           | `GET/POST /api/gestion/utilisateurs/{id}/mouvements`         |
| Gestion : salles, cours, niveaux    | `/api/gestion/salles`, `/api/gestion/cours`, `/api/gestion/niveaux` |
| Gestion : récap cours               | `GET /api/gestion/cours/recap`                               |
