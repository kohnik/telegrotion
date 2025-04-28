import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ICrateQuizAnswer, ICrateQuizSlide} from '../../../interfaces';
import {FormsModule} from '@angular/forms';
import {QuizManagementService} from '../../services/quiz-management.service';
import {Observable, Subscription} from 'rxjs';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-quiz-workflow',
  imports: [
    FormsModule,
    AsyncPipe
  ],
  templateUrl: './quiz-workflow.component.html',
  standalone: true,
  styleUrl: './quiz-workflow.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QuizWorkflowComponent implements OnInit, OnDestroy {
  private subs = new Subscription();
  public slide$: Observable<ICrateQuizSlide | null>;

  constructor(public quizManagementService: QuizManagementService) {}

  ngOnInit(): void {
    this.slide$ = this.quizManagementService.selectedSlide$;
  }

  setCorrectAnswer(answer: ICrateQuizAnswer): void {
    this.quizManagementService.updateCorrectAnswer(answer);
  }

  setQuestion(question: string): void {
    this.quizManagementService.updateSlideQuestion(question);
  }

  setAnswer(answer: ICrateQuizAnswer, text: string): void {
    console.log(answer, text);
    this.quizManagementService.updateAnswers(answer, text)
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }
}
