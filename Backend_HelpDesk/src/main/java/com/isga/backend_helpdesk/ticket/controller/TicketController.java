package com.isga.backend_helpdesk.ticket.controller;

import com.isga.backend_helpdesk.shared.enums.StatutTicket;
import com.isga.backend_helpdesk.ticket.dto.*;
import com.isga.backend_helpdesk.ticket.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

/** Étape API MVC : expose la gestion des tickets en HTTP. */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService service;
    public TicketController(TicketService service) { this.service=service; }
    @GetMapping public List<TicketResponse> lister(@RequestParam(required=false) StatutTicket statut) { return service.lister(statut); }
    @GetMapping("/{id}") public TicketResponse obtenir(@PathVariable Long id) { return service.obtenir(id); }
    @PostMapping public ResponseEntity<TicketResponse> creer(@Valid @RequestBody TicketRequest d) { TicketResponse r=service.creer(d); return ResponseEntity.created(URI.create("/api/tickets/"+r.id())).body(r); }
    @PutMapping("/{id}") public TicketResponse modifier(@PathVariable Long id,@Valid @RequestBody TicketRequest d) { return service.modifier(id,d); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> supprimer(@PathVariable Long id) { service.supprimer(id); return ResponseEntity.noContent().build(); }
}
