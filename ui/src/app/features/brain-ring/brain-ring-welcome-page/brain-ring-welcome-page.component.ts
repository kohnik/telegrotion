import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Router} from '@angular/router';
import {BrainRingService} from '../brain-ring.service';
import {SymbolSpritePipe} from '../../../shared/pipes/symbol-sprite.pipe';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-brain-ring-welcome-page',
  imports: [
    SymbolSpritePipe,
    LoaderComponent
  ],
  templateUrl: './brain-ring-welcome-page.component.html',
  styleUrl: './brain-ring-welcome-page.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService],
})
export class BrainRingWelcomePageComponent {

  public isLoading = false;

  constructor(private readonly router: Router, private readonly brainRingService: BrainRingService) { }

  joinToRoom(): void {
    this.router.navigate(['/brain-ring-join-to']);
  }

  createRoom(): void {
    this.isLoading = true
    this.brainRingService.startBrainRing().subscribe((data)=> {
      console.log(data);
      this.router.navigate(['/brain-ring-lobby'], {
        queryParams: {
          roomId: data.roomId,
          joinCode: data.joinCode,
        }
      });

      this.isLoading = false;
    })
  }

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }
}
