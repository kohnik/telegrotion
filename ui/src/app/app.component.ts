import {ChangeDetectionStrategy, Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {WelcomePageComponent} from './pages/welcome-page/welcome-page.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, WelcomePageComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  title = 'ui';
}
