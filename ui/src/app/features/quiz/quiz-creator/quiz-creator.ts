import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {QuizSlidesComponent} from './components/quiz-slides/quiz-slides.component';
import {ICrateQuizSlide} from '../interfaces';
import {slides} from './mocks';
import {QuizWorkflowComponent} from './components/quiz-workflow/quiz-workflow.component';
import {QuizManagementService} from './services/quiz-management.service';
import {catchError, EMPTY, Observable, of, Subject, Subscription, switchMap, takeUntil, throwError} from 'rxjs';
import {QuizSlidePropertyComponent} from './components/quiz-slide-property/quiz-slide-property.component';
import {QuizDataService} from '../quiz.service';
import {SymbolSpritePipe} from '../../../shared/pipes/symbol-sprite.pipe';

@Component({
  selector: 'app-quiz',
  imports: [
    QuizSlidesComponent,
    QuizWorkflowComponent,
    QuizSlidePropertyComponent,
    SymbolSpritePipe
  ],
  templateUrl: './quiz-creator.html',
  standalone: true,
  styleUrl: './quiz-creator.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [QuizDataService, QuizManagementService]
})
export class QuizCreator implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  public slides$: Observable<ICrateQuizSlide[]>
  private subs = new Subscription();
  public isEditing = false;

  constructor(
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef,
    private readonly quizDataService: QuizDataService,
    private readonly quizManagementService: QuizManagementService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.route.params.pipe(
      takeUntil(this.destroy$),
      switchMap(params => {
        const quizId = params['id'];
        if (!quizId) {
          this.quizManagementService.setSlides(slides);
          return EMPTY
        }

        return this.quizDataService.getQuiz(quizId).pipe(
          catchError(error => {
            console.error('Failed to load quiz:', error);
            throw error;
          })
        );
      })
    ).subscribe({
      next: quiz => {
        console.log(quiz.questions)
        this.quizManagementService.setSlides(quiz.questions);
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
    this.router.navigate(['/quiz-library'])
  }

  isPanelOpen = false;

  // Метод для переключения состояния панели
  togglePanel() {
    this.isPanelOpen = !this.isPanelOpen;
  }

}
