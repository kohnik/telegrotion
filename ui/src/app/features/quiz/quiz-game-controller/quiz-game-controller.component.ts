import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {Router} from '@angular/router';
import {DataService} from '../../../core/services/data.service';
import {FormsModule} from '@angular/forms';
import {Subject, takeUntil} from 'rxjs';
import {EWSEventQuizTypes} from '../models';
import {ICrateQuizAnswer} from '../interfaces';
import {PreGameTimerComponent} from '../pre-game-timer/pre-game-timer.component';
import {quizRingWSTopic} from '../constants';

@Component({
  selector: 'app-quiz-game-controller',
  imports: [
    FormsModule,
    PreGameTimerComponent
  ],
  templateUrl: './quiz-game-controller.component.html',
  styleUrl: './quiz-game-controller.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizGameControllerComponent implements OnInit, OnDestroy {
  public joinCode: string;
  public username = 'COBAKA';
  public isJoined = false;
  public isNeedStartedTimer = false;
  public isWaitingGameAfterJoin = false;
  public answers: ICrateQuizAnswer[] = [];
  public timeLeft = 0
  private destroy$ = new Subject<void>();

  constructor(
    private wsService: WebSocketService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private dataService: DataService,) {

    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);
        if(parseContent.eventId === EWSEventQuizTypes.NEW_QUESTION) {
          console.log(parseContent, 'parseContentparseContentparseContentparseContent')
          this.isWaitingGameAfterJoin = false
          this.answers = parseContent.answers;

          this.timeLeft = parseContent.timeLeft
          let timer = setInterval(() => {
            if(this.timeLeft === 0 )
            {
              clearInterval(timer);
              return;
            }
            this.timeLeft -= 1;
            this.cdr.markForCheck()
          },1000)
          this.cdr.markForCheck();
        }

        if(parseContent.eventId === EWSEventQuizTypes.QUESTION_ENDED) {
          this.isNeedStartedTimer = false
          this.isWaitingGameAfterJoin = true
          this.answers = [];
          this.cdr.markForCheck();
        }

        if(parseContent.eventId === EWSEventQuizTypes.QUIZ_STARTED) {
          this.isNeedStartedTimer = true;
          this.cdr.markForCheck();
        }
    })
  }

  ngOnInit() {

  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(quizRingWSTopic + `${this.joinCode}`)
      this.cdr.markForCheck();
    },1000)
  }

  public goToLobby(): void {
    this.dataService.gotToLobby(this.joinCode, this.username).subscribe(el =>
      {
        this.isJoined = true;
        this.isWaitingGameAfterJoin = true;
        this.cdr.markForCheck();
        this.startSession()
      }
    )
  }

  submitAnswer(answerOrder: number): void {
    this.wsService.send(
      '/app/submit-answer',
      JSON.stringify({
        joinCode: this.joinCode,
        username: this.username,
        answer: answerOrder,
        timeSpent: 7,
      })
    )

    this.isWaitingGameAfterJoin = true
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
