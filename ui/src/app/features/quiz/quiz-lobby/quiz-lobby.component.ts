import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {SymbolSpritePipe} from '../../../shared/pipes/symbol-sprite.pipe';
import {Subject, takeUntil} from 'rxjs';
import {IWsResponse, WebSocketService} from '../../../core/services/web-socket.service';
import {EWSEventQuizTypes} from '../models';
import {quizRingWSTopic} from '../constants';
import {QuizDataService} from '../quiz.service';
import {QuizLogoComponent} from '../quiz-logo/quiz-logo.component';
import {IQuizPlayer} from '../interfaces';
import {QRCodeComponent} from 'angularx-qrcode';

@Component({
  selector: 'app-quiz-lobby',
  imports: [
    SymbolSpritePipe,
    QuizLogoComponent,
    QRCodeComponent,
  ],
  templateUrl: './quiz-lobby.component.html',
  standalone: true,
  styleUrl: './quiz-lobby.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [QuizDataService]
})
export class QuizLobbyComponent implements OnInit, OnDestroy {
  public joinCode = '';
  public roomId = '';
  public isReadyLobby = false;
  public playersCount: number;
  public joinedPlayers: IQuizPlayer[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly wsService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private readonly quizDataService: QuizDataService,
    ) {
    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        let parseContent = JSON.parse(content);

        if(parseContent.eventId === EWSEventQuizTypes.QUIZ_STARTED) {
          this.router.navigate(['/quiz-game-window'], {
            queryParams: {
              joinCode: this.joinCode,
              roomId: this.roomId
            }
          });
          this.cdr.markForCheck()
        }
        else {
          this.playersCount = parseContent.userCount;
          this.joinedPlayers.push({
            playerName: parseContent.username,
            playerId: parseContent.playerId,
          });
          this.cdr.markForCheck()
        }
      });
  }

  ngOnInit() {
    this.joinCode = this.route.snapshot.queryParams['joinCode'];
    this.roomId = this.route.snapshot.queryParams['roomId'];
    this.quizDataService.getParticipantsByCurrentSession(this.roomId).subscribe(el => {
      this.playersCount = el.length;
      this.joinedPlayers = el.map(el => ({
        // @ts-ignore
        playerName: el as string,
        playerId: el.playerId
      }))
      this.cdr.markForCheck()
    })

    this.startSession()
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(quizRingWSTopic + `${this.roomId}`)
      this.isReadyLobby = true;
      this.cdr.markForCheck();
    },3000)
  }

  public startQuiz(): void {
    this.wsService.send(
      '/app/quiz/start',
      JSON.stringify({
        joinCode: this.joinCode,
        roomId: this.roomId,
      })
    )
  }

  public deletePlayer(playerName: string) {

  }

  public setLinkByQrCode(): string {
    return `https://fizzly.by/quiz-join?roomId=${this.roomId}&joinCode=${this.joinCode}`;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
