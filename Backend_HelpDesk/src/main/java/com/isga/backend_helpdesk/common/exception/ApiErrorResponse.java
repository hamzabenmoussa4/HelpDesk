package com.isga.backend_helpdesk.common.exception;

import java.time.Instant;
import java.util.Map;

/** Format homogène envoyé par l'API lorsqu'une requête échoue. */
public record ApiErrorResponse(
        Instant horodatage,
        int statut,
        String erreur,
        String message,
        Map<String, String> erreursChamps
) {
}
