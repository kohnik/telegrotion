import {Component, Input} from '@angular/core';
import {EGameType} from '../../interfaces';

@Component({
  selector: 'app-loader-without-background',
  imports: [],
  templateUrl: './loader-without-background.component.html',
  styleUrl: './loader-without-background.component.scss',
  standalone: true
})
export class LoaderWithoutBackgroundComponent {
  @Input() set gameType(gameType: EGameType) {
    switch (gameType) {
      case EGameType.BrainRing: {
        document.documentElement.style.setProperty('--loader-without-bg-main-text-color', `rgb(61 49 131 / 40%)`);
        document.documentElement.style.setProperty('--loader-without-before-text-color', `#5A4FCF`);
        break;
      }
      case EGameType.Quiz: {
        document.documentElement.style.setProperty('--loader-without-main-text-color', `#4e2d16`);
        document.documentElement.style.setProperty('--loader-without-before-text-color', `#c84e00`);
        break;
      }
    }
  }
}
