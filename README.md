# Cours de soutien

Application web d'inscription d'enfants à des cours de soutien pour une association qui accompagne des collégiens et lycéens, de la 6e à la Terminale.

- Un **parent** inscrit chacun de ses enfants à un cours correspondant à son niveau, et consulte son solde et ses paiements.
- Un **gestionnaire** gère les comptes, enregistre les paiements reçus, administre les salles, niveaux et cours, et consulte la liste des inscrits.

## Technologies

| Partie | Technologies |
|---|---|
| Back-end | Java 21, Spring Boot 4, Gradle (Kotlin DSL), Spring Data JPA / Hibernate, Spring Security, JWT |
| Base de données | PostgreSQL 18 |
| Front-end | Nuxt 4, Vue 3, TypeScript, pnpm |
| Tests | JUnit 5, Mockito, AssertJ |

## Structure du dépôt

```
cours-soutien/
├── backend/    API REST (Spring Boot)
├── frontend/   interface web (Nuxt)
└── docs/       analyse, modèle de données, conception de l'API
```

Le back-end suit une architecture en couches :

```
Controller  →  Service  →  Repository  →  PostgreSQL
```

| Dossier (`backend/src/main/java/fr/soutien/backend/`) | Rôle |
|---|---|
| `controller/` | les URL de l'API |
| `service/` | les règles métier |
| `repository/` | l'accès à la base |
| `entity/` | les tables (entités JPA) |
| `dto/` | les données JSON échangées avec le front |
| `exception/` | la transformation des erreurs en réponses HTTP |
| `config/`, `security/` | sécurité JWT, CORS, données de test |

## Prérequis

- Java 21
- Node.js 22 ou plus, et pnpm
- PostgreSQL

## Installation

### 1. Base de données

```bash
psql -U postgres
```

```sql
CREATE USER soutien WITH PASSWORD 'soutien';
CREATE DATABASE cours_soutien OWNER soutien;
\q
```

Les tables sont créées automatiquement au premier démarrage du back-end.

### 2. Back-end

```bash
cd backend
./gradlew bootRun
```

L'API démarre sur http://localhost:8080. Au premier démarrage, des données de test sont insérées : 7 niveaux, 7 salles, 28 cours et 2 comptes.

Variables d'environnement facultatives (valeurs par défaut adaptées au développement) :

| Variable | Rôle |
|---|---|
| `DB_PASSWORD` | mot de passe de l'utilisateur PostgreSQL `soutien` |
| `JWT_SECRET` | clé de signature des tokens (32 caractères minimum), **à changer en production** |

### 3. Front-end

```bash
cd frontend
pnpm install
pnpm dev
```

L'application est accessible sur http://localhost:3000.

## Comptes de test

| Rôle | E-mail | Mot de passe |
|---|---|---|
| Gestionnaire | admin@soutien.fr | admin123 |
| Parent | durand@mail.fr | parent123 |

## Tests

```bash
cd backend
./gradlew test
```

Rapport : `backend/build/reports/tests/test/index.html`.

Les tests unitaires vérifient les règles métier des services : inscription (niveau, capacité, un seul cours par enfant, propriété de l'enfant), calcul du solde et filtrage des cours complets.

## API

Toutes les URL commencent par `/api`. Après connexion, le token est envoyé dans l'en-tête `Authorization: Bearer <token>`.

| Zone | Accès |
|---|---|
| `/api/auth/**` | connexion, public |
| `/api/parent/**` | rôle PARENT |
| `/api/gestion/**` | rôle GESTIONNAIRE |

Le détail des endpoints et des exemples JSON est dans [`docs/api-rest.md`](docs/api-rest.md).

## Choix de conception

- **Solde calculé, non stocké** : somme des tarifs des niveaux des enfants inscrits moins la somme des paiements. Il ne peut pas devenir incohérent.
- **Relation ManyToMany enfant ↔ cours** : le modèle permet plusieurs cours par enfant ; la règle du sujet (un seul cours) est appliquée dans le service, réglable par une constante.
- **Dernière place protégée** : verrou pessimiste sur le cours pendant l'inscription.
- **DTO** : les entités ne sont jamais exposées ; aucun mot de passe ne sort du serveur.
- **Mots de passe hachés** avec BCrypt.
- **API stateless** avec JWT ; règles d'accès par rôle dans Spring Security.
- **Erreurs centralisées** : toutes les erreurs sont renvoyées au format `{ status, message }`.

Documentation complète : [`docs/analyse.md`](docs/analyse.md), [`docs/modele-donnees.dbml`](docs/modele-donnees.dbml), [`docs/api-rest.md`](docs/api-rest.md).
