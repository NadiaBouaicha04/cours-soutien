package fr.soutien.backend.exception;

// Levée quand une règle métier n'est pas respectée (cours complet...) -> 409
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
