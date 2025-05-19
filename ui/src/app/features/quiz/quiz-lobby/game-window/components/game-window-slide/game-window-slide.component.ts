import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ICurrentSlide} from '../../../interfaces';
import {NgStyle} from '@angular/common';
import {setAnswerBackground, setAnswerFigureIcon} from '../../../../quiz-creator/constants';
import {SymbolSpritePipe} from '../../../../../../shared/pipes/symbol-sprite.pipe';
import {ICrateQuizAnswer, ICrateQuizSlide} from '../../../../interfaces';

@Component({
  selector: 'app-game-window-slide',
  imports: [
    NgStyle,
    SymbolSpritePipe
  ],
  templateUrl: './game-window-slide.component.html',
  styleUrl: './game-window-slide.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameWindowSlideComponent implements OnInit {

  public timeLeft = 0

  @Input() slide: ICurrentSlide;
  @Output() slideAnswers = new EventEmitter<ICrateQuizSlide>();

  protected readonly setAnswerBackground = setAnswerBackground;
  protected readonly setAnswerFigureIcon = setAnswerFigureIcon;

  constructor(private cdr: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.timeLeft = this.slide.timeLeft
    let timer = setInterval(() => {
      if(this.timeLeft === 0 )
      {
        clearInterval(timer);
        return;
      }
      this.timeLeft -= 1;
      this.cdr.markForCheck()
    },1000)

    this.slideAnswers.emit(JSON.parse(JSON.stringify(this.slide)))
  }
}
