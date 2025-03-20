import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-create-quiz',
  imports: [],
  templateUrl: './create-quiz.component.html',
  standalone: true,
  styleUrl: './create-quiz.component.scss'
})
export class CreateQuizComponent {


  constructor(private readonly router: Router) {
  }
  goToHome(): void {
    this.router.navigate(['/'])
  }
}
