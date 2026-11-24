import { Injectable, signal } from '@angular/core';
import { CompteDemo, RoleDemo, SessionUtilisateur } from '../models/session-utilisateur.model';

/** Comptes fictifs uniquement destinés à tester les trois rôles de l'interface. */
export const COMPTES_DEMO: readonly CompteDemo[] = [
  {
    nomComplet: 'Utilisateur Demandeur',
    role: 'DEMANDEUR',
    email: 'demandeur@helpdesk.local',
    motDePasse: 'Demandeur123!',
  },
  {
    nomComplet: 'Technicien Démo',
    role: 'TECHNICIEN',
    email: 'technicien@helpdesk.local',
    motDePasse: 'Technicien123!',
  },
  {
    nomComplet: 'Administrateur Démo',
    role: 'ADMINISTRATEUR',
    email: 'admin@helpdesk.local',
    motDePasse: 'Admin123!',
  },
];

/** Gère une session de démonstration locale, sans authentification serveur. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly cleSession = 'helpdesk-demo-session';

  /** Signal observé par le menu et le tableau de bord. */
  readonly session = signal<SessionUtilisateur | null>(this.lireSession());

  /** Compare les identifiants aux comptes de démonstration et ouvre une session. */
  connecter(email: string, motDePasse: string, resterConnecte = false): boolean {
    const compte = COMPTES_DEMO.find(
      (c) => c.email === email.trim().toLowerCase() && c.motDePasse === motDePasse,
    );
    if (!compte) return false;

    const session: SessionUtilisateur = {
      nomComplet: compte.nomComplet,
      email: compte.email,
      role: compte.role as RoleDemo,
    };
    const stockage = resterConnecte ? localStorage : sessionStorage;
    stockage.setItem(this.cleSession, JSON.stringify(session));
    this.session.set(session);
    return true;
  }

  /** Ferme la session temporaire du navigateur. */
  deconnecter(): void {
    sessionStorage.removeItem(this.cleSession);
    localStorage.removeItem(this.cleSession);
    this.session.set(null);
  }

  /** Recharge une session de démonstration après un rafraîchissement de page. */
  private lireSession(): SessionUtilisateur | null {
    try {
      const donnee =
        sessionStorage.getItem(this.cleSession) ?? localStorage.getItem(this.cleSession);
      return donnee ? (JSON.parse(donnee) as SessionUtilisateur) : null;
    } catch {
      return null;
    }
  }
}
