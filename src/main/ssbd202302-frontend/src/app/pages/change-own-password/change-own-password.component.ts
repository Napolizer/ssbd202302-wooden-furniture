import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Subject} from "rxjs";
import {AccountService} from "../../services/account.service";
import {TranslateService} from "@ngx-translate/core";
import {AuthenticationService} from "../../services/authentication.service";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {AlertService} from "@full-fledged/alerts";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-change-own-password',
  templateUrl: './change-own-password.component.html',
  styleUrls: ['./change-own-password.component.sass'],
  // animations: [
  //   trigger('loadedUnloadedForm', [
  //     state(
  //       'loaded',
  //       style({
  //         opacity: 1,
  //         backgroundColor: 'rgba(221, 221, 221, 1)',
  //       })
  //     ),
  //     state(
  //       'unloaded',
  //       style({
  //         opacity: 0,
  //         paddingTop: '80px',
  //         backgroundColor: 'rgba(0, 0, 0, 0)',
  //       })
  //     ),
  //     transition('loaded => unloaded', [animate('0.5s ease-in')]),
  //     transition('unloaded => loaded', [animate('0.5s ease-in')]),
  //   ]),
  // ],
})
export class ChangeOwnPasswordComponent implements OnInit {

  destroy = new Subject<boolean>();
  loading = true;
  currentPassword: string;
  newPassword: string;
  changePasswordForm: FormGroup;
  constructor(
    private accountService: AccountService,
    private translate: TranslateService,
    private authenticationService: AuthenticationService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.changePasswordForm = new FormGroup({
      currentPassword: new FormControl(''),
      // change validator for password
      newPassword: new FormControl('', Validators.compose([Validators.min(8)])),
    },

      )
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onBackClicked(): void {
    this.navigationService.redirectToMainPage();
  }

  onPasswordChangeClicked() {

  }


}
