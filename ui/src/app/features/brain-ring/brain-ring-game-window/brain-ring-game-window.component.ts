import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {Subject, takeUntil} from 'rxjs';
import {EWSEventQuizTypes} from '../../quiz/models';
import {ActivatedRoute, Router} from '@angular/router';
import {brainRingWSTopic, privateUserBrainRingWSTopic} from '../constants';
import {EWSEventBrainRingTypes} from '../models';
import {IBrainRingTeam, IBrainRingTeamAnswerData} from '../interfaces';
import {BrainRingService} from '../brain-ring.service';
import {BrainRingLogoComponent} from "../brain-ring-logo/brain-ring-logo.component";

@Component({
  selector: 'app-brain-ring-game-window',
    imports: [
        BrainRingLogoComponent
    ],
  templateUrl: './brain-ring-game-window.component.html',
  styleUrl: './brain-ring-game-window.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService]
})
export class BrainRingGameWindowComponent implements OnInit {
  public isFirstTeamAnswer = false;
  public answeredTeam: IBrainRingTeamAnswerData;

  private destroy$ = new Subject<void>();
  public joinCode = '';
  public roomId = '';
  public playerId = '';

  constructor(private readonly wsService: WebSocketService,
              private readonly brainRingService: BrainRingService,
              private cdr: ChangeDetectorRef,
              private readonly route: ActivatedRoute,
              private readonly router: Router,
              ) {
    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        // console.log(JSON.parse(content))
        let parseContent = JSON.parse(content);
        console.log(parseContent,'WINDOW');

        this.handleGameEvent(parseContent.eventId, parseContent)

        if (parseContent.eventId === EWSEventBrainRingTypes.CHECK_EVENT) {
          console.log(parseContent, 'parseContent')
          this.handleGameEvent(parseContent.currentEventId, JSON.parse(parseContent.payload))
        }

        this.cdr.markForCheck();

      });
  }

  private handleGameEvent(eventId: number, parseContent?: any): void {

    if(eventId === EWSEventBrainRingTypes.ANSWER_SUBMITTED) {
      this.answeredTeam = {...parseContent}
      this.isFirstTeamAnswer = true

      this.playReadySound();
    }

    if(eventId === EWSEventBrainRingTypes.NEXT_ROUND) {
      this.isFirstTeamAnswer = false
    }

    if(eventId === EWSEventBrainRingTypes.END_SESSION) {
      this.router.navigate(['/']);
    }
    this.cdr.markForCheck()
  }

  ngOnInit() {
    this.joinCode = this.route.snapshot.queryParams['joinCode'];
    this.roomId = this.route.snapshot.queryParams['roomId'];
    // this.dataService.getParticipantsByCurrentSession(this.joinCode).subscribe(el => {
    //   this.userCount = el.userCount;
    //   this.joinedUsers = el.users.map(el => ({username: el}))
    //   this.cdr.markForCheck()
    // })

    this.startSession()
  }

  playReadySound(): void {
    const audio = new Audio('assets/sounds/buzz-ready.mp3');
    audio.play().catch(e => console.error('Ошибка воспроизведения:', e));
    this.cdr.markForCheck();
  }


  nextQuestion(): void {
    this.wsService.send(
      '/app/brain-ring/nextQuestion',
      JSON.stringify(
        {
          roomId: this.roomId
        }
      )
    )

    this.isFirstTeamAnswer = false;
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(brainRingWSTopic+ `${this.roomId}`)

      this.cdr.markForCheck();
    },2000)

    setTimeout(()=> {
      this.wsService.subscribe(privateUserBrainRingWSTopic(this.roomId) + `${this.roomId}`)

      //TODO запускается в самом начале, а это не надо ( тоесть когда чел просто идёт по плане без перезагрузки )
      this.setCurrentEvent()
      this.cdr.markForCheck();
    },2000)
  }

  private setCurrentEvent(): void {
    this.wsService.send(
      '/app/brain-ring/current-state',
      JSON.stringify({
        roomId: this.roomId,
        playerId: this.roomId
      })
    )
  }

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }

  public endSession(): void {
    this.brainRingService.endSessionRoom({
      roomId: this.roomId
    }).subscribe()
  }
}
