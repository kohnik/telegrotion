import { Routes } from '@angular/router';
import {CreateQuizComponent} from './pages/create-quiz/create-quiz.component';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/welcome-page/welcome-page.component').then(m => m.WelcomePageComponent),
  },
  {
    path: 'main',
    loadComponent: () => import('./pages/main/main.component').then(m => m.MainComponent),
  },
  {
    path: 'authorization',
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full',
      },
      {
        path: 'login',
        loadComponent: () => import('./pages/authorization/login/login.component').then(m => m.LoginComponent),
      },
      {
        path: 'registration',
        loadComponent: () => import('./pages/authorization/registration/registration.component').then(m => m.RegistrationComponent),
      },
    ]
  },
  {
    path: 'create-quiz',
    loadComponent: () => import('./pages/create-quiz/create-quiz.component').then(m => m.CreateQuizComponent),
  },
];
