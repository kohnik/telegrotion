import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {DataService} from '../../../services/data.service';
import {Router} from '@angular/router';
import {QuizSlidesComponent} from '../create-quiz/components/quiz-slides/quiz-slides.component';
import {
  QuizWorkflowComponent
} from '../create-quiz/components/quiz-workflow/quiz-workflow.component';
import {BehaviorSubject, catchError, Observable, of, Subject, takeUntil, tap} from 'rxjs';
import {IQuizConfig} from '../create-quiz/interfaces';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {WebSocketService} from '../../../services/web-socket.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-main',
  imports: [
    QuizSlidesComponent,
    QuizWorkflowComponent,
    AsyncPipe,
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './main.component.html',
  standalone: true,
  styleUrl: './main.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainComponent implements OnInit, OnDestroy {
  newTopic = '/topic/topic/session';
  messages: Array<{ content: string; type: string }> = [];
  private destroy$ = new Subject<void>();

  isConnected$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  subscriptions$: BehaviorSubject<string[]> = new BehaviorSubject([] as string[]);
  quizzes$: Observable<IQuizConfig[]>;

  constructor(private dataService: DataService, private router: Router, private wsService: WebSocketService)  {
    this.isConnected$ = this.wsService.connectionStatus;
    this.subscriptions$ = this.wsService.subscriptionsList;

    this.wsService.messages
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ content, topic }) => {
        console.log(content, topic)
        this.messages.push({
          content: `[${topic}] ${content}`,
          type: topic === 'error' ? 'error' : 'message'
        });
      });
    this.loadQuizzes(1);
  }


  ngOnInit() {}


  loadQuizzes(userId: number): void {
    this.quizzes$ = this.dataService.getAllQuizzesByUserId(userId);

    this.quizzes$ = this.dataService.getAllQuizzesByUserId(userId).pipe(
      tap(quizzes => console.log('Загружено quizzes:', quizzes)),
      catchError(error => {
        console.error('Ошибка загрузки:', error);
        return of([]);
      })
    );
  }

  public goToHome(): void {
    this.router.navigate(['/'])
  }

  public goToQuizList(): void {
    this.router.navigate(['/main'])
  }

  public goToQuiz(quiz: IQuizConfig): void {
    this.router.navigate([`quiz/${quiz.id}`]);
  }

  public startQuiz(quiz: IQuizConfig): void {
    this.dataService.startQuiz({
      quizId: quiz.id,
      userId: 0
    }).subscribe(el =>
      {
        this.router.navigate([`/quiz-lobby/${el.id}`], { queryParams: { joinCode: el.joinCode } })
      }
    )
  }


  connect() {
    this.wsService.connect();
  }

  disconnect() {
    this.wsService.disconnect();
  }

  subscribe() {
    if (!this.newTopic.trim()) {
      alert('Please enter a topic');
      return;
    }
    this.wsService.subscribe(this.newTopic.trim());
    this.newTopic = '';
  }


  unsubscribe(topic: string) {
    this.wsService.unsubscribe(topic);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.wsService.disconnect();
  }
}
