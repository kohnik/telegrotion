import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ICrateQuizSlide} from '../../interfaces';
import {SymbolSpritePipe} from '../../../../../helpers/pipes/symbol-sprite.pipe';
import {NgOptimizedImage} from '@angular/common';
import {deleteAtPosition, insertAtPosition} from '../../utils';

@Component({
  selector: 'app-create-quiz-slides',
  imports: [
    SymbolSpritePipe,
    NgOptimizedImage
  ],
  templateUrl: './create-quiz-slides.component.html',
  standalone: true,
  styleUrl: './create-quiz-slides.component.scss'
})
export class CreateQuizSlidesComponent implements OnInit{

  @Input() slides: ICrateQuizSlide[] = [];
  @Output() toSelectSlide = new EventEmitter<ICrateQuizSlide>();

  public selectedSlide: ICrateQuizSlide;

  ngOnInit(): void {
    this.selectSlide(this.slides[0])
  }

  public selectSlide(slide: ICrateQuizSlide): void{
    this.selectedSlide = slide;
    this.toSelectSlide.emit(this.selectedSlide)
  }

  public addSlide(): void {
    this.slides.push(  {
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
          correct: true,
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
    },)
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
