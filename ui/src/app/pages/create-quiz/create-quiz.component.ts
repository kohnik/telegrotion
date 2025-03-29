import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {CreateQuizSlidesComponent} from './components/create-quiz-slides/create-quiz-slides.component';
import {IAddQuizBody, ICrateQuizSlide} from './interfaces';
import {slides} from './mocks';
import {CreateQuizWorkflowComponent} from './components/create-quiz-workflow/create-quiz-workflow.component';
import {DataService} from '../../../services/data.service';

@Component({
  selector: 'app-create-quiz',
  imports: [
    CreateQuizSlidesComponent,
    CreateQuizWorkflowComponent
  ],
  templateUrl: './create-quiz.component.html',
  standalone: true,
  styleUrl: './create-quiz.component.scss'
})
export class CreateQuizComponent implements OnInit {

  public slides: ICrateQuizSlide[] = slides
  public selectedSlide: ICrateQuizSlide;


  constructor(private readonly router: Router, private readonly dataService: DataService) {
  }

  ngOnInit() {
    this.dataService.getQuiz()
  }

  public goToHome(): void {
    this.router.navigate(['/'])
  }

  public selectSlide(slide: ICrateQuizSlide): void {
    this.selectedSlide = slide;
  }

  createQuiz(): void {
    let body: IAddQuizBody = {
      name: 'test',
      userId: 1,
      questions: this.slides
    }
    this.dataService.addQuiz(body).subscribe(el=> console.log(el))
  }

}
