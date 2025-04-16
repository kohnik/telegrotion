import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {PreGameTimerComponent} from '../../quiz/pre-game-timer/pre-game-timer.component';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ICrateQuizAnswer} from '../../quiz/interfaces';
import {Subject, takeUntil} from 'rxjs';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {Router} from '@angular/router';
import {DataService} from '../../../core/services/data.service';
import {EWSEventQuizTypes} from '../../quiz/models';
import {brainRingWSTopic} from '../constants';
import {BrainRingService} from '../brain-ring.service';
import {EWSEventBrainRingTypes} from '../models';

@Component({
  selector: 'app-brain-ring-game-controller',
  imports: [
    PreGameTimerComponent,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './brain-ring-game-controller.component.html',
  styleUrl: './brain-ring-game-controller.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService]
})
export class BrainRingGameControllerComponent implements OnInit, OnDestroy {
  public isJoined = false;
  public isWaiting = false;
  public form: FormGroup
  public roomId: string;
  public teamId: string;
  public answerTime = 0 ;
  public timer: number;

  private destroy$ = new Subject<void>();

  get joinCode(): string {
    return this.form.get('joinCode')!.value;
  }

  get teamName(): string {
    return this.form.get('teamName')!.value;
  }

  constructor(
    private wsService: WebSocketService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder,
    private brainRingService: BrainRingService,) {
    this.form = this.setForm();

    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);
        console.log(parseContent,'CONTROLLER');

        if(parseContent.eventId === EWSEventBrainRingTypes.ROOM_ACTIVATED) {
          this.isWaiting = false;

          this.timer = setInterval(()=> {
            this.answerTime++;
          }, 1000) as unknown as number;
        }

        if(parseContent.eventId === EWSEventBrainRingTypes.ANSWER_SUBMITTED) {
          this.isWaiting = true;
          this.clearTimer()
        }

        if(parseContent.eventId === EWSEventBrainRingTypes.NEXT_ROUND) {
          this.isWaiting = false;

          this.timer = setInterval(()=> {
            this.answerTime++;
          }, 1000) as unknown as number;
        }

        this.cdr.markForCheck()
      })
  }

  ngOnInit() {

  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(brainRingWSTopic + `${this.roomId}`)
      this.cdr.markForCheck();
    },1000)
  }

  public goRoRoom(): void {
    this.brainRingService.goToRoom({
      teamName: this.teamName,
      joinCode: this.joinCode,
    }).subscribe(el =>
      {
        this.roomId = el.roomId;
        this.teamId = el.teamId;

        this.isJoined = true;
        this.isWaiting = true;
        this.cdr.markForCheck();
        this.startSession()
      }
    )
  }

  submitAnswer(): void {
    this.wsService.send(
      '/app/brain-ring/submit-answer',
      JSON.stringify({
        roomId: this.roomId,
        teamId: this.teamId,
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

  public setForm(): FormGroup {
    return this.fb.group({
      joinCode: ['', [Validators.required]],
      teamName: ['', [Validators.required]],
    })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
