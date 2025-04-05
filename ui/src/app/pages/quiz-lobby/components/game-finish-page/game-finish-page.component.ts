import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ILeaderBoardPlayers} from '../../interfaces';

@Component({
  selector: 'app-game-finish-page',
  imports: [],
  templateUrl: './game-finish-page.component.html',
  styleUrl: './game-finish-page.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameFinishPageComponent implements OnInit {
  @Input() players: ILeaderBoardPlayers[] = [];

  public prizesPlayers: ILeaderBoardPlayers[] = [];

  ngOnInit() {
    this.prizesPlayers = this.players
      .sort((a, b) => b.points - a.points).slice(0, 3);
  }
}
