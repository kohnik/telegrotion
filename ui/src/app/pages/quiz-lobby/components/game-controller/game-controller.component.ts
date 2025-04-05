import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketService} from '../../../../../services/web-socket.service';
import {Router} from '@angular/router';
import {DataService} from '../../../../../services/data.service';
import {FormsModule} from '@angular/forms';
import {Subject, takeUntil} from 'rxjs';
import {EWSEventTypes} from '../../../models';
import {ICrateQuizAnswer} from '../../../create-quiz/interfaces';
import {PreGameTimerComponent} from '../pre-game-timer/pre-game-timer.component';

@Component({
  selector: 'app-game-controller',
  imports: [
    FormsModule,
    PreGameTimerComponent
  ],
  templateUrl: './game-controller.component.html',
  styleUrl: './game-controller.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameControllerComponent implements OnInit, OnDestroy {
  public joinCode: string;
  public username = 'COBAKA';
  public isJoined = false;
  public isNeedStartedTimer = false;
  public isWaitingGameAfterJoin = false;
  public answers: ICrateQuizAnswer[] = [];
  public timeLeft = 0
  newTopic = '/topic/session/';
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
        if(parseContent.eventId === EWSEventTypes.NEW_QUESTION) {
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

        if(parseContent.eventId === EWSEventTypes.QUESTION_ENDED) {
          this.isNeedStartedTimer = false
          this.isWaitingGameAfterJoin = true
          this.answers = [];
          this.cdr.markForCheck();
        }

        if(parseContent.eventId === EWSEventTypes.QUIZ_STARTED) {
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
      this.wsService.subscribe(this.newTopic + `${this.joinCode}`)
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
