import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {DataService} from '../../../services/data.service';
import {Router} from '@angular/router';
import {catchError, Observable, of, Subject, tap} from 'rxjs';
import {IQuizConfig} from '../create-quiz/interfaces';
import {AsyncPipe} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-main',
  imports: [
    AsyncPipe,
    FormsModule,
  ],
  templateUrl: './main.component.html',
  standalone: true,
  styleUrl: './main.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  quizzes$: Observable<IQuizConfig[]>;

  constructor(private dataService: DataService, private router: Router, )  {
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
