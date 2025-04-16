import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {SymbolSpritePipe} from '../../../shared/pipes/symbol-sprite.pipe';
import {Subject, takeUntil} from 'rxjs';
import {IWsResponse, WebSocketService} from '../../../core/services/web-socket.service';
import {DataService} from '../../../core/services/data.service';
import {EWSEventQuizTypes} from '../models';
import {quizRingWSTopic} from '../constants';

@Component({
  selector: 'app-quiz-lobby',
  imports: [
    SymbolSpritePipe,
  ],
  templateUrl: './quiz-lobby.component.html',
  standalone: true,
  styleUrl: './quiz-lobby.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizLobbyComponent implements OnInit, OnDestroy {
  public joinCode = '';
  public userCount: number;
  public joinedUsers: Array<{username: string}> = [];
  private destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly wsService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private readonly dataService: DataService,
    ) {
    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        // console.log(JSON.parse(content))
        let parseContent = JSON.parse(content);
        // console.log(parseContent, 'QUIZ_LOBBY')

        if(parseContent.eventId === EWSEventQuizTypes.QUIZ_STARTED) {
          this.router.navigate(['/game-window'], {
            queryParams: {
              joinCode: this.joinCode
            }
          });
          this.cdr.markForCheck()
        }
        else {
          this.userCount = parseContent.userCount;
          this.joinedUsers.push({
            username: `${parseContent.username}`,
          });
          this.cdr.markForCheck()
        }
      });
  }

  ngOnInit() {
    console.log(this.route.snapshot)
    this.joinCode = this.route.snapshot.queryParams['joinCode'];
    this.dataService.getParticipantsByCurrentSession(this.joinCode).subscribe(el => {
      this.userCount = el.userCount;
      this.joinedUsers = el.users.map(el => ({username: el}))
      this.cdr.markForCheck()
    })

    this.startSession()
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(quizRingWSTopic + `${this.joinCode}`)
      this.cdr.markForCheck();
    },3000)
  }

  public startQuiz(): void {
    this.wsService.send(
      '/app/start',
      JSON.stringify({
        joinCode: this.joinCode,
      })
    )
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
