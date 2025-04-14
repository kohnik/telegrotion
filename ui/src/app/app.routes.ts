import { Routes } from '@angular/router';
import {QuizCreator} from './features/quiz/quiz-creator/quiz-creator';
import {QuizLobbyComponent} from './features/quiz/quiz-lobby/quiz-lobby.component';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/quiz/quiz-welcome-page/quiz-welcome-page.component').then(m => m.QuizWelcomePageComponent),
  },
  {
    path: 'main',
    loadComponent: () => import('./features/quiz/quiz-library/quiz-library.component').then(m => m.QuizLibraryComponent),
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
        loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent),
      },
      {
        path: 'registration',
        loadComponent: () => import('./auth/registration/registration.component').then(m => m.RegistrationComponent),
      },
    ]
  },
  {
    path: 'quiz-lobby/:id',
    loadComponent: () => import('./features/quiz/quiz-lobby/quiz-lobby.component').then(m => m.QuizLobbyComponent),
  },
  {
    path: 'quiz',
    loadComponent: () => import('./features/quiz/quiz-creator/quiz-creator').then(m => m.QuizCreator),
  },
  {
    path: 'quiz/:id',
    loadComponent: () => import('./features/quiz/quiz-creator/quiz-creator').then(m => m.QuizCreator),
  },
  {
    path: 'game-window',
    loadComponent: () => import('./features/quiz/quiz-lobby/game-window/game-window.component').then(m => m.GameWindowComponent),
  },
  {
    path: 'join',
    loadComponent: () => import('./features/quiz/quiz-game-controller/quiz-game-controller.component').then(m => m.QuizGameControllerComponent),
  },
  // brainRing
  {
    path: 'brain-ring-welcome',
    loadComponent: () => import('./features/brain-ring/brain-ring-welcome-page/brain-ring-welcome-page.component').then(m => m.BrainRingWelcomePageComponent),
  },
  {
    path: 'join-to-brain-ring',
    loadComponent: () => import('./features/brain-ring/brain-ring-game-controller/brain-ring-game-controller.component').then(m => m.BrainRingGameControllerComponent),
  },
  {
    path: 'brain-ring-lobby',
    loadComponent: () => import('./features/brain-ring/brain-ring-lobby/brain-ring-lobby.component').then(m => m.BrainRingLobbyComponent),
  },
  {
    path: 'brain-ring-game-window',
    loadComponent: () => import('./features/brain-ring/brain-ring-game-window/brain-ring-game-window.component').then(m => m.BrainRingGameWindowComponent),
  },
];
