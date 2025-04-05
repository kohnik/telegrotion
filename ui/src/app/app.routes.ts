import { Routes } from '@angular/router';
import {QuizComponent} from './pages/create-quiz/quiz.component';
import {QuizLobbyComponent} from './pages/quiz-lobby/quiz-lobby.component';

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
    path: 'quiz-lobby/:id',
    loadComponent: () => import('./pages/quiz-lobby/quiz-lobby.component').then(m => m.QuizLobbyComponent),
  },
  {
    path: 'quiz',
    loadComponent: () => import('./pages/create-quiz/quiz.component').then(m => m.QuizComponent),
  },
  {
    path: 'quiz/:id',
    loadComponent: () => import('./pages/create-quiz/quiz.component').then(m => m.QuizComponent),
  },
  {
    path: 'game-window',
    loadComponent: () => import('./pages/quiz-lobby/components/game-window/game-window.component').then(m => m.GameWindowComponent),
  },
  {
    path: 'join',
    loadComponent: () => import('./pages/quiz-lobby/components/game-controller/game-controller.component').then(m => m.GameControllerComponent),
  },
];
