import {Component, Input} from '@angular/core';
import {ICrateQuizSlide} from '../../interfaces';

@Component({
  selector: 'app-create-quiz-workflow',
  imports: [],
  templateUrl: './create-quiz-workflow.component.html',
  standalone: true,
  styleUrl: './create-quiz-workflow.component.scss'
})
export class CreateQuizWorkflowComponent {
  @Input() set selectedSlide(data: ICrateQuizSlide) {
    this._selectedSlide = data
  }

  public _selectedSlide:ICrateQuizSlide;
}
