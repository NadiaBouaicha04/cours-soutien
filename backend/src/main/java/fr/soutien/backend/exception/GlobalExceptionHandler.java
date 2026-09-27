package fr.soutien.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// Intercepte les exceptions de toute l'application et les transforme en réponses JSON
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurResponse> introuvable(RessourceIntrouvableException ex) {
        return reponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ErreurResponse> regleMetier(RegleMetierException ex) {
        return reponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Violation d'une contrainte de la base (unique, clé étrangère...)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErreurResponse> integrite(DataIntegrityViolationException ex) {
        return reponse(HttpStatus.CONFLICT, "Cette donnée existe déjà ou est encore utilisée");
    }

    // Échec des annotations de validation (@NotBlank, @NotNull...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<ErreurResponse> reponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErreurResponse(status.value(), message));
    }
}
