package fr.soutien.backend.exception;

// Format uniforme de toutes les erreurs renvoyées au front
public record ErreurResponse(int status, String message) {
}
