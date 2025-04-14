import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import { trigger, state, style, animate, transition } from '@angular/animations';
import {NgIf} from '@angular/common';

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
        animate('500ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('3 => 2', [
        animate('500ms ease-out', style({ transform: 'scale(0.5)', opacity: 0 })),
        animate('500ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('2 => 1', [
        animate('500ms ease-out', style({ transform: 'scale(0.5)', opacity: 0 })),
        animate('500ms ease-out', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('1 => void', [
        animate('500ms ease-out', style({ transform: 'scale(0.5)', opacity: 0 }))
      ])
    ])
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true
})
export class PreGameTimerComponent implements OnInit {
  showTimer = false;
  currentNumber: number | null = null;
  private colors = ['#FF5733', '#33FF57', '#3357FF', '#F3FF33', '#FF33F5'];

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
      if (this.currentNumber && this.currentNumber > 1) {
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

  getRandomColor() {
    return this.colors[Math.floor(Math.random() * this.colors.length)];
  }
}
