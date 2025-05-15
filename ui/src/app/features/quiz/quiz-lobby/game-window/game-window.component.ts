import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {PreGameTimerComponent} from '../../../../shared/components/pre-game-timer/pre-game-timer.component';
import {Subject, takeUntil} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../../../../core/services/web-socket.service';
import {ICurrentSlide} from '../interfaces';
import {EWSEventQuizTypes} from '../../models';
import {GameWindowSlideComponent} from './components/game-window-slide/game-window-slide.component';
import {GameWindowLeaderBoardComponent} from './components/game-window-leader-board/game-window-leader-board.component';
import {GameFinishPageComponent} from './components/game-finish-page/game-finish-page.component';
import {EGameType} from '../../../../shared/interfaces';
import {GameWindowAnswerComponent} from './components/game-window-answer/game-window-answer.component';

export enum EWindowsType {
  LEADER_BORDER_WINDOW = 0,
  QUESTION_WINDOW = 1,
  ANSWER_WINDOW,
  FINISH_WINDOW
}
@Component({
  selector: 'app-game-window',
  imports: [
    PreGameTimerComponent,
    GameWindowSlideComponent,
    GameWindowLeaderBoardComponent,
    GameFinishPageComponent,
    GameWindowAnswerComponent
  ],
  templateUrl: './game-window.component.html',
  styleUrl: './game-window.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
})
export class GameWindowComponent implements OnInit, OnDestroy {
  public quizStarted = false;
  public currentSlide: ICurrentSlide | null;
  public players = []
  public isFinishedQuiz = false;

  public joinCode: string;
  public roomId: string;
  private destroy$ = new Subject<void>();

  public currentWindow = EWindowsType.QUESTION_WINDOW;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly wsService: WebSocketService,
    private cdr: ChangeDetectorRef,
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
          console.log(parseContent)
            this.currentWindow = EWindowsType.ANSWER_WINDOW
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
    this.roomId = this.route.snapshot.queryParams['roomId'];
    // this.nextQuestion()


  }

  nextQuestion(): void {
    this.wsService.send(
      '/app/quiz/next-question',
      JSON.stringify(
        {
          joinCode: this.joinCode,
          roomId: this.roomId
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

  protected readonly EGameType = EGameType;
  protected readonly EWindowsType = EWindowsType;
}
