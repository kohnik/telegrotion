import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {WebSocketService} from '../../../core/services/web-socket.service';
import {BrainRingService} from '../brain-ring.service';
import {brainRingWSTopic} from '../constants';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-brain-ring-join-to',
  imports: [
    ReactiveFormsModule,
    LoaderComponent
  ],
  templateUrl: './brain-ring-join-to.component.html',
  styleUrl: './brain-ring-join-to.component.scss',
  standalone: true,
  providers: [BrainRingService]
})
export class BrainRingJoinToComponent implements OnInit, OnDestroy{
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

  get teamName(): string {
    return this.form.get('teamName')!.value;
  }

  public setForm(): FormGroup {
    return this.fb.group({
      joinCode: ['', [Validators.required]],
      teamName: ['', [Validators.required]],
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
      teamName: this.teamName,
      joinCode: this.joinCode,
    }).subscribe({
      next: (el) => {
        this.router.navigate(['/brain-ring-controller'], {
          queryParams: {
            roomId: el.roomId,
            teamId: el.teamId
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
