/** Forme des données JSON renvoyées par GET /api/health. */
export interface HealthResponse {
  status: string;
  application: string;
}
