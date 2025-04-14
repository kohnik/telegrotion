import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: '../auth-style.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private readonly router: Router) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]], // Логин (обязательное поле)
      password: ['', [Validators.required]], // Пароль (обязательное поле)
    });
  }

  onSubmit(): void  {
    if (this.loginForm.valid) {
      console.log('Форма отправлена:', this.loginForm.value);
      // Здесь можно отправить данные на сервер
    } else {
      console.log('Форма невалидна');
    }

    this.router.navigate(['/create-quiz'])
  }

  registration(): void {
    this.router.navigate(['/authorization/registration'])
  }

  back(): void {
    this.router.navigate(['/'])
  }
}
