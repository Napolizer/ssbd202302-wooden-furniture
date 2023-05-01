import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AuthenticationService} from '../../services/authentication.service';
import {AlertService} from '@full-fledged/alerts';
import {Router} from '@angular/router';
import {TokenService} from '../../services/token.service';
import {combineLatest, map, Subject, takeUntil} from 'rxjs';
import {FormControl, FormGroup} from '@angular/forms';
import {TranslateService} from "@ngx-translate/core";

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
export class LoginPageComponent implements OnInit, OnDestroy {
  hide = true;
  loaded = false;
  loginForm = new FormGroup({
    login: new FormControl(''),
    password: new FormControl('')
  });
  destroy = new Subject<boolean>();

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private router: Router,
    private tokenService: TokenService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  onLoginClicked(): void {
    this.authenticationService.login(this.loginForm.value['login'] ?? '', this.loginForm.value['password'] ?? '')
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: token => {
          this.tokenService.saveToken(token);

          this.translate.get('login.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              void this.router.navigate(['/home']);
            });
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              this.alertService.danger(`${data.title}: ${data.message}`);
            });
        }
      })
  }
}
