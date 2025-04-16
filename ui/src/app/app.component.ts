import {ChangeDetectionStrategy, Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {QuizWelcomePageComponent} from './features/quiz/quiz-welcome-page/quiz-welcome-page.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, QuizWelcomePageComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  title = 'ui';
}
