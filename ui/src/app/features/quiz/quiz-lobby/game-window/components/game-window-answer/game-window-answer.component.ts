import { Component } from '@angular/core';

export const mock = [
  {
    playerId: 'sdfgdsfsd',
    answer: 0,
  },
  {
    playerId: 'sdfsdf2323',
    answer: 0,
  },
  {
    playerId: 'dsfwe323',
    answer: 1,
  },
  {
    playerId: 'efefefefe',
    answer: 1,
  },
  {
    playerId: 'efefefeffeefee',
    answer: 2,
  },
]
@Component({
  selector: 'app-game-window-answer',
  imports: [],
  templateUrl: './game-window-answer.component.html',
  standalone: true,
  styleUrl: './game-window-answer.component.scss'
})
export class GameWindowAnswerComponent {

}
