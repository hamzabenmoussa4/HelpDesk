package com.isga.backend_helpdesk.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Étape API : données minimales pour ajouter une trace à un ticket. */
public record HistoriqueRequest(
        @NotBlank(message="Le type d'événement est obligatoire.") @Size(max=60, message="Le type est limité à 60 caractères.") String typeEvenement,
        @Size(max=255, message="L'ancienne valeur est limitée à 255 caractères.") String ancienneValeur,
        @Size(max=255, message="La nouvelle valeur est limitée à 255 caractères.") String nouvelleValeur,
        String commentaire,
        @NotNull(message="L'identifiant de l'acteur est obligatoire.") Long acteurId
) {}
