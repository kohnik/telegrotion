import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {QuizDataService} from '../quiz.service';
import {QuizLogoComponent} from '../quiz-logo/quiz-logo.component';
import {Subject, takeUntil} from 'rxjs';
import {setLocalStorageUserData} from '../../brain-ring/utils';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';
import {EGameType} from '../../../shared/interfaces';

@Component({
  selector: 'app-quiz-join',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    QuizLogoComponent,
    LoaderComponent
  ],
  templateUrl: './quiz-join.component.html',
  styleUrl: './quiz-join.component.scss',
  standalone: true,
  providers: [QuizDataService]
})
export class QuizJoinComponent implements OnInit, OnDestroy {
  public form: FormGroup
  public errorMessage = '';
  public isAfterQrCodeRedirect = false;
  private destroy$ = new Subject<void>();
  public isLoading = false;

  get joinCode(): string {
    return this.form.get('joinCode')!.value;
  }

  get playerName(): string {
    return this.form.get('playerName')!.value;
  }

  constructor(
    private cdr: ChangeDetectorRef,
    private quizDataService: QuizDataService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,) {
    this.form = this.setForm();
  }

  ngOnInit() {
    let joinCode = this.route.snapshot.queryParams['joinCode'];
    let roomId = this.route.snapshot.queryParams['roomId'];

    if(joinCode ?? roomId) {
      this.isAfterQrCodeRedirect = true;
      return this.form.get('joinCode')?.setValue(joinCode)
    }

    this.form.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(el=> this.errorMessage = '')
  }

  public goToLobby(): void {
    this.isLoading = true;
    this.quizDataService.gotToLobby({
      playerName: this.playerName,
      joinCode: this.joinCode,
    }).subscribe({
      next: (el) => {

        setLocalStorageUserData(el)
        this.router.navigate(['/quiz-game-controller'], {
          queryParams: {
            roomId: el.roomId,
            playerId: el.playerId,
            playerName: el.playerName
          }
        });
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.log(err);
        this.errorMessage = err.error.message;
        this.isLoading = false;

        this.cdr.markForCheck();
      },
      complete: () => {

      }
    })
  }

  public setForm(): FormGroup {
    return this.fb.group({
      joinCode: ['', [Validators.required]],
      playerName: ['', [Validators.required]],
    })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly EGameType = EGameType;
}
