import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Le composant est chargé à la demande lorsque le chemin est sélectionné.
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  // Ces vues temporaires seront remplacées au fur et à mesure de l'apprentissage.
  {
    path: 'tickets',
    canActivate: [authGuard],
    loadComponent: () => import('./features/tickets/tickets').then((m) => m.Tickets),
  },
  {
    path: 'categories',
    canActivate: [authGuard],
    loadComponent: () => import('./features/categories/categories').then((m) => m.Categories),
  },
  {
    path: 'utilisateurs',
    canActivate: [authGuard],
    loadComponent: () => import('./features/users/users').then((m) => m.Users),
  },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' },
];
