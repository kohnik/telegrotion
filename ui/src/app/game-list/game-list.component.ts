import { Component } from '@angular/core';
import {Router} from '@angular/router';

export enum EGameType {
  QUIZ = 0,
  BRAIN_RING = 1,
}
@Component({
  selector: 'app-game-list',
  imports: [],
  templateUrl: './game-list.component.html',
  styleUrl: './game-list.component.scss',
  standalone: true
})
export class GameListComponent {

  public readonly EGameType = EGameType

  constructor(private router: Router) { }

  selectGame(game: EGameType): void {
    switch (game) {
      case EGameType.QUIZ: {
        this.router.navigate(['/quiz-welcome'])
        break;
      }
      case EGameType.BRAIN_RING: {
        this.router.navigate(['/brain-ring-welcome'])
        break;
      }
    }
  }
}
