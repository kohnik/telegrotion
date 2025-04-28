import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {PreGameTimerComponent} from '../../quiz/pre-game-timer/pre-game-timer.component';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ICrateQuizAnswer} from '../../quiz/interfaces';
import {Subject, takeUntil} from 'rxjs';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {brainRingWSTopic, privateUserBrainRingWSTopic} from '../constants';
import {BrainRingService} from '../brain-ring.service';
import {EWSEventBrainRingTypes} from '../models';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';
import {clearLocalStorageUserData} from '../utils';

@Component({
  selector: 'app-brain-ring-game-controller',
  imports: [
    PreGameTimerComponent,
    ReactiveFormsModule,
    FormsModule,
    LoaderComponent
  ],
  templateUrl: './brain-ring-game-controller.component.html',
  styleUrl: './brain-ring-game-controller.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService]
})
export class BrainRingGameControllerComponent implements OnInit, OnDestroy {
  public isWaiting = true;
  public roomId: string;
  public playerId: string;
  public answerTime = 0 ;
  public timer: number;
  public isLoading = false;

  private destroy$ = new Subject<void>();
  constructor(
    private readonly route: ActivatedRoute,
    private wsService: WebSocketService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder
  ) {

    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);
        console.log(parseContent,'CONTROLLER');

        this.handleGameEvent(parseContent.eventId)

        if (parseContent.eventId === EWSEventBrainRingTypes.CHECK_EVENT) {
          console.log(parseContent, 'parseContent')
          parseContent.currentEventId > 1 && this.handleGameEvent(parseContent.currentEventId)
        }

        this.cdr.markForCheck()
      })
  }

  ngOnInit() {
    this.roomId = this.route.snapshot.queryParams['roomId'];
    this.playerId = this.route.snapshot.queryParams['playerId'];
    this.startSession();
  }

  private handleGameEvent(eventId: number): void {
    if (eventId=== EWSEventBrainRingTypes.ROOM_ACTIVATED) {
      this.isWaiting = false;

      this.timer = setInterval(()=> {
        this.answerTime++;
      }, 1000) as unknown as number;
    }

    if (eventId === EWSEventBrainRingTypes.ANSWER_SUBMITTED) {
      this.isWaiting = true;
      this.clearTimer()
    }

    if(eventId === EWSEventBrainRingTypes.END_SESSION) {
      clearLocalStorageUserData()
      this.wsService.disconnect();
      this.router.navigate(['/brain-ring-room-not-found']);
    }

    if (eventId === EWSEventBrainRingTypes.NEXT_ROUND) {
      this.isWaiting = false;

      this.timer = setInterval(()=> {
        this.answerTime++;
      }, 1000) as unknown as number;
    }

    this.cdr.markForCheck()
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(brainRingWSTopic + `${this.roomId}`)

      this.cdr.markForCheck();
    },2000)

    setTimeout(()=> {
      this.wsService.subscribe(privateUserBrainRingWSTopic(this.playerId) + `${this.roomId}`)

      //TODO запускается в самом начале, а это не надо ( тоесть когда чел просто идёт по плане без перезагрузки )
      this.setCurrentEvent()
      this.cdr.markForCheck();
    },2000)
  }

  private setCurrentEvent(): void {
    this.wsService.send(
      `/app/brain-ring/current-state`,
      JSON.stringify({
        roomId: this.roomId,
        playerId: this.playerId,
      }),
    )
  }

  submitAnswer(): void {
    this.wsService.send(
      '/app/brain-ring/submit-answer',
      JSON.stringify({
        roomId: this.roomId,
        playerId: this.playerId,
        answerTime: this.answerTime,
      })
    )

    this.clearTimer()
    this.isWaiting = true
  }

  private clearTimer(): void {
    this.answerTime = 0;
    clearInterval(this.timer)
  }

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
