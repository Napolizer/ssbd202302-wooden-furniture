import { AccountRegister } from './account.register';

export interface AccountGoogleRegister extends AccountRegister {
  idToken: string;
}
