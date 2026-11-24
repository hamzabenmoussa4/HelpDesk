import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import { Categorie, CategorieRequest } from '../models/categorie.model';

/** Couche HTTP du module : traduit les opérations de l'écran en requêtes REST. */
@Injectable({ providedIn: 'root' })
export class CategorieService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE_URL}/categories`;

  /** GET /api/categories : active vaut true, false ou undefined pour tout lister. */
  lister(active?: boolean): Observable<Categorie[]> {
    let params = new HttpParams();
    if (active !== undefined) params = params.set('active', active);
    return this.http.get<Categorie[]>(this.url, { params });
  }

  /** POST /api/categories : crée une catégorie. */
  creer(demande: CategorieRequest): Observable<Categorie> {
    return this.http.post<Categorie>(this.url, demande);
  }

  /** PUT /api/categories/{id} : remplace les données modifiables. */
  modifier(id: number, demande: CategorieRequest): Observable<Categorie> {
    return this.http.put<Categorie>(`${this.url}/${id}`, demande);
  }

  /** DELETE /api/categories/{id} : demande une suppression au backend. */
  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
