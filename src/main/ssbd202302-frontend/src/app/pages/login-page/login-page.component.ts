import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
      state('loaded', style({
        opacity: 1
      })),
      state('unloaded', style({
        opacity: 0
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

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }
}
