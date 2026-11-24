import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { AuthService } from './core/services/auth.service';

/** Composant racine : cadre commun et emplacement de la page active. */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  /** Le routeur donne accès à l'URL courante sans stocker une seconde copie. */
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  protected readonly session = this.auth.session;

  /** Ferme la session avant de renvoyer l'utilisateur à l'écran login. */
  protected deconnecter(): void {
    this.auth.deconnecter();
    void this.router.navigateByUrl('/login');
  }

  /** Signal de l'URL active, mis à jour après chaque navigation Angular. */
  private readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  /** Autorise l'écran de connexion à masquer la navigation principale. */
  protected readonly estPageLogin = computed(() => this.currentUrl().split('?')[0] === '/login');

  /** Calcule le titre à afficher à partir du chemin courant. */
  protected readonly pageTitle = computed(() => this.titrePour(this.currentUrl()));

  /** Convertit le chemin courant en titre affichable dans la barre supérieure. */
  private titrePour(url: string): string {
    const chemin = url.split('?')[0];
    if (chemin.startsWith('/tickets')) return 'Tickets';
    if (chemin.startsWith('/categories')) return 'Catégories';
    if (chemin.startsWith('/utilisateurs')) return 'Utilisateurs';
    if (chemin.startsWith('/login')) return 'Connexion';
    return 'Tableau de bord';
  }
}
