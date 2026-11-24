package com.isga.backend_helpdesk.ticket.entity;

import com.isga.backend_helpdesk.category.entity.Categorie;
import com.isga.backend_helpdesk.history.entity.HistoriqueTicket;
import com.isga.backend_helpdesk.shared.enums.Priorite;
import com.isga.backend_helpdesk.shared.enums.StatutTicket;
import com.isga.backend_helpdesk.user.entity.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Étape modèle : décrit l'incident, ses liens métier et son cycle de vie.
 */
@Entity
@Table(name = "tickets")
public class Ticket {

    /** Identifiant technique généré par PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Résumé court affiché dans les listes de tickets. */
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titre;

    /** Description détaillée obligatoire du problème signalé. */
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Priorité en texte pour rendre les valeurs SQL lisibles et stables. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priorite priorite = Priorite.NORMALE;

    /** État courant du traitement ; tout changement sera historisé par le service. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutTicket statut = StatutTicket.NOUVEAU;

    /** Date et heure de création, fixées automatiquement à la première sauvegarde. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /** Date et heure de la dernière modification du ticket. */
    @Column(nullable = false)
    private LocalDateTime dateMiseAJour;

    /** Date renseignée quand le ticket passe à l'état RESOLU. */
    private LocalDateTime dateResolution;

    /** Catégorie obligatoire ; la clé étrangère est stockée dans tickets. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    /** Demandeur obligatoire ayant créé le ticket. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demandeur_id", nullable = false)
    private Utilisateur demandeur;

    /** Technicien affecté ; nul tant qu'aucune affectation n'a été faite. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_assigne_id")
    private Utilisateur technicienAssigne;

    /** Événements liés au ticket ; l'historique n'est pas supprimé en cascade. */
    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<HistoriqueTicket> historique = new ArrayList<>();

    /** Constructeur requis par JPA pour reconstruire l'entité depuis la base. */
    protected Ticket() {
    }

    /** Initialise un ticket métier avec ses relations obligatoires. */
    public Ticket(String titre, String description, Priorite priorite,
                   Categorie categorie, Utilisateur demandeur) {
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.categorie = categorie;
        this.demandeur = demandeur;
        this.statut = StatutTicket.NOUVEAU;
    }

    /** Prépare les dates initiales juste avant l'insertion du ticket. */
    @PrePersist
    protected void initialiserDates() {
        LocalDateTime maintenant = LocalDateTime.now();
        if (dateCreation == null) {
            dateCreation = maintenant;
        }
        dateMiseAJour = maintenant;
    }

    /** Actualise automatiquement la date lors d'une modification JPA. */
    @PreUpdate
    protected void actualiserDateMiseAJour() {
        dateMiseAJour = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public StatutTicket getStatut() {
        return statut;
    }

    public void setStatut(StatutTicket statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateMiseAJour() {
        return dateMiseAJour;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Utilisateur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Utilisateur demandeur) {
        this.demandeur = demandeur;
    }

    public Utilisateur getTechnicienAssigne() {
        return technicienAssigne;
    }

    public void setTechnicienAssigne(Utilisateur technicienAssigne) {
        this.technicienAssigne = technicienAssigne;
    }

    public List<HistoriqueTicket> getHistorique() {
        return historique;
    }
}
