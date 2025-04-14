import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {SymbolSpritePipe} from "../../../shared/pipes/symbol-sprite.pipe";
import {Subject, takeUntil} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {DataService} from '../../../core/services/data.service';
import {EWSEventQuizTypes} from '../../quiz/models';
import {brainRingWSTopic} from '../constants';
import {BrainRingService} from '../brain-ring.service';
import {IBrainRingTeam} from '../interfaces';
import {EWSEventBrainRingTypes} from '../models';
import {QRCodeComponent} from 'angularx-qrcode';

@Component({
  selector: 'app-brain-ring-lobby',
  imports: [
    SymbolSpritePipe,
    QRCodeComponent
  ],
  templateUrl: './brain-ring-lobby.component.html',
  styleUrl: './brain-ring-lobby.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService]
})
export class BrainRingLobbyComponent implements OnInit, OnDestroy {
  public joinCode = '';
  public roomId = '';
  public teamsCount: number;
  public joinedTeams: IBrainRingTeam[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly wsService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private readonly brainRingService: BrainRingService,
  ) {
    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);
        console.log(parseContent, 'QUIZ_LOBBY')

        if(parseContent.eventId === EWSEventBrainRingTypes.USER_ADDED) {
            this.joinedTeams.push({
              teamName: `${parseContent.teamName}`,
              teamId: parseContent.teamId ?? '',
            });
          this.cdr.markForCheck()
        }

        if(parseContent.eventId === EWSEventBrainRingTypes.ROOM_ACTIVATED) {
          this.router.navigate(['/brain-ring-game-window'],
            {
              queryParams: {
                roomId: this.roomId
              }
            })
          this.cdr.markForCheck()
        }


        // if(parseContent.eventId === EWSEventQuizTypes.QUIZ_STARTED) {
        //   this.router.navigate(['/brain-ring-game-window'], {
        //     queryParams: {
        //       joinCode: this.joinCode
        //     }
        //   });
        //   this.cdr.markForCheck()
        // }
        // else {
        //   this.userCount = parseContent.userCount;
        //   this.joinedTeams.push({
        //     username: `${parseContent.username}`,
        //   });
        //   this.cdr.markForCheck()
        // }
      });
  }

  ngOnInit() {
    this.joinCode = this.route.snapshot.queryParams['joinCode'];
    this.roomId = this.route.snapshot.queryParams['roomId'];

    this.brainRingService.roomInfo(this.roomId).subscribe(el => {
      this.joinedTeams = el.teams ?? []
      this.teamsCount = this.joinedTeams.length;
      this.cdr.markForCheck()
    })

    this.startSession()
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(brainRingWSTopic + `${this.roomId}`)
      this.cdr.markForCheck();
    },3000)
  }

  public startBrainRing(): void {
    this.wsService.send(
      '/app/brain-ring/start',
      JSON.stringify({
        roomId: this.roomId,
      })
    )
  }

  public deleteTeam(teamName: string) {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
