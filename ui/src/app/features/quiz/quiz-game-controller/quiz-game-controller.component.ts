import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {Subject, takeUntil} from 'rxjs';
import {EWSEventQuizTypes} from '../models';
import {ICrateQuizAnswer} from '../interfaces';
import {PreGameTimerComponent} from '../pre-game-timer/pre-game-timer.component';
import {quizRingWSTopic} from '../constants';
import {QuizDataService} from '../quiz.service';
import {QuizLogoComponent} from '../quiz-logo/quiz-logo.component';
import {EWSEventBrainRingTypes} from '../../brain-ring/models';


@Component({
  selector: 'app-quiz-game-controller',
  imports: [
    FormsModule,
    PreGameTimerComponent,
    QuizLogoComponent
  ],
  templateUrl: './quiz-game-controller.component.html',
  styleUrl: './quiz-game-controller.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [QuizDataService]
})
export class QuizGameControllerComponent implements OnInit, OnDestroy {
  public joinCode: string;
  public playerId = '';
  public playerName = '';
  public roomId = '';
  public isNeedStartedTimer = false;
  public isWaiting = true;
  public answers: ICrateQuizAnswer[] = [];
  public timeLeft = 0
  private destroy$ = new Subject<void>();

  constructor(
    private wsService: WebSocketService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
  ){

    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);
        console.log(parseContent,'CONTROLLER');

        this.handleGameEvent(parseContent.eventId, parseContent)

        if (parseContent.eventId === EWSEventBrainRingTypes.CHECK_EVENT) {
          console.log(parseContent, 'parseContent')
          parseContent.currentEventId > 1 && this.handleGameEvent(parseContent.currentEventId, parseContent)
        }

        this.cdr.markForCheck()
      })

    // this.wsService.messages
    //   .pipe(takeUntil(this.destroy$))
    //   .subscribe(({ content, topic }) => {
    //     let parseContent = JSON.parse(content);
    //     if(parseContent.eventId === EWSEventQuizTypes.NEW_QUESTION) {
    //       console.log(parseContent, 'parseContentparseContentparseContentparseContent')
    //
    //       this.isWaitingGameAfterJoin = false
    //       this.answers = parseContent.answers;
    //
    //       this.timeLeft = parseContent.timeLeft
    //       let timer = setInterval(() => {
    //         if(this.timeLeft === 0 )
    //         {
    //           clearInterval(timer);
    //           return;
    //         }
    //         this.timeLeft -= 1;
    //         this.cdr.markForCheck()
    //       },1000)
    //       this.cdr.markForCheck();
    //     }
    //
    //     if(parseContent.eventId === EWSEventQuizTypes.QUESTION_ENDED) {
    //       this.isNeedStartedTimer = false
    //       this.isWaitingGameAfterJoin = true
    //       this.answers = [];
    //       this.cdr.markForCheck();
    //     }
    //
    //     if(parseContent.eventId === EWSEventQuizTypes.QUIZ_STARTED) {
    //       this.isNeedStartedTimer = true;
    //       this.cdr.markForCheck();
    //     }
    // })
  }

  private handleGameEvent(eventId: number, parseContent: any): void {
    if(eventId === EWSEventQuizTypes.NEW_QUESTION) {

      this.isWaiting = false
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

    if(eventId === EWSEventQuizTypes.ANSWER_SUBMITTED) {
      this.isWaiting = true
    }

    if(eventId === EWSEventQuizTypes.QUESTION_ENDED) {
      this.isNeedStartedTimer = false
      this.isWaiting = true
      this.answers = [];
    }

    if(eventId === EWSEventQuizTypes.QUIZ_STARTED) {
      this.isNeedStartedTimer = true;
    }

    // if(eventId === EWSEventBrainRingTypes.END_SESSION) {
    //   clearLocalStorageUserData()
    //   this.wsService.disconnect();
    //   this.router.navigate(['/brain-ring-room-not-found']);
    // }

    this.cdr.markForCheck()
  }

  ngOnInit() {
    this.roomId = this.route.snapshot.queryParams['roomId'];
    this.playerId = this.route.snapshot.queryParams['playerId'];
    this.playerName = this.route.snapshot.queryParams['playerName'];

    console.log(this.playerName,'this.playerName')
    this.startSession();
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(quizRingWSTopic + `${this.roomId}`)
      this.cdr.markForCheck();
    },2000)
  }

  submitAnswer(answerOrder: number): void {
    this.wsService.send(
      '/app/quiz/submit-answer',
      JSON.stringify({
        roomId: this.roomId,
        playerName: this.playerName,
        answer: answerOrder,
        timeSpent: 7,
      })
    )

    this.isWaiting = true
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
