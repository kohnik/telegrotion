import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {ICrateQuizSlide} from '../../../interfaces';
import {SymbolSpritePipe} from '../../../../../shared/pipes/symbol-sprite.pipe';
import {AsyncPipe, NgOptimizedImage} from '@angular/common';
import {QuizManagementService} from '../../services/quiz-management.service';
import {Observable, Subscription} from 'rxjs';

@Component({
  selector: 'app-quiz-slides',
  imports: [
    SymbolSpritePipe,
    NgOptimizedImage,
    AsyncPipe
  ],
  templateUrl: './quiz-slides.component.html',
  standalone: true,
  styleUrl: './quiz-slides.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QuizSlidesComponent implements OnInit, OnDestroy {
  private subs = new Subscription();

  public selectedSlide$: Observable<ICrateQuizSlide | null>;
  public slides$: Observable<ICrateQuizSlide[]>;

  constructor(private quizManagementService: QuizManagementService) {}

  ngOnInit(): void {
    this.selectedSlide$ = this.quizManagementService.selectedSlide$;
    this.slides$ = this.quizManagementService.slides$;
  }

  selectSlide(slide: ICrateQuizSlide): void {
    this.quizManagementService.setSelectedSlide(slide);
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  public addSlide(): void {
    this.quizManagementService.addSlide()
  }

  duplicateSlide(slide: ICrateQuizSlide): void {
    this.quizManagementService.duplicateSlide(slide)
  }

  deleteSlide(index: number): void {
    this.quizManagementService.deleteSlide(index)
  }
}
