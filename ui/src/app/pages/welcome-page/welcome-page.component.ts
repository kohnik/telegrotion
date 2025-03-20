import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-welcome-page',
  imports: [],
  templateUrl: './welcome-page.component.html',
  styleUrl: './welcome-page.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WelcomePageComponent {

  constructor(private readonly router: Router) {
  }

  createFizzly(): void {
    this.router.navigate(['/authorization'])
  }
}
