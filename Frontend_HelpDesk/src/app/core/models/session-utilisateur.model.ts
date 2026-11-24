/** Rôle du modèle Utilisateur défini côté backend. */
export type RoleDemo = 'DEMANDEUR' | 'TECHNICIEN' | 'ADMINISTRATEUR';

/** Identité minimale conservée pour la session de démonstration Angular. */
export interface SessionUtilisateur {
  nomComplet: string;
  email: string;
  role: RoleDemo;
}

/** Compte de démonstration visible sur la page login. */
export interface CompteDemo extends SessionUtilisateur {
  motDePasse: string;
}
