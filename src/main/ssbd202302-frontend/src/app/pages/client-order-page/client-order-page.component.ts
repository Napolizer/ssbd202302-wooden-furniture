import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {combineLatest, map, Observable, Subject, takeUntil, timer} from "rxjs";
import {OrderWithProductsDto} from "../../interfaces/order.with.products.dto";
import {ActivatedRoute} from "@angular/router";
import {OrderService} from "../../services/order.service";
import {OrderedProduct} from "../../interfaces/ordered.product";
import {OrderState} from "../../enums/order.state";

@Component({
  selector: 'app-client-order-page',
  templateUrl: './client-order-page.component.html',
  styleUrls: ['./client-order-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state('loaded', style({
        opacity: 1,
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "10px",
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
export class ClientOrderPageComponent implements OnInit, OnDestroy {

  destroy = new Subject<boolean>();
  loading = true;
  orderId: number;
  order: OrderWithProductsDto;
  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
  ) { }

  ngOnInit(): void {

  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

}
