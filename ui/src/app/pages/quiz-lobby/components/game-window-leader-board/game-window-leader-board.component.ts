import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ILeaderBoardPlayers} from '../../interfaces';
import {GameWindowSlideComponent} from '../game-window-slide/game-window-slide.component';

@Component({
  selector: 'app-game-window-leader-board',
  imports: [
    GameWindowSlideComponent
  ],
  templateUrl: './game-window-leader-board.component.html',
  styleUrl: './game-window-leader-board.component.scss'
})
export class GameWindowLeaderBoardComponent {
  @Input() players: ILeaderBoardPlayers[] = []
  @Output() nextQuestion = new EventEmitter<ILeaderBoardPlayers[]>()
}
