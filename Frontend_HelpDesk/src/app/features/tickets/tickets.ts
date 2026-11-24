import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Categorie } from '../categories/models/categorie.model';
import { CategorieService } from '../categories/services/categorie.service';
import { Utilisateur } from '../users/models/utilisateur.model';
import { UtilisateurService } from '../users/services/utilisateur.service';
import {
  HistoriqueRequest,
  HistoriqueTicket,
  Priorite,
  StatutTicket,
  Ticket,
  TicketRequest,
} from './models/ticket.model';
import { TicketService } from './services/ticket.service';

/** Page de gestion des tickets et de leur historique de traitement. */
@Component({
  selector: 'app-tickets',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './tickets.html',
  styleUrl: './tickets.css',
})
export class Tickets implements OnInit {
  private readonly builder = inject(FormBuilder);
  private readonly service = inject(TicketService);
  private readonly categorieService = inject(CategorieService);
  private readonly utilisateurService = inject(UtilisateurService);
  protected readonly priorites: Priorite[] = ['BASSE', 'NORMALE', 'HAUTE', 'CRITIQUE'];
  protected readonly statuts: StatutTicket[] = [
    'NOUVEAU',
    'EN_COURS',
    'EN_ATTENTE',
    'RESOLU',
    'FERME',
  ];
  protected readonly formulaire = this.builder.nonNullable.group({
    titre: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', Validators.required],
    priorite: ['NORMALE' as Priorite, Validators.required],
    statut: ['NOUVEAU' as StatutTicket, Validators.required],
    categorieId: [0, Validators.required],
    demandeurId: [0, Validators.required],
    technicienAssigneId: [0],
  });
  /** Formulaire de commentaire/événement : l'acteur est l'utilisateur connecté plus tard. */
  protected readonly formulaireHistorique = this.builder.nonNullable.group({
    typeEvenement: ['COMMENTAIRE', Validators.required],
    ancienneValeur: [''],
    nouvelleValeur: [''],
    commentaire: ['', Validators.required],
    acteurId: [0, Validators.required],
  });
  protected readonly tickets = signal<Ticket[]>([]);
  protected readonly categories = signal<Categorie[]>([]);
  protected readonly demandeurs = signal<Utilisateur[]>([]);
  protected readonly techniciens = signal<Utilisateur[]>([]);
  protected readonly utilisateursActifs = signal<Utilisateur[]>([]);
  protected readonly historique = signal<HistoriqueTicket[]>([]);
  protected readonly ticketHistorique = signal<Ticket | null>(null);
  protected readonly filtreStatut = signal('TOUS');
  protected readonly chargement = signal(true);
  protected readonly enregistrement = signal(false);
  protected readonly erreur = signal('');
  protected readonly message = signal('');
  protected readonly editionId = signal<number | null>(null);

  ngOnInit(): void {
    this.chargerReferences();
    this.chargerTickets();
  }

