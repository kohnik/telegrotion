import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {IAddQuizBody, ICrateQuizAnswer, ICrateQuizSlide, IQuizConfig} from '../../interfaces';
import {deleteAtPosition, insertAtPosition} from '../../utils';
import {QuizDataService} from '../../quiz.service';

@Injectable()
export class QuizManagementService {
  private _slides = new BehaviorSubject<ICrateQuizSlide[]>([]);
  private _selectedSlide = new BehaviorSubject<ICrateQuizSlide | null>(null);

  public slides$ = this._slides.asObservable();
  public selectedSlide$ = this._selectedSlide.asObservable();

  constructor(private readonly quizDataService: QuizDataService) {
  }

  updateSlide(updateFn: (slide: ICrateQuizSlide) => ICrateQuizSlide): void {
    const currentSlides = this._slides.getValue();
    const currentSelectedSlide = this._selectedSlide.getValue();

    if (!currentSlides || !currentSelectedSlide) {
      throw new Error("Slides or selected slide not initialized");
    }

    const updatedSlide = updateFn(currentSelectedSlide);

    this._slides.next(
      currentSlides.map(slide =>
        slide.questionId === currentSelectedSlide.questionId ? updatedSlide : slide
      )
    );
    this._selectedSlide.next(updatedSlide);
  }

  updateCorrectAnswer(answer: ICrateQuizAnswer): void {
    this.updateSlide(slide => ({
      ...slide,
      answers: slide.answers.map(el => ({
        ...el,
        correct: el.order === answer.order,
      })),
    }));
  }

  updateSlideQuestion(newQuestion: string): void {
    this.updateSlide(slide => ({
      ...slide,
      question: newQuestion,
    }));
  }

  updateSlideImage(imageUrl: string): void {
    this.updateSlide(slide => ({
      ...slide,
      img: imageUrl,
    }));
  }

  updateSlideProperty(updatedSlide: ICrateQuizSlide): void {
    this.updateSlide(slide => ({
      ...updatedSlide
    }));
  }

  updateAnswers(answer: ICrateQuizAnswer, newText: string): void {
    this.updateSlide(slide => {
      const updatedAnswers =
        slide.answers.map(el => el.order === answer.order
          ? { ...el,
            answer: newText,
          } : el
        )

      return {
        ...slide,
        answers: updatedAnswers
      }
    })
  }

  setSlides(slides: ICrateQuizSlide[]): void {
    this._slides.next(slides);
    if (!this._selectedSlide.getValue()) {
      this._selectedSlide.next(slides[0]);
    }
  }

  setSelectedSlide(slide: ICrateQuizSlide): void {
    this._selectedSlide.next(slide);
  }

  addSlide(): void {
    let newSlide: ICrateQuizSlide =     {
      questionId: new Date().getTime(),
      question: 'Введите ваш вопрос',
      type: "Quiz",
      order: this._slides.getValue().length,
      seconds: 20,
      img: '',
      points: 500,
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
    }

    this._slides.next([
      ...this._slides.getValue(),
      newSlide
    ])
  }

  deleteSlide(index: number): void {
    this._slides.next(deleteAtPosition(this._slides.getValue(), index))
  }

  duplicateSlide(slide: ICrateQuizSlide): void {
    let duplicateSlide :ICrateQuizSlide = {
      ...slide,
    }

    let newOrder = slide.order + 1;

    this._slides.next(
      insertAtPosition(this._slides.getValue(), duplicateSlide, newOrder ).map((el, index) => ({
        ...el,
        order: index
      }) )
    )
  }

  createNewQuiz(quizName: string): Observable<IQuizConfig> {
    let body: IAddQuizBody = {
      name: quizName,
      userId: 1,
      questions: this._slides.value,
    }
    return this.quizDataService.addQuiz(body)
  }
}
