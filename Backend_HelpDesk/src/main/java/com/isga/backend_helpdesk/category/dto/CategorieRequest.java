package com.isga.backend_helpdesk.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Étape API : données qu'un client est autorisé à envoyer pour créer ou modifier
 * une catégorie. Ce DTO empêche d'exposer directement l'entité JPA.
 */
public record CategorieRequest(

        /** Nom obligatoire de la catégorie, limité à la taille prévue en base. */
        @NotBlank(message = "Le nom de la catégorie est obligatoire.")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
        String nom,

        /** Description facultative permettant de préciser la catégorie. */
        @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères.")
        String description,

        /** Valeur facultative : vraie par défaut à la création, inchangée à la mise à jour. */
        Boolean active
) {
}
