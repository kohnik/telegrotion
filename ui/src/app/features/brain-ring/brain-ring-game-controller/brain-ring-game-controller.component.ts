import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {PreGameTimerComponent} from '../../quiz/pre-game-timer/pre-game-timer.component';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ICrateQuizAnswer} from '../../quiz/interfaces';
import {Subject, takeUntil} from 'rxjs';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DataService} from '../../../core/services/data.service';
import {EWSEventQuizTypes} from '../../quiz/models';
import {brainRingWSTopic} from '../constants';
import {BrainRingService} from '../brain-ring.service';
import {EWSEventBrainRingTypes} from '../models';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';

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
  public teamId: string;
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
    this.roomId = this.route.snapshot.queryParams['roomId'];
    this.teamId = this.route.snapshot.queryParams['teamId'];
    this.startSession();
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(brainRingWSTopic + `${this.roomId}`)
      this.cdr.markForCheck();
    },1000)
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

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
