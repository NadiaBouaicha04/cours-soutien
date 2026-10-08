// Fonctions d'affichage : transforment les données de l'API en texte lisible

// "SAMEDI" -> "Samedi"
export function formatJour(jour: string): string {
  return jour.charAt(0) + jour.slice(1).toLowerCase()
}

// "10:00:00" -> "10h00"
export function formatHeure(heure: string): string {
  return heure.slice(0, 5).replace(':', 'h')
}

// { jour: "SAMEDI", heureDebut: "10:00:00", heureFin: "12:00:00" } -> "Samedi 10h00 - 12h00"
export function formatCreneau(c: { jour: string; heureDebut: string; heureFin: string }): string {
  return `${formatJour(c.jour)} ${formatHeure(c.heureDebut)} - ${formatHeure(c.heureFin)}`
}

// 300 -> "300,00 €"
export function formatEuros(montant: number): string {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(montant)
}

// "2026-09-20" -> "20/09/2026"
export function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR')
}
