/** Valeurs de rôle définies dans l'énumération Java RoleUtilisateur. */
export type RoleUtilisateur = 'DEMANDEUR' | 'TECHNICIEN' | 'ADMINISTRATEUR';

/** Représentation JSON d'un utilisateur renvoyée par Spring Boot. */
export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: RoleUtilisateur;
  actif: boolean;
  dateCreation: string;
}

/** Données envoyées pour créer ou mettre à jour un utilisateur. */
export interface UtilisateurRequest {
  nom: string;
  prenom: string;
  email: string;
  role: RoleUtilisateur;
  actif: boolean;
}
