package com.isga.backend_helpdesk.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Étape API : transforme les erreurs Java en réponses HTTP JSON cohérentes.
 * Cette classe évite de répéter les mêmes try/catch dans chaque contrôleur.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Répond 404 lorsqu'un identifiant demandé n'existe pas. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> traiterRessourceIntrouvable(
            ResourceNotFoundException exception) {
        return construireReponse(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    /** Répond 409 si le service détecte un nom ou une ressource déjà existante. */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> traiterDoublon(DuplicateResourceException exception) {
        return construireReponse(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    /** Renvoie les erreurs de validation champ par champ avec le statut 400. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> traiterValidation(
            MethodArgumentNotValidException exception) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        for (FieldError erreur : exception.getBindingResult().getFieldErrors()) {
            erreurs.putIfAbsent(erreur.getField(), erreur.getDefaultMessage());
        }
        return construireReponse(HttpStatus.BAD_REQUEST,
                "Les données envoyées ne respectent pas les règles de validation.", erreurs);
    }

    /** Répond 400 quand le JSON reçu est mal formé ou contient une valeur inconnue. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> traiterJsonIllisible(
            HttpMessageNotReadableException exception) {
        return construireReponse(HttpStatus.BAD_REQUEST,
                "Le corps JSON est absent, mal formé ou contient une valeur invalide.", Map.of());
    }

    /** Répond 409 sans exposer les détails SQL si une contrainte relationnelle bloque l'action. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> traiterContrainteRelationnelle(
            DataIntegrityViolationException exception) {
        return construireReponse(HttpStatus.CONFLICT,
                "L'opération est impossible car cette donnée est utilisée ou viole une contrainte.",
                Map.of());
    }

    /** Répond 400 aux règles métier dont la donnée fournie ne respecte pas le domaine. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> traiterRegleMetierInvalide(IllegalArgumentException exception) {
        return construireReponse(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    /** Répond 409 lorsqu'une opération est bloquée par l'état actuel de la ressource. */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> traiterOperationInterdite(IllegalStateException exception) {
        return construireReponse(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    /** Construit la forme commune d'erreur avec l'heure et le statut HTTP. */
    private ResponseEntity<ApiErrorResponse> construireReponse(
            HttpStatus statut, String message, Map<String, String> erreursChamps) {
        ApiErrorResponse corps = new ApiErrorResponse(
                Instant.now(), statut.value(), statut.getReasonPhrase(), message, erreursChamps);
        return ResponseEntity.status(statut).body(corps);
    }
}
