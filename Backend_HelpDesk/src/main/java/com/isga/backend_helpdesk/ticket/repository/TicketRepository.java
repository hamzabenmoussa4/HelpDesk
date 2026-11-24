package com.isga.backend_helpdesk.ticket.repository;

import com.isga.backend_helpdesk.shared.enums.StatutTicket;
import com.isga.backend_helpdesk.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Étape persistance : accès aux tickets avec leurs catégories et utilisateurs. */
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderByDateCreationDesc();
    List<Ticket> findByStatutOrderByDateCreationDesc(StatutTicket statut);
}
