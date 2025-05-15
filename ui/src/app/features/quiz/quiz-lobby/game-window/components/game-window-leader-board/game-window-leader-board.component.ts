import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy, OnInit,
  Output
} from '@angular/core';
import {ILeaderBoardPlayers} from '../../../interfaces';
import {GameWindowSlideComponent} from '../game-window-slide/game-window-slide.component';

@Component({
  selector: 'app-game-window-leader-board',
  imports: [
    GameWindowSlideComponent
  ],
  templateUrl: './game-window-leader-board.component.html',
  styleUrl: './game-window-leader-board.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameWindowLeaderBoardComponent implements AfterViewInit, OnInit {
  @Input() players: ILeaderBoardPlayers[] = []
  @Output() nextQuestion = new EventEmitter<ILeaderBoardPlayers[]>()

  public readyToFill = false;
  public colors: string[] = [
    '#30316b',
    '#5A4FCF',
    '#6A5ACD',
    '#483D8B',
    '#7B68EE',
    '#4B0082',
    '#9370DB',
    '#8A2BE2',
    '#B0C4DE',
  ];

  constructor(public cdr: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.players = this.players.sort((a, b)=> {
      return a.points > b.points ? -1 : 1;
    })
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.readyToFill = true;
      this.cdr.markForCheck()
    },500);
  }

  fillWidth(player: ILeaderBoardPlayers): string {
    console.log(player)
    if(player.points === 0) {
      return '80px';
    }

    const maxPoints = Math.max(...this.players.map(p => p.points));
    if (!maxPoints || maxPoints === 0) {
      return '80px';
    }

    const percentage = (player.points / maxPoints) * 100;
    return `${percentage}%`;
  }

  getRandomColor(): string {
    const index = Math.floor(Math.random() * this.colors.length);
    return this.colors[index];
  }
}
