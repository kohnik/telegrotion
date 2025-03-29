import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ICrateQuizAnswer, ICrateQuizSlide} from '../../interfaces';
import {FormsModule} from '@angular/forms';
import {QuizService} from '../../services/quiz.service';
import {Observable, Subscription} from 'rxjs';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-create-quiz-workflow',
  imports: [
    FormsModule,
    AsyncPipe
  ],
  templateUrl: './create-quiz-workflow.component.html',
  standalone: true,
  styleUrl: './create-quiz-workflow.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateQuizWorkflowComponent implements OnInit, OnDestroy {
  private subs = new Subscription();
  public slide$: Observable<ICrateQuizSlide | null>;

  constructor(public quizService: QuizService) {}

  ngOnInit(): void {
    this.slide$ = this.quizService.selectedSlide$;
  }

  setCorrectAnswer(answer: ICrateQuizAnswer): void {
    this.quizService.setCorrectAnswer(answer);
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }
}
