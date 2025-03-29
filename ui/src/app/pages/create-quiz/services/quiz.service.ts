import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {ICrateQuizAnswer, ICrateQuizSlide} from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private _slides = new BehaviorSubject<ICrateQuizSlide[]>([]);
  private _selectedSlide = new BehaviorSubject<ICrateQuizSlide | null>(null);

  public slides$ = this._slides.asObservable();
  public selectedSlide$ = this._selectedSlide.asObservable();

  setCorrectAnswer(answer: ICrateQuizAnswer): void {
    const currentSlides = this._slides.getValue();
    const currentSelectedSlide = this._selectedSlide.getValue();

    if (!currentSlides || !currentSelectedSlide) {
      throw new Error("Slides or selected slide not initialized");
    }

    const updatedSlide: ICrateQuizSlide = {
      ...currentSelectedSlide,
      answers: currentSelectedSlide.answers.map(el => ({
        ...el,
        correct: el.order === answer.order,
      })),
    };

    this._slides.next(
      currentSlides.map(slide =>
        slide.id === currentSelectedSlide.id ? updatedSlide : slide
      )
    );

    this._selectedSlide.next(updatedSlide);
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

  addSlide(slide: ICrateQuizSlide): void {
    this._slides.next([
      ...this._slides.getValue(),
      slide
    ])
  }
}
