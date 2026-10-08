// Extrait le message d'erreur renvoyé par le back-end ({ status, message })
export function messageErreur(e: unknown): string {
  const erreur = e as { data?: { message?: string } }
  return erreur?.data?.message ?? 'Une erreur est survenue, réessayez.'
}
