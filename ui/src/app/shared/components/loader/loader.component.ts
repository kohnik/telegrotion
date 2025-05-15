import {Component, Input} from '@angular/core';
import {EGameType} from '../../interfaces';

@Component({
  selector: 'app-loader',
  imports: [],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.scss',
  standalone: true
})
export class LoaderComponent {
  @Input() set gameType(gameType: EGameType) {
    switch (gameType) {
      case EGameType.Quiz: {
        document.documentElement.style.setProperty('--loader-main-text-color', `rgb(61 49 131 / 40%)`);
        document.documentElement.style.setProperty('--loader-before-text-color', `#5A4FCF`);
        document.documentElement.style.setProperty('--loader-background', `linear-gradient(135deg, #30316b, #4c6cee)`);
        break;
      }
      case EGameType.BrainRing: {
        document.documentElement.style.setProperty('--loader-main-text-color', `#4e2d16`);
        document.documentElement.style.setProperty('--loader-before-text-color', `#c84e00`);
        document.documentElement.style.setProperty('--loader-background', `linear-gradient(to bottom, #f9a825, #ef6c00)`);
        break;
      }
    }
  }
}
