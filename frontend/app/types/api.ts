// Types TypeScript qui correspondent aux DTO du back-end

export type Role = 'PARENT' | 'GESTIONNAIRE'
export type Jour = 'LUNDI' | 'MARDI' | 'MERCREDI' | 'JEUDI' | 'VENDREDI' | 'SAMEDI' | 'DIMANCHE'
export type ModePaiement = 'CHEQUE' | 'ESPECES' | 'VIREMENT'

// ---------- Connexion ----------

// LoginResponse.java
export interface LoginResponse {
  token: string
  role: Role
  prenom: string
  nom: string
}

// Ce que le front garde sur l'utilisateur connecté
export interface UtilisateurConnecte {
  prenom: string
  nom: string
  role: Role
}

// ---------- Espace parent ----------

// NiveauResponse.java
export interface Niveau {
  id: number
  libelle: string
  ordre: number
  montant: number
}

// EnfantResponse.CoursResume
export interface CoursResume {
  id: number
  jour: Jour
  heureDebut: string
  heureFin: string
  salle: string
}

// EnfantResponse.java
export interface Enfant {
  id: number
  nom: string
  prenom: string
  dateNaissance: string
  etablissement: string | null
  niveauId: number
  niveau: string
  cours: CoursResume[]
}

// EnfantRequest.java (ce que le front envoie)
export interface EnfantRequest {
  nom: string
  prenom: string
  dateNaissance: string
  etablissement: string | null
  niveauId: number | null
}

// CoursDisponibleResponse.java
export interface CoursDisponible {
  id: number
  jour: Jour
  heureDebut: string
  heureFin: string
  salle: string
  placesRestantes: number
}

// MouvementResponse.java
export interface Mouvement {
  id: number
  date: string
  montant: number
  libelle: string | null
}

// SoldeResponse.java
export interface Solde {
  totalDu: number
  totalPaye: number
  soldeRestant: number
  mouvements: Mouvement[]
}

// ---------- Espace gestionnaire ----------

// UtilisateurResponse.java
export interface Utilisateur {
  id: number
  nom: string
  prenom: string
  email: string
  role: Role
  modePaiement: ModePaiement | null
  nombrePaiements: number | null
}

// UtilisateurRequest.java
export interface UtilisateurRequest {
  nom: string
  prenom: string
  email: string
  motDePasse: string | null   // obligatoire à la création, vide = inchangé à la modification
  role: Role
  modePaiement: ModePaiement | null
  nombrePaiements: number | null
}

// MouvementRequest.java
export interface MouvementRequest {
  date: string
  montant: number | null
  libelle: string | null
}

// SalleResponse.java / SalleRequest.java
export interface Salle {
  id: number
  nom: string
  capacite: number
}

// CoursResponse.java (vue gestionnaire, avec le taux de remplissage)
export interface CoursGestion {
  id: number
  jour: Jour
  heureDebut: string
  heureFin: string
  niveauId: number
  niveau: string
  salleId: number
  salle: string
  capacite: number
  nombreInscrits: number
}

// CoursRequest.java
export interface CoursRequest {
  jour: Jour | null
  heureDebut: string
  heureFin: string
  niveauId: number | null
  salleId: number | null
}

// CoursRecapResponse.java
export interface CoursRecap {
  id: number
  jour: Jour
  heureDebut: string
  heureFin: string
  niveau: string
  salle: string
  capacite: number
  nombreInscrits: number
  inscrits: { id: number; nom: string; prenom: string; etablissement: string | null; parent: string }[]
}

export const JOURS: Jour[] = ['LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE']
