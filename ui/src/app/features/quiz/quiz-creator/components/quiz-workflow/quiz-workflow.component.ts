import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ICrateQuizAnswer, ICrateQuizSlide} from '../../../interfaces';
import {FormsModule} from '@angular/forms';
import {QuizManagementService} from '../../services/quiz-management.service';
import {Observable, Subscription} from 'rxjs';
import {AsyncPipe, NgStyle} from '@angular/common';
import {bgAnswerCircleColor, bgAnswerColor} from '../../constants';
import {SymbolSpritePipe} from '../../../../../shared/pipes/symbol-sprite.pipe';

@Component({
  selector: 'app-quiz-workflow',
  imports: [
    FormsModule,
    AsyncPipe,
    NgStyle,
    SymbolSpritePipe
  ],
  templateUrl: './quiz-workflow.component.html',
  standalone: true,
  styleUrl: './quiz-workflow.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QuizWorkflowComponent implements OnInit, OnDestroy {
  private subs = new Subscription();
  public slide$: Observable<ICrateQuizSlide | null>;

  constructor(public quizManagementService: QuizManagementService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.slide$ = this.quizManagementService.selectedSlide$;
  }

  setCorrectAnswer(answer: ICrateQuizAnswer): void {
    this.quizManagementService.updateCorrectAnswer(answer);
  }

  setQuestion(question: string): void {
    this.quizManagementService.updateSlideQuestion(question);
  }

  setImage(imageUrl: string): void {
    this.quizManagementService.updateSlideImage(imageUrl);
  }

  setAnswer(answer: ICrateQuizAnswer, text: string): void {
    console.log(answer, text);
    this.quizManagementService.updateAnswers(answer, text)
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  setAnswerBackground(index: number): string {
    return bgAnswerColor[index];
  }

  setAnswerCircleBackground(index: number): string {
    return bgAnswerCircleColor[index];
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files ? input.files[0] : null;

    if (file) {
      const reader = new FileReader();

      reader.onload = () => {
        this.setImage(reader.result as string);
        this.cdr.markForCheck();
      };

      reader.readAsDataURL(file);
    }
  }
}
