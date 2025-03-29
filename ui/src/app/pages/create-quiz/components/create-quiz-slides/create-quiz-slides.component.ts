import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import {ICrateQuizSlide} from '../../interfaces';
import {SymbolSpritePipe} from '../../../../../helpers/pipes/symbol-sprite.pipe';
import {AsyncPipe, NgOptimizedImage} from '@angular/common';
import {deleteAtPosition, insertAtPosition} from '../../utils';
import {QuizService} from '../../services/quiz.service';
import {Observable, Subscription} from 'rxjs';

@Component({
  selector: 'app-create-quiz-slides',
  imports: [
    SymbolSpritePipe,
    NgOptimizedImage,
    AsyncPipe
  ],
  templateUrl: './create-quiz-slides.component.html',
  standalone: true,
  styleUrl: './create-quiz-slides.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateQuizSlidesComponent implements OnInit, OnDestroy {
  slides: ICrateQuizSlide[] = [];
  private subs = new Subscription();

  public slide$: Observable<ICrateQuizSlide | null>;

  constructor(private quizService: QuizService) {}

  ngOnInit(): void {
    this.subs.add(
      this.quizService.slides$.subscribe(slides => {
        this.slides = slides;
      })
    );

    this.slide$ = this.quizService.selectedSlide$;
  }

  selectSlide(slide: ICrateQuizSlide): void {
    this.quizService.setSelectedSlide(slide);
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  public addSlide(): void {
    let newSlide =     {
      id: this.slides.length,
      question: 'Введите ваш вопрос',
      type: "Quiz",
      order: this.slides.length,
      timeLimit: 20,
      img: '',
      points: 20,
      answers: [
        {
          answer: 'dfdsf',
          correct: false,
          order: 0,
        },
        {
          answer: '12312',
          correct: false,
          order: 1,
        },
        {
          answer: 'opa',
          correct: false,
          order: 2,
        },
        {
          answer: 'dfsdsd',
          correct: false,
          order: 3,
        },
      ]
    }

    this.quizService.addSlide(newSlide)
  }

  duplicateSlide(slide: ICrateQuizSlide, index: number): void {
    let duplicateSlide :ICrateQuizSlide = {
      ...slide,
    }

    let newOrder = slide.order + 1;

    this.slides = insertAtPosition(this.slides, duplicateSlide, newOrder )
      .map((el, index) => ({
      ...el,
      order: index
    }) )
  }

  deleteSlide(index: number): void {
    this.slides = deleteAtPosition(this.slides, index)
  }
}
