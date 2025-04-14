import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {PreGameTimerComponent} from '../../pre-game-timer/pre-game-timer.component';
import {Subject, takeUntil} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../../../../core/services/web-socket.service';
import {DataService} from '../../../../core/services/data.service';
import {ICurrentSlide} from '../interfaces';
import {EWSEventQuizTypes} from '../../models';
import {GameWindowSlideComponent} from './components/game-window-slide/game-window-slide.component';
import {GameWindowLeaderBoardComponent} from './components/game-window-leader-board/game-window-leader-board.component';
import {GameFinishPageComponent} from './components/game-finish-page/game-finish-page.component';

@Component({
  selector: 'app-game-window',
  imports: [
    PreGameTimerComponent,
    GameWindowSlideComponent,
    GameWindowLeaderBoardComponent,
    GameFinishPageComponent
  ],
  templateUrl: './game-window.component.html',
  styleUrl: './game-window.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameWindowComponent implements OnInit, OnDestroy {
  public quizStarted = false;
  public currentSlide: ICurrentSlide | null;
  public players = []
  public isFinishedQuiz = false;

  public joinCode: string;
  private destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly wsService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private readonly dataService: DataService,
  ) {
    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);

        if (parseContent.eventId === EWSEventQuizTypes.NEW_QUESTION) {
          this.currentSlide = parseContent;
          this.cdr.markForCheck()
        }

        if(parseContent.eventId === EWSEventQuizTypes.QUESTION_ENDED) {
            this.currentSlide = null;
            this.players = parseContent.players;
            this.cdr.markForCheck()
        }

        if(parseContent.eventId === EWSEventQuizTypes.QUIZ_FINISHED) {
          this.currentSlide = null;
          this.players = parseContent.players;
          this.isFinishedQuiz = true;
          this.cdr.markForCheck()
        }
      });
  }

  ngOnInit() {
    this.joinCode = this.route.snapshot.queryParams['joinCode'];
    // this.nextQuestion()
  }

  nextQuestion(): void {
    this.wsService.send(
      '/app/nextQuestion',
      JSON.stringify(
        {
          joinCode: this.joinCode
        }
      )
    )
    this.quizStarted = true;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }

}
