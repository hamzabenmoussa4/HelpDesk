package com.isga.backend_helpdesk.history.repository;

import com.isga.backend_helpdesk.history.entity.HistoriqueTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Étape persistance : lecture chronologique et contrôle d'existence des traces. */
public interface HistoriqueTicketRepository extends JpaRepository<HistoriqueTicket, Long> {
    List<HistoriqueTicket> findByTicketIdOrderByDateEvenementAsc(Long ticketId);
    boolean existsByTicketId(Long ticketId);
}
