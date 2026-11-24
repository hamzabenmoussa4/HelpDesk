import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { HealthResponse } from '../models/health-response.model';

/** Service chargé des appels HTTP liés à l'état du backend. */
@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly http = inject(HttpClient);

  /** Observable transporte la réponse asynchrone de Spring Boot vers le composant. */
  verifierApi(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(`${API_BASE_URL}/health`);
  }
}
