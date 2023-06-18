import { Component, OnInit } from '@angular/core';
import {NavigationService} from "../../services/navigation.service";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-server-error-page',
  templateUrl: './server-error-page.component.html',
  styleUrls: ['./server-error-page.component.sass'],
  animations: [
    trigger('loadedUnloaded', [
      state('loaded', style({
        opacity: 1
      })),
      state('unloaded', style({
        opacity: 0
      })),
      transition('loaded => unloaded', [
        animate('0.2s ease-in')
      ]),
      transition('unloaded => loaded', [
        animate('0.5s ease-in')
      ])
    ]),
  ]
})
export class ServerErrorPageComponent implements OnInit {

  private loaded = false;

  constructor(
    private navigationService: NavigationService
  ) { }

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);
  }

  getAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  redirectToHomePage(): void {
    if (this.loaded) {
      this.loaded = false;
      setTimeout(() => {
        void this.navigationService.redirectToMainPage();
      }, 200);
    }
  }

}
