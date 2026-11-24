/** Valeurs des énumérations utilisées par le modèle Java Ticket. */
export type Priorite = 'BASSE' | 'NORMALE' | 'HAUTE' | 'CRITIQUE';
export type StatutTicket = 'NOUVEAU' | 'EN_COURS' | 'EN_ATTENTE' | 'RESOLU' | 'FERME';

/** Ticket tel qu'il est renvoyé par l'API ; ses relations sont résumées. */
export interface Ticket {
  id: number;
  titre: string;
  description: string;
  priorite: Priorite;
  statut: StatutTicket;
  dateCreation: string;
  dateMiseAJour: string;
  dateResolution: string | null;
  categorieId: number;
  categorieNom: string;
  demandeurId: number;
  demandeurNom: string;
  technicienAssigneId: number | null;
  technicienNom: string | null;
}

/** Corps JSON attendu par POST et PUT /api/tickets. */
export interface TicketRequest {
  titre: string;
  description: string;
  priorite: Priorite;
  statut: StatutTicket;
  categorieId: number;
  demandeurId: number;
  technicienAssigneId: number | null;
}

/** Données visibles d'un événement conservé dans l'historique du ticket. */
export interface HistoriqueTicket {
  id: number;
  ticketId: number;
  typeEvenement: string;
  ancienneValeur: string | null;
  nouvelleValeur: string | null;
  commentaire: string | null;
  dateEvenement: string;
  acteurId: number;
  acteurNom: string;
}

/** Champs nécessaires pour ajouter une trace à l'historique. */
export interface HistoriqueRequest {
  typeEvenement: string;
  ancienneValeur: string;
  nouvelleValeur: string;
  commentaire: string;
  acteurId: number;
}
