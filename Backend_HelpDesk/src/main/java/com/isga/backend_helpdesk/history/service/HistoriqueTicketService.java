package com.isga.backend_helpdesk.history.service;

import com.isga.backend_helpdesk.common.exception.ResourceNotFoundException;
import com.isga.backend_helpdesk.history.dto.*;
import com.isga.backend_helpdesk.history.entity.HistoriqueTicket;
import com.isga.backend_helpdesk.history.repository.HistoriqueTicketRepository;
import com.isga.backend_helpdesk.ticket.entity.Ticket;
import com.isga.backend_helpdesk.ticket.repository.TicketRepository;
import com.isga.backend_helpdesk.user.entity.Utilisateur;
import com.isga.backend_helpdesk.user.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Étape métier : permet de consulter et d'ajouter des traces sans les réécrire. */
@Service
@Transactional(readOnly=true)
public class HistoriqueTicketService {
    private final HistoriqueTicketRepository historiques; private final TicketRepository tickets; private final UtilisateurRepository utilisateurs;
    public HistoriqueTicketService(HistoriqueTicketRepository historiques, TicketRepository tickets, UtilisateurRepository utilisateurs) { this.historiques=historiques; this.tickets=tickets; this.utilisateurs=utilisateurs; }
    public List<HistoriqueResponse> lister(Long ticketId) {
        if(!tickets.existsById(ticketId)) throw new ResourceNotFoundException("Ticket introuvable.");
        return historiques.findByTicketIdOrderByDateEvenementAsc(ticketId).stream().map(this::reponse).toList();
    }
    /** Ajoute une nouvelle trace ; les dates sont fixées par le serveur. */
    @Transactional
    public HistoriqueResponse ajouter(Long ticketId, HistoriqueRequest d) {
        Ticket ticket=tickets.findById(ticketId).orElseThrow(()->new ResourceNotFoundException("Ticket introuvable."));
        Utilisateur acteur=utilisateurs.findById(d.acteurId()).orElseThrow(()->new ResourceNotFoundException("Acteur introuvable."));
        if(!acteur.isActif()) throw new IllegalArgumentException("Un utilisateur inactif ne peut pas ajouter un événement.");
        HistoriqueTicket h=new HistoriqueTicket(d.typeEvenement().trim(),d.ancienneValeur(),d.nouvelleValeur(),d.commentaire(),ticket,acteur);
        return reponse(historiques.save(h));
    }
    private HistoriqueResponse reponse(HistoriqueTicket h) { return new HistoriqueResponse(h.getId(),h.getTicket().getId(),h.getTypeEvenement(),h.getAncienneValeur(),h.getNouvelleValeur(),h.getCommentaire(),h.getDateEvenement(),h.getActeur().getId(),h.getActeur().getPrenom()+" "+h.getActeur().getNom()); }
}
