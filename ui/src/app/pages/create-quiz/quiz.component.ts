import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {QuizSlidesComponent} from './components/quiz-slides/quiz-slides.component';
import {ICrateQuizSlide} from './interfaces';
import {slides} from './mocks';
import {QuizWorkflowComponent} from './components/quiz-workflow/quiz-workflow.component';
import {DataService} from '../../../services/data.service';
import {QuizManagementService} from './services/quiz-management.service';
import {catchError, EMPTY, Observable, of, Subject, Subscription, switchMap, takeUntil, throwError} from 'rxjs';

@Component({
  selector: 'app-quiz',
  imports: [
    QuizSlidesComponent,
    QuizWorkflowComponent
  ],
  templateUrl: './quiz.component.html',
  standalone: true,
  styleUrl: './quiz.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  public slides$: Observable<ICrateQuizSlide[]>
  private subs = new Subscription();
  public isEditing = false;


  constructor(
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef,
    private readonly dataService: DataService,
    private readonly quizService: QuizManagementService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.route.params.pipe(
      takeUntil(this.destroy$),
      switchMap(params => {
        const quizId = params['id'];
        if (!quizId) {
          this.quizService.setSlides(slides);
          return EMPTY
        }

        return this.dataService.getQuiz(quizId).pipe(
          catchError(error => {
            console.error('Failed to load quiz:', error);
            throw error;
          })
        );
      })
    ).subscribe({
      next: quiz => {
        this.quizService.setSlides(quiz.questions);
        this.isEditing = true;
      },
      error: err => {
        console.error('Error in quiz subscription:', err);
      }
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public goToHome(): void {
    this.router.navigate(['/'])
  }

  public goToQuizList(): void {
    this.router.navigate(['/main'])
  }

  createQuiz(): void {
    this.quizService.createNewQuiz().subscribe(() => {
      this.router.navigate(['/main']);
    })
  }

}
