package com.isga.backend_helpdesk.ticket.dto;

import com.isga.backend_helpdesk.shared.enums.Priorite;
import com.isga.backend_helpdesk.shared.enums.StatutTicket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Étape API : contient les champs qu'un client peut envoyer pour un ticket. */
public record TicketRequest(
        @NotBlank(message="Le titre est obligatoire.") @Size(max=150, message="Le titre est limité à 150 caractères.") String titre,
        @NotBlank(message="La description est obligatoire.") String description,
        @NotNull(message="La priorité est obligatoire.") Priorite priorite,
        StatutTicket statut,
        @NotNull(message="La catégorie est obligatoire.") Long categorieId,
        @NotNull(message="Le demandeur est obligatoire.") Long demandeurId,
        Long technicienAssigneId
) {}
