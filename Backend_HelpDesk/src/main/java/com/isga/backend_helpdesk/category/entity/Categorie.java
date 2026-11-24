package com.isga.backend_helpdesk.category.entity;

import com.isga.backend_helpdesk.ticket.entity.Ticket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Étape modèle : représente une catégorie d'incident et sa table PostgreSQL.
 */
@Entity
@Table(name = "categories")
public class Categorie {

    /** Identifiant technique généré par PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom obligatoire et unique de la catégorie. */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    /** Description facultative utilisée pour préciser le type d'incident. */
    @Size(max = 500)
    @Column(length = 500)
    private String description;

    /** Indique si cette catégorie peut encore être choisie pour un ticket. */
    @Column(nullable = false)
    private boolean active = true;

    /** Relation inverse : la clé étrangère est portée par Ticket. */
    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    /** Constructeur requis par JPA pour reconstruire l'entité depuis la base. */
    protected Categorie() {
    }

    /** Constructeur pratique pour créer une catégorie depuis le service métier. */
    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.active = true;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
