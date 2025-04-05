import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {SymbolSpritePipe} from '../../../helpers/pipes/symbol-sprite.pipe';
import {Subject, takeUntil} from 'rxjs';
import {IWsResponse, WebSocketService} from '../../../services/web-socket.service';
import {DataService} from '../../../services/data.service';

@Component({
  selector: 'app-quiz-lobby',
  imports: [
    SymbolSpritePipe
  ],
  templateUrl: './quiz-lobby.component.html',
  standalone: true,
  styleUrl: './quiz-lobby.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizLobbyComponent implements OnInit, OnDestroy {
  public joinCode = '';
  public userCount: number;
  public joinedUsers: Array<{userName: string}> = [];
  newTopic = '/topic/session/';
  private destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly wsService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private readonly dataService: DataService,
    ) {
    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        console.log(JSON.parse(content))
        let parseContent = JSON.parse(content) as IWsResponse;
        this.userCount = parseContent.userCount;
        this.joinedUsers.push({
          userName: `${this.userCount}`,
        });
        this.cdr.markForCheck()
      });
  }

  ngOnInit() {
    console.log(this.route.snapshot)
    this.joinCode = this.route.snapshot.queryParams['joinCode'];
    this.dataService.getParticipantsByCurrentSession(this.joinCode).subscribe(el => {
      this.userCount = el.userCount;
      this.joinedUsers = el.users.map(el => ({userName: el}))
      this.cdr.markForCheck()
    })

    this.startSession()
  }

  private startSession() {
    this.wsService.connect();
    setTimeout(()=> {
      this.wsService.subscribe(this.newTopic + `${this.joinCode}`)
      this.cdr.markForCheck();
    },1000)
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
