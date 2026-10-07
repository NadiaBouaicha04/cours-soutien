package fr.soutien.backend.exception;

// Levée quand l'e-mail ou le mot de passe est incorrect -> 401
public class IdentifiantsInvalidesException extends RuntimeException {

    public IdentifiantsInvalidesException() {
        super("E-mail ou mot de passe incorrect");
    }
}
