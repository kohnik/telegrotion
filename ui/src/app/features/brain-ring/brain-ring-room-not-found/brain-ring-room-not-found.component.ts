import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {BrainRingLogoComponent} from "../brain-ring-logo/brain-ring-logo.component";

@Component({
  selector: 'app-brain-ring-room-not-found',
    imports: [
        BrainRingLogoComponent
    ],
  templateUrl: './brain-ring-room-not-found.component.html',
  styleUrl: './brain-ring-room-not-found.component.scss',
  standalone: true
})
export class BrainRingRoomNotFoundComponent {

  constructor(    private router: Router,) {
  }

  public goToMainPage(event: MouseEvent): void {
    event.preventDefault();
    this.router.navigate(['/']);
  }
}
