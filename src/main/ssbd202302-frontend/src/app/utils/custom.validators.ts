import { AbstractControl } from '@angular/forms';

export class CustomValidators {
  static MatchPasswords(control: AbstractControl) {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      control.get('confirmPassword')?.setErrors({ notmatching: true });
      return { notmatching: true };
    } else {
      return null;
    }
  }

  static MatchNewPasswords(control: AbstractControl) {
    const newPassword = control.get('newPassword')?.value;
    const confirmNewPassword = control.get('confirmPassword')?.value;

    if (newPassword !== confirmNewPassword) {
      control.get('confirmPassword')?.setErrors({ notmatching: true });
      return { notmatching: true };
    } else {
      return null;
    }
  }

  static PasswordsMatch(control: AbstractControl) {
    const password = control.get('currentPassword')?.value;
    const newPassword = control.get('newPassword')?.value;

    if (password === newPassword) {
      control.get('newPassword')?.setErrors({ matching: true });
      return { matching: true };
    } else {
      return null;
    }
  }

  static EmailsCannotMatch(control: AbstractControl) {
    const currentEmail = control.get('currentEmail')?.value;
    const newEmail = control.get('newEmail')?.value;

    if (currentEmail === newEmail) {
      control.get('newEmail')?.setErrors({ matching: true });
      return { matching: true };
    } else {
      return null;
    }
  }
}
