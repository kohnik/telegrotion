import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {Subject, takeUntil} from 'rxjs';
import {EWSEventQuizTypes} from '../../quiz/models';
import {ActivatedRoute, Router} from '@angular/router';
import {brainRingWSTopic} from '../constants';
import {EWSEventBrainRingTypes} from '../models';
import {IBrainRingTeam, IBrainRingTeamAnswerData} from '../interfaces';

@Component({
  selector: 'app-brain-ring-game-window',
  imports: [],
  templateUrl: './brain-ring-game-window.component.html',
  styleUrl: './brain-ring-game-window.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BrainRingGameWindowComponent implements OnInit {
  public isFirstTeamAnswer = false;
  public answeredTeam: IBrainRingTeamAnswerData;

  private destroy$ = new Subject<void>();
  public joinCode = '';
  public roomId = '';

  constructor(private readonly wsService: WebSocketService,
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

        if(parseContent.eventId === EWSEventBrainRingTypes.ANSWER_SUBMITTED) {
          this.answeredTeam = {...parseContent}
          this.isFirstTeamAnswer = true
        }

        if(parseContent.eventId === EWSEventBrainRingTypes.NEXT_ROUND) {
          this.isFirstTeamAnswer = false
        }

        this.cdr.markForCheck();

      });
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
      this.wsService.subscribe(brainRingWSTopic+ `${this.joinCode}`)
      this.cdr.markForCheck();
    },3000)
  }
}
