import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, Observable, of, Subject, tap} from 'rxjs';
import {IQuizConfig} from '../interfaces';
import {AsyncPipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {QuizDataService} from '../quiz.service';
import {QuizLogoComponent} from "../quiz-logo/quiz-logo.component";
import {LoaderComponent} from '../../../shared/components/loader/loader.component';
import {
  LoaderWithoutBackgroundComponent
} from '../../../shared/components/loader-without-background/loader-without-background.component';
import {EGameType} from '../../../shared/interfaces';

@Component({
  selector: 'app-quiz-library',
  imports: [
    AsyncPipe,
    FormsModule,
    QuizLogoComponent,
    LoaderComponent,
    LoaderWithoutBackgroundComponent,
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
  isLoading = false

  constructor(private quizDataService: QuizDataService, private router: Router, )  {
    this.loadQuizzes(1);
  }

  ngOnInit() {}

  loadQuizzes(userId: number): void {
    this.isLoading = true

    this.quizzes$ = this.quizDataService.getAllQuizzesByUserId(userId).pipe(
      tap(quizzes =>
      {
        this.isLoading = false
        console.log('Загружено quizzes:', quizzes)
      }),
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
    this.router.navigate([`quiz-creator/${quiz.id}`]);
  }

  public startQuiz(quiz: IQuizConfig): void {
    this.quizDataService.startQuiz({
      quizId: quiz.id,
      userId: 0
    }).subscribe(el =>
      {
        this.router.navigate([`/quiz-lobby`],
          {
            queryParams:
              {
                joinCode: el.joinCode,
                roomId: el.roomId
              } })
      }
    )
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly EGameType = EGameType;
}
