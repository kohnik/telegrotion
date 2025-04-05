import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ICurrentSlide} from '../../interfaces';

@Component({
  selector: 'app-game-window-slide',
  imports: [],
  templateUrl: './game-window-slide.component.html',
  styleUrl: './game-window-slide.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GameWindowSlideComponent implements OnInit {

  public timeLeft = 0

  @Input() slide: ICurrentSlide

  constructor(private cdr: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.timeLeft = this.slide.timeLeft
    let timer = setInterval(() => {
      if(this.timeLeft === 0 )
      {
        clearInterval(timer);
        return;
      }
      this.timeLeft -= 1;
      this.cdr.markForCheck()
    },1000)
  }
}
