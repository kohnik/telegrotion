import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {SymbolSpritePipe} from "../../../shared/pipes/symbol-sprite.pipe";
import {Subject, takeUntil} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {brainRingWSTopic} from '../constants';
import {BrainRingService} from '../brain-ring.service';
import {IBrainRingTeam} from '../interfaces';
import {EWSEventBrainRingTypes} from '../models';
import {QRCodeComponent} from 'angularx-qrcode';
import {BrainRingLogoComponent} from "../brain-ring-logo/brain-ring-logo.component";

@Component({
  selector: 'app-brain-ring-lobby',
    imports: [
        SymbolSpritePipe,
        QRCodeComponent,
        BrainRingLogoComponent
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
  public playersCount: number;
  public isReadyLobby = false;
  public joinedPlayers: IBrainRingTeam[] = [];
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
            this.joinedPlayers.push({
              playerName: `${parseContent.playerName}`,
              playerId: parseContent.playerId ?? '',
            });
          this.playersCount = this.joinedPlayers.length;
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
      this.joinedPlayers = el.players ?? []
      this.playersCount = this.joinedPlayers.length;
      this.cdr.markForCheck()
    })

    this.startSession()
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(brainRingWSTopic + `${this.roomId}`)
      this.isReadyLobby = true;
      this.cdr.markForCheck();
    },2000)
  }

  public startBrainRing(event: MouseEvent): void {
    event.stopPropagation();

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
  }

  public setLinkByQrCode(): string {
    return `https://fizzly.by/brain-ring-join?roomId=${this.roomId}&joinCode=${this.joinCode}`;
  }

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }
}
