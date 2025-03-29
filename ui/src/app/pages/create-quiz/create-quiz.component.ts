import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {CreateQuizSlidesComponent} from './components/create-quiz-slides/create-quiz-slides.component';
import {IAddQuizBody, ICrateQuizSlide} from './interfaces';
import {slides} from './mocks';
import {CreateQuizWorkflowComponent} from './components/create-quiz-workflow/create-quiz-workflow.component';
import {DataService} from '../../../services/data.service';
import {QuizService} from './services/quiz.service';
import {Subscription} from 'rxjs';

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

  public slides: ICrateQuizSlide[] = [];
  private subs = new Subscription();

  constructor(
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef,
    private readonly dataService: DataService,
    private readonly quizService: QuizService) {
  }

  ngOnInit() {
    this.quizService.setSlides(slides); // Предположим, slides загружены заранее

    this.subs.add(
      this.quizService.slides$.subscribe(slides => {
        this.slides = slides;
      })
    );
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
    let body: IAddQuizBody = {
      name: 'test',
      userId: 1,
      questions: this.slides
    }
    this.dataService.addQuiz(body).subscribe(el=> console.log(el))
  }

}
