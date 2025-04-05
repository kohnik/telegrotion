import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import {ICrateQuizSlide} from '../../interfaces';
import {QuizManagementService} from '../../services/quiz-management.service';
import {Subject, takeUntil} from 'rxjs';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-quiz-slide-property',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './quiz-slide-property.component.html',
  styleUrl: './quiz-slide-property.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizSlidePropertyComponent implements OnInit, OnDestroy {

  public slide: ICrateQuizSlide;
  private destroy$ = new Subject<void>();
  public form: FormGroup;
  public quizName: string = '';

  @Output() quizNameChange = new EventEmitter<string>();

  constructor(private quizService: QuizManagementService, private fb: FormBuilder, private cdr: ChangeDetectorRef) {
    this.form = this.fb.group({
      seconds: [null, [Validators.required]],
      points: [null, [Validators.required]],
    })
  }

  ngOnInit() {
    this.quizService.selectedSlide$
      .pipe(takeUntil(this.destroy$))
      .subscribe((selectedSlide) => {
        if(!selectedSlide) return
        this.slide = selectedSlide!;
        this.form.patchValue({
          points: selectedSlide.points,
          seconds: selectedSlide.seconds,
        },{ emitEvent: false });

        this.cdr.markForCheck();
    })


    this.form.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(data => {
      this.updateProperty(
        {
          ...this.slide,
          points: data.points,
          seconds: data.seconds,
        }
      )
      this.cdr.markForCheck();
    })
  }

  updateProperty(updatedSlide: ICrateQuizSlide): void {
    this.quizService.updateSlideProperty(updatedSlide);
  }

  setQuizName(name: string): void {
    this.quizNameChange.emit(name)
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    // this.wsService.disconnect();
  }
}
