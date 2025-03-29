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
import {QuizManagementService} from '../../services/quiz-management.service';
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
  private subs = new Subscription();

  public slide$: Observable<ICrateQuizSlide | null>;
  public slides$: Observable<ICrateQuizSlide[]>;

  constructor(private quizService: QuizManagementService) {}

  ngOnInit(): void {
    this.slide$ = this.quizService.selectedSlide$;
    this.slides$ = this.quizService.slides$;
  }

  selectSlide(slide: ICrateQuizSlide): void {
    this.quizService.setSelectedSlide(slide);
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  public addSlide(): void {
    this.quizService.addSlide()
  }

  duplicateSlide(slide: ICrateQuizSlide): void {
    this.quizService.duplicateSlide(slide)
  }

  deleteSlide(index: number): void {
    this.quizService.deleteSlide(index)
  }
}
