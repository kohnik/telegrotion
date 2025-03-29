import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {CreateQuizSlidesComponent} from './components/create-quiz-slides/create-quiz-slides.component';
import {IAddQuizBody, ICrateQuizSlide} from './interfaces';
import {slides} from './mocks';
import {CreateQuizWorkflowComponent} from './components/create-quiz-workflow/create-quiz-workflow.component';
import {DataService} from '../../../services/data.service';
import {QuizManagementService} from './services/quiz-management.service';
import {Observable, Subscription} from 'rxjs';

@Component({
  selector: 'app-create-quiz',
  imports: [
    CreateQuizSlidesComponent,
    CreateQuizWorkflowComponent
  ],
  templateUrl: './create-quiz.component.html',
  standalone: true,
  styleUrl: './create-quiz.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateQuizComponent implements OnInit, OnDestroy {

  public slides$: Observable<ICrateQuizSlide[]>
  private subs = new Subscription();

  constructor(
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef,
    private readonly dataService: DataService,
    private readonly quizService: QuizManagementService) {
  }

  ngOnInit() {
    this.quizService.setSlides(slides);

    this.slides$ = this.quizService.slides$;
  }

  selectSlide(slide: ICrateQuizSlide): void {
    this.quizService.setSelectedSlide(slide);
  }

  ngOnDestroy() {
    this.subs.unsubscribe(); // Отписываемся от всех подписок
  }

  public goToHome(): void {
    this.router.navigate(['/'])
  }

  createQuiz(): void {
    this.quizService.createNewQuiz()
  }

}
