package com.isga.backend_helpdesk.history.entity;

import com.isga.backend_helpdesk.ticket.entity.Ticket;
import com.isga.backend_helpdesk.user.entity.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Étape modèle : conserve la trace d'une action réalisée sur un ticket.
 */
@Entity
@Table(name = "historique_tickets")
public class HistoriqueTicket {

    /** Identifiant technique généré par PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nature de l'action, par exemple CREATION, AFFECTATION ou CHANGEMENT_STATUT. */
    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String typeEvenement;

    /** Valeur précédente conservée lorsqu'une propriété change. */
    @Size(max = 255)
    @Column(length = 255)
    private String ancienneValeur;

    /** Nouvelle valeur conservée lorsqu'une propriété change. */
    @Size(max = 255)
    @Column(length = 255)
    private String nouvelleValeur;

    /** Détail facultatif, notamment le commentaire saisi pendant le traitement. */
    @Column(columnDefinition = "TEXT")
    private String commentaire;

    /** Date de l'événement, ajoutée automatiquement avant l'insertion. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateEvenement;

    /** Ticket auquel cet événement appartient ; cette relation est obligatoire. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /** Utilisateur qui a réalisé l'action historisée. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "acteur_id", nullable = false)
    private Utilisateur acteur;

    /** Constructeur requis par JPA pour reconstruire l'entité depuis la base. */
    protected HistoriqueTicket() {
    }

    /** Initialise un événement avant son enregistrement dans l'historique. */
    public HistoriqueTicket(String typeEvenement, String ancienneValeur,
                            String nouvelleValeur, String commentaire,
                            Ticket ticket, Utilisateur acteur) {
        this.typeEvenement = typeEvenement;
        this.ancienneValeur = ancienneValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.commentaire = commentaire;
        this.ticket = ticket;
        this.acteur = acteur;
    }

    /** Ajoute la date serveur quand l'événement est créé sans date explicite. */
    @PrePersist
    protected void initialiserDateEvenement() {
        if (dateEvenement == null) {
            dateEvenement = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(String typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public String getAncienneValeur() {
        return ancienneValeur;
    }

    public void setAncienneValeur(String ancienneValeur) {
        this.ancienneValeur = ancienneValeur;
    }

    public String getNouvelleValeur() {
        return nouvelleValeur;
    }

    public void setNouvelleValeur(String nouvelleValeur) {
        this.nouvelleValeur = nouvelleValeur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Utilisateur getActeur() {
        return acteur;
    }

    public void setActeur(Utilisateur acteur) {
        this.acteur = acteur;
    }
}
