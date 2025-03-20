import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-registration',
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './registration.component.html',
  standalone: true,
  styleUrl: '../auth-style.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegistrationComponent {
  registrationForm: FormGroup;

  constructor(private fb: FormBuilder, private readonly router: Router) {
    this.registrationForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
    }, { validator: this.passwordMatchValidator });
  }

  // Валидатор для проверки совпадения паролей
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { mismatch: true };
  }

  onSubmit() {
    if (this.registrationForm.valid) {
      console.log('Форма отправлена:', this.registrationForm.value);
      // Здесь можно отправить данные на сервер
    } else {
      console.log('Форма невалидна');
    }
  }

  login(): void {
    this.router.navigate(['/authorization/login'])
  }

  back(): void {
    this.router.navigate(['/'])
  }
}
