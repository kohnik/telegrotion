import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Router} from '@angular/router';
import {BrainRingService} from '../brain-ring.service';
import {SymbolSpritePipe} from '../../../shared/pipes/symbol-sprite.pipe';

@Component({
  selector: 'app-brain-ring-welcome-page',
  imports: [
    SymbolSpritePipe
  ],
  templateUrl: './brain-ring-welcome-page.component.html',
  styleUrl: './brain-ring-welcome-page.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService],
})
export class BrainRingWelcomePageComponent {

  constructor(private readonly router: Router, private readonly brainRingService: BrainRingService) { }

  joinToRoom(): void {
    this.router.navigate(['/join-to-brain-ring']);
  }

  createRoom(): void {
    this.brainRingService.startBrainRing().subscribe((data)=> {
      console.log(data);
      this.router.navigate(['/brain-ring-lobby'], {
        queryParams: {
          roomId: data.roomId,
          joinCode: data.joinCode,
        }
      });
    })
  }
}
