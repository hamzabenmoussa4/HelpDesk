import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Categorie, CategorieRequest } from './models/categorie.model';
import { CategorieService } from './services/categorie.service';

/** Page de gestion : liste, crée, modifie et supprime les catégories. */
@Component({
  selector: 'app-categories',
  imports: [ReactiveFormsModule],
  templateUrl: './categories.html',
  styleUrl: './categories.css',
})
export class Categories implements OnInit {
  private readonly formulaireBuilder = inject(FormBuilder);
  private readonly categorieService = inject(CategorieService);

  /** Le formulaire réactif garde les champs et leurs règles de validation. */
  protected readonly formulaire = this.formulaireBuilder.nonNullable.group({
    nom: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(500)]],
    active: [true],
  });

  /** Signaux utilisés par le template pour afficher l'état courant de la page. */
  protected readonly categories = signal<Categorie[]>([]);
  protected readonly chargement = signal(true);
  protected readonly enregistrement = signal(false);
  protected readonly erreur = signal('');
  protected readonly message = signal('');
  protected readonly identifiantEnEdition = signal<number | null>(null);
  protected readonly filtre = signal<'toutes' | 'actives' | 'inactives'>('toutes');

  /** Charge la liste dès que le composant est affiché par le routeur. */
  ngOnInit(): void {
    this.chargerCategories();
  }

  /** Recharge les données en appliquant le filtre choisi par l'utilisateur. */
  protected chargerCategories(): void {
    this.chargement.set(true);
    this.erreur.set('');
    const filtre = this.filtre();
    const active = filtre === 'toutes' ? undefined : filtre === 'actives';

    this.categorieService.lister(active).subscribe({
      next: (resultat) => {
        this.categories.set(resultat);
        this.chargement.set(false);
      },
      error: (erreur: HttpErrorResponse) => {
        this.erreur.set(this.messageErreur(erreur));
        this.chargement.set(false);
      },
    });
  }

  /** Lit le filtre HTML puis recharge la liste correspondante. */
  protected changerFiltre(event: Event): void {
    const valeur = (event.target as HTMLSelectElement).value;
    this.filtre.set(valeur as 'toutes' | 'actives' | 'inactives');
    this.chargerCategories();
  }

  /** Envoie une création ou une modification selon le mode du formulaire. */
  protected enregistrer(): void {
    this.message.set('');
    this.erreur.set('');
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    const demande: CategorieRequest = this.formulaire.getRawValue();
    const id = this.identifiantEnEdition();
    const operation =
      id === null
        ? this.categorieService.creer(demande)
        : this.categorieService.modifier(id, demande);

    this.enregistrement.set(true);
    operation.subscribe({
      next: () => {
        this.message.set(id === null ? 'Catégorie créée.' : 'Catégorie modifiée.');
        this.annulerEdition();
        this.chargerCategories();
        this.enregistrement.set(false);
      },
      error: (erreur: HttpErrorResponse) => {
        this.erreur.set(this.messageErreur(erreur));
        this.enregistrement.set(false);
      },
    });
  }

  /** Charge une catégorie dans le formulaire pour autoriser sa modification. */
  protected commencerEdition(categorie: Categorie): void {
    this.identifiantEnEdition.set(categorie.id);
    this.formulaire.setValue({
      nom: categorie.nom,
      description: categorie.description ?? '',
      active: categorie.active,
    });
    this.message.set('');
    this.erreur.set('');
  }

  /** Efface le mode édition et prépare le formulaire pour une nouvelle catégorie. */
  protected annulerEdition(): void {
    this.identifiantEnEdition.set(null);
    this.formulaire.reset({ nom: '', description: '', active: true });
  }

  /** Demande confirmation avant l'effacement puis actualise la liste. */
  protected supprimer(categorie: Categorie): void {
    const confirme = window.confirm(`Supprimer la catégorie « ${categorie.nom} » ?`);
    if (!confirme) return;

    this.erreur.set('');
    this.message.set('');
    this.categorieService.supprimer(categorie.id).subscribe({
      next: () => {
        this.message.set('Catégorie supprimée.');
        this.chargerCategories();
      },
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.messageErreur(erreur)),
    });
  }

  /** Adapte la réponse d'erreur JSON commune à un message lisible dans l'écran. */
  private messageErreur(erreur: HttpErrorResponse): string {
    const messageServeur = erreur.error?.message as string | undefined;
    if (messageServeur) return messageServeur;
    if (erreur.status === 0)
      return 'Le backend est inaccessible. Vérifie qu’il fonctionne sur le port 8081.';
    return `La requête a échoué (HTTP ${erreur.status}).`;
  }
}
