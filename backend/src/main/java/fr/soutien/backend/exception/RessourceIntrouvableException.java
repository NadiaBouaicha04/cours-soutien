package fr.soutien.backend.exception;

// Levée quand un élément n'existe pas (ou n'appartient pas à l'utilisateur) -> 404
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
