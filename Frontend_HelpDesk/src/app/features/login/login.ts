import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService, COMPTES_DEMO } from '../../core/services/auth.service';
import { CompteDemo } from '../../core/models/session-utilisateur.model';

/** Écran de connexion visuel ; l'authentification serveur n'est pas encore configurée. */
@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly builder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly comptesDemo = COMPTES_DEMO;
  /** Masque les identifiants tant que l'utilisateur ne les demande pas. */
  protected readonly accesDemoVisibles = signal(false);

  /** Formulaire réactif : Angular conserve les valeurs et valide les champs. */
  protected readonly formulaire = this.builder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    motDePasse: ['', [Validators.required, Validators.minLength(8)]],
    resterConnecte: [false],
  });

  /** Contrôle l'affichage du mot de passe sans modifier sa valeur. */
  protected readonly afficherMotDePasse = signal(false);
  /** Explique clairement que le backend d'authentification reste à développer. */
  protected readonly message = signal('');

  /** Bascule l'affichage du champ sans modifier la valeur saisie. */
  protected basculerVisibiliteMotDePasse(): void {
    this.afficherMotDePasse.update((visible) => !visible);
  }

  /** Affiche ou replie la liste des trois accès de démonstration. */
  protected basculerAccesDemo(): void {
    this.accesDemoVisibles.update((visible) => !visible);
  }

  /** Vérifie le compte démo et ouvre le tableau de bord si les identifiants sont corrects. */
  protected seConnecter(): void {
    this.message.set('');
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    const { email, motDePasse, resterConnecte } = this.formulaire.getRawValue();
    if (!this.auth.connecter(email, motDePasse, resterConnecte)) {
      this.message.set(
        'Adresse e-mail ou mot de passe incorrect. Utilise un des comptes de démonstration ci-dessous.',
      );
      this.formulaire.controls.motDePasse.reset('');
      return;
    }
    this.formulaire.controls.motDePasse.reset('');
    void this.router.navigateByUrl('/dashboard');
  }

  /** Remplit le formulaire avec le compte choisi dans la liste de démonstration. */
  protected utiliserCompte(compte: CompteDemo): void {
    this.formulaire.patchValue({ email: compte.email, motDePasse: compte.motDePasse });
    this.message.set('');
  }
}
