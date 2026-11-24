import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/config/api.config';
import {
  HistoriqueRequest,
  HistoriqueTicket,
  StatutTicket,
  Ticket,
  TicketRequest,
} from '../models/ticket.model';

/** Couche HTTP : garde les URLs REST hors des composants d'écran. */
@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE_URL}/tickets`;
  /** Liste tous les tickets ou filtre par statut. */
  lister(statut?: StatutTicket): Observable<Ticket[]> {
    let params = new HttpParams();
    if (statut) params = params.set('statut', statut);
    return this.http.get<Ticket[]>(this.url, { params });
  }
  /** Envoie une création du ticket. */
  creer(demande: TicketRequest): Observable<Ticket> {
    return this.http.post<Ticket>(this.url, demande);
  }
  /** Met à jour le contenu du ticket. */
  modifier(id: number, demande: TicketRequest): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.url}/${id}`, demande);
  }
  /** Demande la suppression ; le backend refuse si une trace historique existe. */
  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
  /** Lit les événements dans l'ordre chronologique. */
  historique(ticketId: number): Observable<HistoriqueTicket[]> {
    return this.http.get<HistoriqueTicket[]>(`${this.url}/${ticketId}/historique`);
  }
  /** Ajoute une trace immuable à l'historique du ticket. */
  ajouterEvenement(ticketId: number, demande: HistoriqueRequest): Observable<HistoriqueTicket> {
    return this.http.post<HistoriqueTicket>(`${this.url}/${ticketId}/historique`, demande);
  }
}
