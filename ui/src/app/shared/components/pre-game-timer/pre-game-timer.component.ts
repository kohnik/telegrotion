import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import { trigger, state, style, animate, transition } from '@angular/animations';
import {NgIf} from '@angular/common';
import {EGameType} from '../../interfaces';

@Component({
  selector: 'app-pre-game-timer',
  templateUrl: './pre-game-timer.component.html',
  styleUrl: './pre-game-timer.component.scss',
  imports: [
    NgIf
  ],
  animations: [
    trigger('timerAnimation', [
      state('3', style({
        transform: 'scale(1)',
        opacity: 1
      })),
      state('2', style({
        transform: 'scale(1)',
        opacity: 1
      })),
      state('1', style({
        transform: 'scale(1)',
        opacity: 1
      })),
      state('void', style({
        transform: 'scale(0.5)',
        opacity: 0
      })),
      transition('void => 3', [
        animate('300ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('3 => 2', [
        animate('300ms ease-out', style({ transform: 'scale(0.9)', opacity: 0.2 })),
        animate('300ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('2 => 1', [
        animate('300ms ease-out', style({ transform: 'scale(0.9)', opacity: 0.2 })),
        animate('300ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('1 => 0', [
        animate('300ms ease-out', style({ transform: 'scale(0.9)', opacity: 0.2 })),
        animate('300ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('1 => void', [
        animate('300ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ])
    ])
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true
})
export class PreGameTimerComponent implements OnInit {
  showTimer = false;
  currentNumber:number | null = null;

  @Input() set gameType(gameType: EGameType) {
    switch (gameType) {
      case EGameType.Quiz: {
        document.documentElement.style.setProperty('--timer-background', `linear-gradient(135deg, #30316b, #4c6cee)`);
        break;
      }
      case EGameType.BrainRing: {
        document.documentElement.style.setProperty('--timer-background', `linear-gradient(to bottom, #f9a825, #ef6c00)`);
        break;
      }
    }
  }

  @Output() timerEnd = new EventEmitter();

  constructor(private cdr: ChangeDetectorRef) {
  }

  ngOnInit() {
    setTimeout(() => {
      this.startCountdown();
      this.cdr.markForCheck();
    }, 1000);
  }

  startCountdown() {
    this.showTimer = true;
    this.currentNumber = 3;

    console.log(this.showTimer)

    const countdown = setInterval(() => {
      if (this.currentNumber && this.currentNumber >= 0) {
        this.currentNumber--;
      } else {
        clearInterval(countdown);
        setTimeout(() => {
          this.showTimer = false;
          this.currentNumber = null;

          this.timerEnd.emit();
          this.cdr.markForCheck();
        }, 1000);
      }
      this.cdr.markForCheck();
    }, 1000);
  }
}
