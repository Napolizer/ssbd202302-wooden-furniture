import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Account} from "../../interfaces/account";
import {MatTableDataSource} from "@angular/material/table";
import {Subject, takeUntil, tap} from "rxjs";
import {ClientOrder} from "../../interfaces/client.order";
import {AccountService} from "../../services/account.service";
import {NavigationService} from "../../services/navigation.service";
import {BreadcrumbsService} from "../../services/breadcrumbs.service";
import {TranslateService} from "@ngx-translate/core";
import {NavigationEnd, Router} from "@angular/router";
import {DialogService} from "../../services/dialog.service";
import {ClientOrderService} from "../../services/client-order.service";

@Component({
  selector: 'app-client-orders-page',
  templateUrl: './client-orders-page.component.html',
  styleUrls: ['./client-orders-page.component.sass'],
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
export class ClientOrdersPageComponent implements OnInit, OnDestroy {

  orders: ClientOrder[] = [];
  loading = true;
  listLoading = false;
  displayedColumns = ['id', 'productsNumber', 'totalPrice', 'orderState', 'show', 'observe'];
  dataSource = new MatTableDataSource<ClientOrder>(this.orders);
  destroy = new Subject<boolean>();

  constructor(
    private orderService: ClientOrderService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private translate: TranslateService,
    private router: Router,
    private dialogService: DialogService
  ) { }

  ngOnInit(): void {
    this.router.events
      .pipe(takeUntil(this.destroy))
      .subscribe((val) => {
        if (val instanceof NavigationEnd) {
          this.loading = true;
        }
      });
    this.orderService.getAllClientOrders()
      .pipe(tap(() => this.loading = true), takeUntil(this.destroy))
      .subscribe(orders => {
        console.log(orders)
        this.orders = orders;
        this.loading = false;
        this.dataSource = new MatTableDataSource<ClientOrder>(this.orders);
      })
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onBackClicked(): void {
    void this.navigationService.redirectToMainPage();
  }

  onResetClicked(): void {
    this.listLoading = true;
    this.orderService.getAllClientOrders()
      .subscribe(orders => {
        this.orders = orders;
        this.dataSource.data = this.orders;
        this.listLoading = false;
      });
  }

  shouldDisplayTable(): boolean {
    return this.orders.length > 0;
  }

  shouldDisplaySpinner(): boolean {
    return this.loading || this.listLoading;
  }

  showOrder(): void {

  }

  observeOrder(): void {

  }

  canObserve(order: any): boolean {
    return (order.orderState == 'CREATED' || order.orderState == 'IN_DELIVERY') && !order.observed;
  }

  calculateTotalQuantity(order: any): number {
    let total = 0;
    for (const product of order.orderProductList) {
      total += product.amount;
    }
    return total;
  }
}
