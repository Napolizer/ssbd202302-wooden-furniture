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
    this.route.paramMap.subscribe(params => {
      this.orderId = Number(params.get('id'));
      this.getOrder()
        .subscribe(order => {
          this.order = order;
          this.loading = false;
        });
    })
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getOrder(): Observable<OrderWithProductsDto> {
    return this.orderService.getOrderAsClient(this.orderId);
  }

  getProducts(): OrderedProduct[] {
    return this.order?.orderedProducts ?? [];
  }

  getRecipientFullName(): string {
    return this.order?.recipientFirstName + ' ' + this.order?.recipientLastName;
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  getProgress(): number {
    if (this.order.orderState === undefined) return 0;
    switch (this.order.orderState) {
      case OrderState.CREATED:
        return 5;
      case OrderState.COMPLETED:
        return 35;
      case OrderState.IN_DELIVERY:
        return 75;
      default:
        return 100;
    }
  }

  getProgressStyle(): any {
    return {
      'width': this.getProgress() + '%',
      ...(this.order?.orderState === OrderState.CANCELLED && {'background-color': 'red !important'}),
      ...(this.order?.orderState === OrderState.DELIVERED && {'background-color': 'green !important'})
    }
  }

  round(num: number): string {
    return String(+parseFloat(String(num)).toFixed(2));
  }

  onResetClicked(): void {
    this.loading = true;
    combineLatest([
      this.getOrder(),
      timer(1000)
    ])
      .pipe(takeUntil(this.destroy), map(obj => obj[0]))
      .subscribe(order => {
        this.order = order;
        this.loading = false;
      });
  }

}
