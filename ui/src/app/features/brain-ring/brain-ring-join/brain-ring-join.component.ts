import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {BrainRingService} from '../brain-ring.service';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';
import {setLocalStorageUserData} from '../utils';

@Component({
  selector: 'app-brain-ring-join',
  imports: [
    ReactiveFormsModule,
    LoaderComponent
  ],
  templateUrl: './brain-ring-join.component.html',
  styleUrl: './brain-ring-join.component.scss',
  standalone: true,
  providers: [BrainRingService]
})
export class BrainRingJoinComponent implements OnInit, OnDestroy{
  public form: FormGroup
  public isLoading = false;
  public isAfterQrCodeRedirect = false;
  public errorMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder,
    private brainRingService: BrainRingService,) {
    this.form = this.setForm();
  }

  private destroy$ = new Subject<void>();

  get joinCode(): string {
    return this.form.get('joinCode')!.value;
  }

  get playerName(): string {
    return this.form.get('playerName')!.value;
  }

  public setForm(): FormGroup {
    return this.fb.group({
      joinCode: ['', [Validators.required]],
      playerName: ['', [Validators.required]],
    })
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

  public goToRoom(): void {
    this.isLoading = true;
    this.brainRingService.goToRoom({
      playerName: this.playerName,
      joinCode: this.joinCode,
    }).subscribe({
      next: (el) => {

        setLocalStorageUserData(el)
        this.router.navigate(['/brain-ring-controller'], {
          queryParams: {
            roomId: el.roomId,
            playerId: el.playerId
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

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
