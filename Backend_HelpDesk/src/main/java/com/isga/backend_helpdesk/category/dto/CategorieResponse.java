package com.isga.backend_helpdesk.category.dto;

/**
 * Étape API : représentation publique d'une catégorie renvoyée en JSON.
 */
public record CategorieResponse(
        Long id,
        String nom,
        String description,
        boolean active
) {
}
