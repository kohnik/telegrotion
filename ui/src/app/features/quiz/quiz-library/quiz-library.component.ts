import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, Observable, of, Subject, tap} from 'rxjs';
import {IQuizConfig} from '../interfaces';
import {AsyncPipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {QuizDataService} from '../quiz.service';

@Component({
  selector: 'app-quiz-library',
  imports: [
    AsyncPipe,
    FormsModule,
  ],
  templateUrl: './quiz-library.component.html',
  standalone: true,
  styleUrl: './quiz-library.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [QuizDataService]
})
export class QuizLibraryComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  quizzes$: Observable<IQuizConfig[]>;

  constructor(private quizDataService: QuizDataService, private router: Router, )  {
    this.loadQuizzes(1);
  }

  ngOnInit() {}

  loadQuizzes(userId: number): void {
    this.quizzes$ = this.quizDataService.getAllQuizzesByUserId(userId);

    this.quizzes$ = this.quizDataService.getAllQuizzesByUserId(userId).pipe(
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
    this.router.navigate(['/quiz-library'])
  }

  public goToQuiz(quiz: IQuizConfig): void {
    this.router.navigate([`quiz/${quiz.id}`]);
  }

  public startQuiz(quiz: IQuizConfig): void {
    this.quizDataService.startQuiz({
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
