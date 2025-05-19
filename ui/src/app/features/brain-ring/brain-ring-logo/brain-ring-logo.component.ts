import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-brain-ring-logo',
  imports: [],
  templateUrl: './brain-ring-logo.component.html',
  styleUrl: './brain-ring-logo.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BrainRingLogoComponent {

  constructor(private router: Router) {}

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }
}
