package com.isga.backend_helpdesk.ticket.service;

import com.isga.backend_helpdesk.category.entity.Categorie;
import com.isga.backend_helpdesk.category.repository.CategorieRepository;
import com.isga.backend_helpdesk.common.exception.ResourceNotFoundException;
import com.isga.backend_helpdesk.history.repository.HistoriqueTicketRepository;
import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import com.isga.backend_helpdesk.shared.enums.StatutTicket;
import com.isga.backend_helpdesk.ticket.dto.*;
import com.isga.backend_helpdesk.ticket.entity.Ticket;
import com.isga.backend_helpdesk.ticket.repository.TicketRepository;
import com.isga.backend_helpdesk.user.entity.Utilisateur;
import com.isga.backend_helpdesk.user.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/** Étape métier : vérifie les liens et applique les règles principales du ticket. */
@Service
@Transactional(readOnly=true)
public class TicketService {
    private final TicketRepository tickets; private final CategorieRepository categories;
    private final UtilisateurRepository utilisateurs; private final HistoriqueTicketRepository historiques;
    public TicketService(TicketRepository tickets, CategorieRepository categories, UtilisateurRepository utilisateurs, HistoriqueTicketRepository historiques) {
        this.tickets=tickets; this.categories=categories; this.utilisateurs=utilisateurs; this.historiques=historiques;
    }
    public List<TicketResponse> lister(StatutTicket statut) { return (statut == null ? tickets.findAllByOrderByDateCreationDesc() : tickets.findByStatutOrderByDateCreationDesc(statut)).stream().map(this::reponse).toList(); }
    public TicketResponse obtenir(Long id) { return reponse(trouver(id)); }

    /** Crée un ticket avec son demandeur et sa catégorie obligatoires. */
    @Transactional
    public TicketResponse creer(TicketRequest d) {
        Categorie categorie=categories.findById(d.categorieId()).orElseThrow(()->new ResourceNotFoundException("Catégorie introuvable."));
        Utilisateur demandeur=utilisateurActif(d.demandeurId());
        Ticket t=new Ticket(d.titre().trim(), d.description().trim(), d.priorite(), categorie, demandeur);
        appliquer(t,d,true); t.setStatut(d.statut()==null?StatutTicket.NOUVEAU:d.statut());
        if(t.getStatut()==StatutTicket.RESOLU) t.setDateResolution(LocalDateTime.now());
        return reponse(tickets.save(t));
    }
    /** Modifie un ticket et met à jour la date de résolution selon son statut. */
    @Transactional
    public TicketResponse modifier(Long id, TicketRequest d) {
        Ticket t=trouver(id); appliquer(t,d,false);
        if(d.statut()!=null) t.setStatut(d.statut());
        if(t.getStatut()==StatutTicket.RESOLU && t.getDateResolution()==null) t.setDateResolution(LocalDateTime.now());
        if(t.getStatut()!=StatutTicket.RESOLU) t.setDateResolution(null);
        return reponse(tickets.save(t));
    }
    /** Efface un ticket seulement s'il n'a pas encore d'événement historique. */
    @Transactional
    public void supprimer(Long id) {
        Ticket t=trouver(id);
        if(historiques.existsByTicketId(id)) throw new IllegalStateException("Impossible de supprimer un ticket qui possède un historique.");
        tickets.delete(t);
    }
    private void appliquer(Ticket t, TicketRequest d, boolean creation) {
        Categorie c=categories.findById(d.categorieId()).orElseThrow(()->new ResourceNotFoundException("Catégorie introuvable."));
        if(!c.isActive()) throw new IllegalArgumentException("La catégorie sélectionnée est inactive.");
        Utilisateur demandeur=utilisateurActif(d.demandeurId());
        if(demandeur.getRole()!=RoleUtilisateur.DEMANDEUR && demandeur.getRole()!=RoleUtilisateur.ADMINISTRATEUR) throw new IllegalArgumentException("Le demandeur doit avoir le rôle DEMANDEUR ou ADMINISTRATEUR.");
        t.setTitre(d.titre().trim()); t.setDescription(d.description().trim()); t.setPriorite(d.priorite()); t.setCategorie(c); t.setDemandeur(demandeur);
        if(d.technicienAssigneId()==null) t.setTechnicienAssigne(null);
        else { Utilisateur tech=utilisateurActif(d.technicienAssigneId()); if(tech.getRole()!=RoleUtilisateur.TECHNICIEN) throw new IllegalArgumentException("L'utilisateur affecté doit avoir le rôle TECHNICIEN."); t.setTechnicienAssigne(tech); }
        if(creation && d.statut()!=null) t.setStatut(d.statut());
    }
    private Utilisateur utilisateurActif(Long id) { Utilisateur u=utilisateurs.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilisateur introuvable.")); if(!u.isActif()) throw new IllegalArgumentException("Un utilisateur inactif ne peut pas être lié à un ticket."); return u; }
    private Ticket trouver(Long id) { return tickets.findById(id).orElseThrow(()->new ResourceNotFoundException("Aucun ticket ne correspond à l'identifiant "+id+".")); }
    private TicketResponse reponse(Ticket t) {
        return new TicketResponse(t.getId(),t.getTitre(),t.getDescription(),t.getPriorite(),t.getStatut(),t.getDateCreation(),t.getDateMiseAJour(),t.getDateResolution(),t.getCategorie().getId(),t.getCategorie().getNom(),t.getDemandeur().getId(),t.getDemandeur().getPrenom()+" "+t.getDemandeur().getNom(),t.getTechnicienAssigne()==null?null:t.getTechnicienAssigne().getId(),t.getTechnicienAssigne()==null?null:t.getTechnicienAssigne().getPrenom()+" "+t.getTechnicienAssigne().getNom());
    }
}
