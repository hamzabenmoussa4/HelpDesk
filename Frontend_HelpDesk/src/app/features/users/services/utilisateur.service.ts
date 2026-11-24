import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { RoleUtilisateur, Utilisateur, UtilisateurRequest } from '../models/utilisateur.model';

/** Couche HTTP : regroupe les requêtes adressées à /api/utilisateurs. */
@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE_URL}/utilisateurs`;

  /** Liste les utilisateurs avec les filtres facultatifs du contrôleur Spring. */
  lister(actif?: boolean, role?: RoleUtilisateur): Observable<Utilisateur[]> {
    let params = new HttpParams();
    if (actif !== undefined) params = params.set('actif', actif);
    if (role !== undefined) params = params.set('role', role);
    return this.http.get<Utilisateur[]>(this.url, { params });
  }
  /** Envoie une création au backend. */
  creer(demande: UtilisateurRequest): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(this.url, demande);
  }
  /** Met à jour les champs d'un utilisateur identifié. */
  modifier(id: number, demande: UtilisateurRequest): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.url}/${id}`, demande);
  }
  /** Le backend interprète DELETE comme une désactivation logique. */
  desactiver(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
