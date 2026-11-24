package com.isga.backend_helpdesk.ticket.dto;

import com.isga.backend_helpdesk.shared.enums.Priorite;
import com.isga.backend_helpdesk.shared.enums.StatutTicket;
import java.time.LocalDateTime;

/** Étape API : résume le ticket et expose ses relations sous forme d'identifiants. */
public record TicketResponse(Long id, String titre, String description, Priorite priorite,
        StatutTicket statut, LocalDateTime dateCreation, LocalDateTime dateMiseAJour,
        LocalDateTime dateResolution, Long categorieId, String categorieNom,
        Long demandeurId, String demandeurNom, Long technicienAssigneId, String technicienNom) {}
