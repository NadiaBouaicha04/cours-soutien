// Exécute une action vers l'API en gérant pour nous :
// le chargement (bouton désactivé), le message de succès et le message d'erreur
export function useAction() {
  const chargement = ref(false)
  const erreur = ref('')
  const succes = ref('')

  async function executer(messageSucces: string, appel: () => Promise<unknown>): Promise<boolean> {
    chargement.value = true
    erreur.value = ''
    succes.value = ''
    try {
      await appel()
      succes.value = messageSucces
      return true
    } catch (e) {
      erreur.value = messageErreur(e)   // le message renvoyé par le back-end (409, 400...)
      return false
    } finally {
      chargement.value = false
    }
  }

  return { chargement, erreur, succes, executer }
}
