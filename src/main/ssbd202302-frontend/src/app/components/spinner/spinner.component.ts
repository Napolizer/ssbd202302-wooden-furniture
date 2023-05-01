import {Component, Input, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.sass'],
  animations: [
    trigger('loadedUnloadedSpinner', [
      state('loaded', style({
        opacity: 1
      })),
      state('unloaded', style({
        opacity: 0,
        'display': 'none'
      })),
      transition('loaded => unloaded', [
        animate('0.6s ease-in')
      ]),
      transition('unloaded => loaded', [
        animate('0.6s ease-in')
      ])
    ]),
  ]
})
export class SpinnerComponent implements OnInit {
  @Input()
  loading = true;

  constructor() { }

  ngOnInit(): void {
  }

  getFormAnimationState(): string {
    return this.loading ? 'loaded' : 'unloaded';
  }
}
