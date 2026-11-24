/** Données reçues de Spring Boot pour une catégorie enregistrée. */
export interface Categorie {
  id: number;
  nom: string;
  description: string | null;
  active: boolean;
}

/** Champs envoyés à Spring Boot lors d'une création ou d'une modification. */
export interface CategorieRequest {
  nom: string;
  description: string;
  active: boolean;
}
