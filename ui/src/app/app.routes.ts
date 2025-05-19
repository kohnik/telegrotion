import { Routes } from '@angular/router';

// Маршруты Quiz
const quizRoutes: Routes = [
  {
    path: 'quiz-welcome',
    loadComponent: () => import('./features/quiz/quiz-welcome-page/quiz-welcome-page.component').then(m => m.QuizWelcomePageComponent)
  },
  {
    path: 'quiz-library',
    loadComponent: () => import('./features/quiz/quiz-library/quiz-library.component').then(m => m.QuizLibraryComponent)
  },
  {
    path: 'quiz-lobby',
    loadComponent: () => import('./features/quiz/quiz-lobby/quiz-lobby.component').then(m => m.QuizLobbyComponent)
  },
  // {
  //   path: 'quiz-lobby',
  //   loadComponent: () => import('./features/quiz/quiz-lobby/game-window/components/game-window-answer/game-window-answer.component').then(m => m.GameWindowAnswerComponent)
  // },
  {
    path: 'quiz-creator',
    loadComponent: () => import('./features/quiz/quiz-creator/quiz-creator').then(m => m.QuizCreator)
  },
  {
    path: 'quiz-creator/:id',
    loadComponent: () => import('./features/quiz/quiz-creator/quiz-creator').then(m => m.QuizCreator)
  },
  {
    path: 'quiz-game-window',
    loadComponent: () => import('./features/quiz/quiz-lobby/game-window/game-window.component').then(m => m.GameWindowComponent)
  },
  {
    path: 'quiz-join',
    loadComponent: () => import('./features/quiz/quiz-join/quiz-join.component').then(m => m.QuizJoinComponent)
  },
  {
    path: 'quiz-game-controller',
    loadComponent: () => import('./features/quiz/quiz-game-controller/quiz-game-controller.component').then(m => m.QuizGameControllerComponent)
  },
];

// Маршруты Brain Ring
const brainRingRoutes: Routes = [
  {
    path: 'brain-ring-welcome',
    loadComponent: () => import('./features/brain-ring/brain-ring-welcome-page/brain-ring-welcome-page.component').then(m => m.BrainRingWelcomePageComponent)
  },
  {
    path: 'brain-ring-join',
    loadComponent: () => import('./features/brain-ring/brain-ring-join/brain-ring-join.component').then(m => m.BrainRingJoinComponent)
  },
  {
    path: 'brain-ring-room-not-found',
    loadComponent: () => import('./features/brain-ring/brain-ring-room-not-found/brain-ring-room-not-found.component').then(m => m.BrainRingRoomNotFoundComponent)
  },
  {
    path: 'brain-ring-controller',
    loadComponent: () => import('./features/brain-ring/brain-ring-game-controller/brain-ring-game-controller.component').then(m => m.BrainRingGameControllerComponent)
  },
  {
    path: 'brain-ring-lobby',
    loadComponent: () => import('./features/brain-ring/brain-ring-lobby/brain-ring-lobby.component').then(m => m.BrainRingLobbyComponent)
  },
  {
    path: 'brain-ring-game-window',
    loadComponent: () => import('./features/brain-ring/brain-ring-game-window/brain-ring-game-window.component').then(m => m.BrainRingGameWindowComponent)
  }
];

// Маршруты Авторизации
const authorizationRoutes: Routes = [
  {
    path: 'authorization',
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path: 'login',
        loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'registration',
        loadComponent: () => import('./auth/registration/registration.component').then(m => m.RegistrationComponent)
      }
    ]
  }
];

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./game-list/game-list.component').then(m => m.GameListComponent)
  },
  ...quizRoutes,
  ...authorizationRoutes,
  ...brainRingRoutes
];
