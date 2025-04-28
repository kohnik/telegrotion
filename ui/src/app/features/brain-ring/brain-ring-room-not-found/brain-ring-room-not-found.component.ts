import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-brain-ring-room-not-found',
  imports: [],
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
