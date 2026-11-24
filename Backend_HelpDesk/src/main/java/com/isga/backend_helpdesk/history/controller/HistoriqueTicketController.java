package com.isga.backend_helpdesk.history.controller;

import com.isga.backend_helpdesk.history.dto.*;
import com.isga.backend_helpdesk.history.service.HistoriqueTicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

/** Étape API MVC : expose la consultation et l'ajout d'événements immuables. */
@RestController
@RequestMapping("/api/tickets/{ticketId}/historique")
public class HistoriqueTicketController {
    private final HistoriqueTicketService service;
    public HistoriqueTicketController(HistoriqueTicketService service) { this.service=service; }
    @GetMapping public List<HistoriqueResponse> lister(@PathVariable Long ticketId) { return service.lister(ticketId); }
    @PostMapping public ResponseEntity<HistoriqueResponse> ajouter(@PathVariable Long ticketId,@Valid @RequestBody HistoriqueRequest d) {
        HistoriqueResponse r=service.ajouter(ticketId,d);
        return ResponseEntity.created(URI.create("/api/tickets/"+ticketId+"/historique/"+r.id())).body(r);
    }
}
