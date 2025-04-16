import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {SymbolSpritePipe} from "../../../shared/pipes/symbol-sprite.pipe";

@Component({
  selector: 'app-quiz-welcome-page',
    imports: [
        SymbolSpritePipe
    ],
  templateUrl: './quiz-welcome-page.component.html',
  styleUrl: './quiz-welcome-page.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizWelcomePageComponent {

  public isAuthorized = true;

  constructor(private readonly router: Router) {}

  createQuiz(): void {
    if (!this.isAuthorized) {
      this.router.navigate(['/authorization'])
    } else {
      this.router.navigate(['/quiz'])
    }
  }

  connectToQuiz(): void {
    if (!this.isAuthorized) {
      this.router.navigate(['/authorization'])
    } else {
      this.router.navigate(['/join'])
    }
  }
}
