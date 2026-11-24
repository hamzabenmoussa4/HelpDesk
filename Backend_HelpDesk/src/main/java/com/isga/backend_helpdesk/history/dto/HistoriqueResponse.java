package com.isga.backend_helpdesk.history.dto;

import java.time.LocalDateTime;

/** Étape API : expose la trace avec les identifiants et noms utiles à l'interface. */
public record HistoriqueResponse(Long id, Long ticketId, String typeEvenement,
        String ancienneValeur, String nouvelleValeur, String commentaire,
        LocalDateTime dateEvenement, Long acteurId, String acteurNom) {}