  /** Charge les choix nécessaires aux listes déroulantes des relations du ticket. */
  private chargerReferences(): void {
    this.categorieService
      .lister(true)
      .subscribe({
        next: (v) => this.categories.set(v),
        error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
      });
    this.utilisateurService
      .lister(true, 'DEMANDEUR')
      .subscribe({
        next: (v) => this.demandeurs.set(v),
        error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
      });
    this.utilisateurService
      .lister(true, 'TECHNICIEN')
      .subscribe({
        next: (v) => this.techniciens.set(v),
        error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
      });
    this.utilisateurService
      .lister(true)
      .subscribe({
        next: (v) => this.utilisateursActifs.set(v),
        error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
      });
  }
  /** Charge la liste des tickets, avec le filtre sélectionné dans l'écran. */
  protected chargerTickets(): void {
    this.chargement.set(true);
    const filtre = this.filtreStatut();
    this.service.lister(filtre === 'TOUS' ? undefined : (filtre as StatutTicket)).subscribe({
      next: (v) => {
        this.tickets.set(v);
        this.chargement.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.erreur.set(this.messageErreur(e));
        this.chargement.set(false);
      },
    });
  }
  protected changerFiltre(event: Event): void {
    this.filtreStatut.set((event.target as HTMLSelectElement).value);
    this.chargerTickets();
  }
  /** Convertit les identifiants numériques du formulaire avant l'appel de l'API. */
  protected enregistrer(): void {
    this.message.set('');
    this.erreur.set('');
    if (
      this.formulaire.invalid ||
      this.formulaire.controls.categorieId.value === 0 ||
      this.formulaire.controls.demandeurId.value === 0
    ) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const valeurs = this.formulaire.getRawValue();
    const id = this.editionId();
    const demande: TicketRequest = {
      ...valeurs,
      technicienAssigneId: valeurs.technicienAssigneId || null,
    };
    const requete = id === null ? this.service.creer(demande) : this.service.modifier(id, demande);
    this.enregistrement.set(true);
    requete.subscribe({
      next: () => {
        this.message.set(id === null ? 'Ticket créé.' : 'Ticket modifié.');
        this.annulerEdition();
        this.chargerTickets();
        this.enregistrement.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.erreur.set(this.messageErreur(e));
        this.enregistrement.set(false);
      },
    });
  }
  /** Charge un ticket dans le formulaire de modification. */
  protected editer(ticket: Ticket): void {
    this.editionId.set(ticket.id);
    this.formulaire.setValue({
      titre: ticket.titre,
      description: ticket.description,
      priorite: ticket.priorite,
      statut: ticket.statut,
      categorieId: ticket.categorieId,
      demandeurId: ticket.demandeurId,
      technicienAssigneId: ticket.technicienAssigneId ?? 0,
    });
    this.message.set('');
    this.erreur.set('');
  }
  protected annulerEdition(): void {
    this.editionId.set(null);
    this.formulaire.reset({
      titre: '',
      description: '',
      priorite: 'NORMALE',
      statut: 'NOUVEAU',
      categorieId: 0,
      demandeurId: 0,
      technicienAssigneId: 0,
    });
  }
  /** Supprime seulement après confirmation ; le backend protège l'historique lié. */
  protected supprimer(ticket: Ticket): void {
    if (!window.confirm(`Supprimer le ticket « ${ticket.titre} » ?`)) return;
    this.service.supprimer(ticket.id).subscribe({
      next: () => {
        this.message.set('Ticket supprimé.');
        this.chargerTickets();
      },
      error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
    });
  }
  /** Ouvre le panneau d'historique et demande ses événements au backend. */
  protected ouvrirHistorique(ticket: Ticket): void {
    this.ticketHistorique.set(ticket);
    this.erreur.set('');
    this.historique.set([]);
    this.formulaireHistorique.reset({
      typeEvenement: 'COMMENTAIRE',
      ancienneValeur: '',
      nouvelleValeur: '',
      commentaire: '',
      acteurId: 0,
    });
    this.service
      .historique(ticket.id)
      .subscribe({
        next: (v) => this.historique.set(v),
        error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
      });
  }
  /** Ajoute une trace/commentaire à l'historique du ticket sélectionné. */
  protected ajouterEvenement(): void {
    const ticket = this.ticketHistorique();
    if (!ticket) return;
    if (
      this.formulaireHistorique.invalid ||
      this.formulaireHistorique.controls.acteurId.value === 0
    ) {
      this.formulaireHistorique.markAllAsTouched();
      return;
    }
    const demande: HistoriqueRequest = this.formulaireHistorique.getRawValue();
    this.service.ajouterEvenement(ticket.id, demande).subscribe({
      next: () => {
        this.message.set('Événement ajouté à l’historique.');
        this.ouvrirHistorique(ticket);
      },
      error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
    });
  }
  protected fermerHistorique(): void {
    this.ticketHistorique.set(null);
  }
  protected nomUtilisateur(id: number): string {
    const u = this.utilisateursActifs().find((v) => v.id === id);
    return u ? `${u.prenom} ${u.nom}` : `Utilisateur #${id}`;
  }
  private messageErreur(e: HttpErrorResponse): string {
    return (
      (e.error?.message as string | undefined) ??
      (e.status === 0
        ? 'Backend inaccessible sur le port 8081.'
        : `La requête a échoué (HTTP ${e.status}).`)
    );
  }
}
