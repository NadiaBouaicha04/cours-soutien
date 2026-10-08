import type { Role } from '~/types/api'

// Page d'accueil de chaque rôle
export function accueilDuRole(role?: Role | null): string {
  return role === 'GESTIONNAIRE' ? '/gestion' : '/parent'
}
