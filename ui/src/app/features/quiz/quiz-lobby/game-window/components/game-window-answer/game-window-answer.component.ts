import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ChartType, NgApexchartsModule} from 'ng-apexcharts';
import {NgForOf, NgIf, NgStyle} from '@angular/common';
import {ICrateQuizAnswer, ICrateQuizSlide, IQuizQuestionStatistic} from '../../../../interfaces';
import {getRandomPalletColor, setAnswerBackground, setAnswerFigureIcon} from '../../../../quiz-creator/constants';
import {SymbolSpritePipe} from '../../../../../../shared/pipes/symbol-sprite.pipe';
import {SpriteIdsType} from '../../../../../../../sprites-ids.type';

export const mock =
  {
    answers:   [
      {
        playerId: 'sdfgdsfsd',
        answer: 0,
      },
      {
        playerId: 'sdfsdf2323',
        answer: 0,
      },
      {
        playerId: 'dsfwe323',
        answer: 1,
      },
      {
        playerId: 'efefefefe',
        answer: 1,
      },
      {
        playerId: '33333',
        answer: 2,
      },
      {
        playerId: 'efefefeffeefee',
        answer: 3,
      },
    ],
    correctAnswer: 0,
    eventId: 0,
  }

export interface IAnswerResult {
  answer: string;
  votes: number;
  isCorrect: boolean;
}
@Component({
  selector: 'app-game-window-answer',
  imports: [NgApexchartsModule, NgForOf, NgIf, SymbolSpritePipe, NgStyle],
  templateUrl: './game-window-answer.component.html',
  standalone: true,
  styleUrl: './game-window-answer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameWindowAnswerComponent implements OnInit, AfterViewInit {

  @Input() public slide: ICrateQuizSlide
  //   = {
  //   order: 0,
  //   points: 0,
  //   questionId: 0,
  //   question:'sdf sdf sdfsdfsd fsdddddddddddddddddddddddd sdsdfsdfsdsdfsdfsd sdfsdfsd sdfsdfsdsdfsdfsd sdfsdfsdsdfsdfsdsdfsdfsdsdfsdfsd sdfsdfsdsdfsdfsd',
  //   answers: [
  //     {
  //       answer: 'Чина',
  //       order: 0,
  //       correct: true
  //     },
  //     {
  //       answer: 'Гений',
  //       order: 1,
  //       correct: false
  //     },
  //     {
  //       answer: 'Москва',
  //       order: 2,
  //       correct: false
  //     },
  //     {
  //       answer: 'Питер',
  //       order: 3,
  //       correct: false
  //     }
  //   ]
  // }
  @Input() public statistics: IQuizQuestionStatistic = mock
  @Output() nextWindowHandler = new EventEmitter();

  correctAnswerIndex = 0;
  answersData: IAnswerResult[] = []
  series: ApexNonAxisChartSeries = [];
  labels: string[] = [];
  colors: string[] = []
  public readyToFill = false;

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    console.log(this.slide)
    this.answersData = this.slide.answers.map(ans => {
      const sortedArr = this.statistics.answers.map(el=> (el.answer === ans.order ? el : null)).filter(Boolean)
      this.correctAnswerIndex = ans.correct ? ans.order : this.correctAnswerIndex

      return {
        answer: ans.answer,
        votes: sortedArr.length,
        isCorrect: ans.correct
      }
    })
    this.series = this.answersData.map(a => a.votes);
    this.labels = this.answersData.map(a => a.answer);
    this.colors = this.answersData.map(((a,index) => setAnswerBackground(index)));
  }

  next(): void {
    this.nextWindowHandler.emit()
  }

  setIcon(item: IAnswerResult): SpriteIdsType {
    return item.isCorrect ? 'approve' : 'red-cross'
  }

  getBarHeight(count: number): number {
    console.log(this.answersData)
    const max = 300;
    const maxCount = Math.max(...this.answersData.map(a => a.votes), 1);
    return (count / maxCount) * max === 0 ? 40 : (count / maxCount) * max;
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.readyToFill = true;
      this.cdr.markForCheck()
    },500);
  }

  protected readonly setAnswerFigureIcon = setAnswerFigureIcon;
  protected readonly setAnswerBackground = setAnswerBackground;
}
