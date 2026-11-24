import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { HealthResponse } from '../../core/models/health-response.model';
import { HealthService } from '../../core/services/health.service';
import { AuthService } from '../../core/services/auth.service';

/** Première page : vérifie la communication entre Angular et Spring Boot. */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private readonly healthService = inject(HealthService);
  private readonly authService = inject(AuthService);

  /** Session sélectionnée sur la page de connexion de démonstration. */
  protected readonly utilisateur = this.authService.session;

  /** Signals : Angular rafraîchit la vue quand ces valeurs changent. */
  protected readonly enChargement = signal(true);
  protected readonly resultat = signal<HealthResponse | null>(null);
  protected readonly erreur = signal('');

  /** Le contrôle s'exécute quand Angular crée la page. */
  ngOnInit(): void {
    this.healthService.verifierApi().subscribe({
      next: (reponse) => {
        this.resultat.set(reponse);
        this.enChargement.set(false);
      },
      error: (erreur: HttpErrorResponse) => {
        this.erreur.set(
          erreur.status === 0
            ? 'Le backend est inaccessible. Vérifie qu’il fonctionne sur le port 8081.'
            : `Le backend a répondu avec l’erreur ${erreur.status}.`,
        );
        this.enChargement.set(false);
      },
    });
  }
}
