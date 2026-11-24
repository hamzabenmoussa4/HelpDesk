import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RoleUtilisateur, Utilisateur, UtilisateurRequest } from './models/utilisateur.model';
import { UtilisateurService } from './services/utilisateur.service';

/** Page utilisateur : gestion des comptes métier et de leur état actif. */
@Component({
  selector: 'app-users',
  imports: [ReactiveFormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  private readonly builder = inject(FormBuilder);
  private readonly service = inject(UtilisateurService);
  protected readonly roles: RoleUtilisateur[] = ['DEMANDEUR', 'TECHNICIEN', 'ADMINISTRATEUR'];
  protected readonly formulaire = this.builder.nonNullable.group({
    nom: ['', [Validators.required, Validators.maxLength(80)]],
    prenom: ['', [Validators.required, Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(180)]],
    role: ['DEMANDEUR' as RoleUtilisateur, Validators.required],
    actif: [true],
  });
  protected readonly utilisateurs = signal<Utilisateur[]>([]);
  protected readonly chargement = signal(true);
  protected readonly enregistrement = signal(false);
  protected readonly erreur = signal('');
  protected readonly message = signal('');
  protected readonly editionId = signal<number | null>(null);
  protected readonly filtreRole = signal('TOUS');
  protected readonly filtreEtat = signal('actifs');

  ngOnInit(): void {
    this.charger();
  }

  /** Recharge les utilisateurs selon le rôle et l'état choisis. */
  protected charger(): void {
    this.chargement.set(true);
    this.erreur.set('');
    const role = this.filtreRole() === 'TOUS' ? undefined : (this.filtreRole() as RoleUtilisateur);
    const actif = this.filtreEtat() === 'tous' ? undefined : this.filtreEtat() === 'actifs';
    this.service.lister(actif, role).subscribe({
      next: (lignes) => {
        this.utilisateurs.set(lignes);
        this.chargement.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.erreur.set(this.messageErreur(e));
        this.chargement.set(false);
      },
    });
  }
  protected changerFiltre(event: Event, type: 'role' | 'etat'): void {
    const valeur = (event.target as HTMLSelectElement).value;
    if (type === 'role') this.filtreRole.set(valeur);
    else this.filtreEtat.set(valeur);
    this.charger();
  }
  /** Envoie POST en création ou PUT en modification, après validation du formulaire. */
  protected enregistrer(): void {
    this.message.set('');
    this.erreur.set('');
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const demande: UtilisateurRequest = this.formulaire.getRawValue();
    const id = this.editionId();
    const operation =
      id === null ? this.service.creer(demande) : this.service.modifier(id, demande);
    this.enregistrement.set(true);
    operation.subscribe({
      next: () => {
        this.message.set(id === null ? 'Utilisateur créé.' : 'Utilisateur modifié.');
        this.annuler();
        this.charger();
        this.enregistrement.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.erreur.set(this.messageErreur(e));
        this.enregistrement.set(false);
      },
    });
  }
  /** Préremplit le formulaire avec les données renvoyées par l'API. */
  protected editer(u: Utilisateur): void {
    this.editionId.set(u.id);
    this.formulaire.setValue({
      nom: u.nom,
      prenom: u.prenom,
      email: u.email,
      role: u.role,
      actif: u.actif,
    });
    this.message.set('');
    this.erreur.set('');
  }
  /** Réinitialise les champs et revient au mode création. */
  protected annuler(): void {
    this.editionId.set(null);
    this.formulaire.reset({ nom: '', prenom: '', email: '', role: 'DEMANDEUR', actif: true });
  }
  /** Désactive le compte sans effacer ses références dans les tickets historiques. */
  protected desactiver(u: Utilisateur): void {
    if (!window.confirm(`Désactiver le compte de ${u.prenom} ${u.nom} ?`)) return;
    this.service.desactiver(u.id).subscribe({
      next: () => {
        this.message.set('Utilisateur désactivé.');
        this.charger();
      },
      error: (e: HttpErrorResponse) => this.erreur.set(this.messageErreur(e)),
    });
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
