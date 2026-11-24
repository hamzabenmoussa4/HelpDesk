package com.isga.backend_helpdesk.user.entity;

import com.isga.backend_helpdesk.history.entity.HistoriqueTicket;
import com.isga.backend_helpdesk.shared.enums.RoleUtilisateur;
import com.isga.backend_helpdesk.ticket.entity.Ticket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Étape modèle : représente un demandeur, un technicien ou un administrateur.
 */
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    /** Identifiant technique généré par PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de famille obligatoire, limité pour garder une donnée propre. */
    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nom;

    /** Prénom obligatoire, limité pour garder une donnée propre. */
    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String prenom;

    /** Adresse de contact obligatoire et unique dans l'application. */
    @NotBlank
    @Email
    @Size(max = 180)
    @Column(nullable = false, unique = true, length = 180)
    private String email;

    /** Rôle métier stocké comme texte lisible, et non comme ordinal numérique. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleUtilisateur role;

    /** Désactivation logique : conserve les liens historiques de l'utilisateur. */
    @Column(nullable = false)
    private boolean actif = true;

    /** Date de création définie automatiquement avant l'insertion en base. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /** Tickets dont cet utilisateur est le demandeur ; relation inverse. */
    @OneToMany(mappedBy = "demandeur", fetch = FetchType.LAZY)
    private List<Ticket> ticketsCrees = new ArrayList<>();

    /** Tickets confiés à cet utilisateur lorsqu'il a le rôle technicien. */
    @OneToMany(mappedBy = "technicienAssigne", fetch = FetchType.LAZY)
    private List<Ticket> ticketsAssignes = new ArrayList<>();

    /** Événements d'historique réalisés par cet utilisateur. */
    @OneToMany(mappedBy = "acteur", fetch = FetchType.LAZY)
    private List<HistoriqueTicket> evenementsRealises = new ArrayList<>();

    /** Constructeur requis par JPA pour reconstruire l'entité depuis la base. */
    protected Utilisateur() {
    }

    /** Initialise un utilisateur métier avant sa validation et son insertion. */
    public Utilisateur(String nom, String prenom, String email, RoleUtilisateur role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.actif = true;
    }

    /** Complète la date juste avant que PostgreSQL enregistre l'utilisateur. */
    @PrePersist
    protected void initialiserDateCreation() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public List<Ticket> getTicketsCrees() {
        return ticketsCrees;
    }

    public List<Ticket> getTicketsAssignes() {
        return ticketsAssignes;
    }

    public List<HistoriqueTicket> getEvenementsRealises() {
        return evenementsRealises;
    }
}
