import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AuthenticationService} from '../../services/authentication.service';
import {AlertService} from '@full-fledged/alerts';
import {Router} from '@angular/router';
import {TokenService} from '../../services/token.service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
      state('loaded', style({
        opacity: 1,
        backgroundColor: "rgba(221, 221, 221, 1)"
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "80px",
        backgroundColor: "rgba(0, 0, 0, 0)"
      })),
      transition('loaded => unloaded', [
        animate('0.5s ease-in')
      ]),
      transition('unloaded => loaded', [
        animate('0.5s ease-in')
      ])
    ]),
  ]
})
export class LoginPageComponent implements OnInit {
  hide = true;
  loaded = false;
  loginField = '';
  passwordField = '';

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private router: Router,
    private tokenService: TokenService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  onLoginClicked(): void {
    this.authenticationService.login(this.loginField, this.passwordField).then(async token => {
      this.alertService.success('You have successfully logged in!');
      this.tokenService.saveToken(token);
      await this.router.navigate(['/home']);
    }).catch(e => {
      this.alertService.danger('Error occurred: ' + e.error.message);
    });
  }
}
