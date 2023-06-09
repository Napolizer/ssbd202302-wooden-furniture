import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Subject} from "rxjs";
import {DialogService} from "../../services/dialog.service";
import {AlertService} from "@full-fledged/alerts";
import {NavigationService} from "../../services/navigation.service";

@Component({
  selector: 'app-client-page',
  templateUrl: './client-page.component.html',
  styleUrls: ['./client-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state('loaded', style({
        opacity: 1,
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "20px",
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
export class ClientPageComponent implements OnInit {
  loaded = false;
  destroy = new Subject<boolean>();

  constructor(
    private dialogService : DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService
  ) { }

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

  redirectToClientOrdersPage(): void {
    void this.navigationService.redirectToClientOrdersPage();
  }

}
