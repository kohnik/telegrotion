import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-quiz-logo',
  imports: [],
  templateUrl: './quiz-logo.component.html',
  styleUrl: './quiz-logo.component.scss',
  standalone: true,
})
export class QuizLogoComponent {

  constructor(private router: Router) {}

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }
}
