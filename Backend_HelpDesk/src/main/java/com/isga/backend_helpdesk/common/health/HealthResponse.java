package com.isga.backend_helpdesk.common.health;

/** Modèle de réponse que Spring convertit en JSON pour l'endpoint de santé. */
public record HealthResponse(String status, String application) {
}
