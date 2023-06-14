import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {MatTableDataSource} from "@angular/material/table";
import {first, Subject, takeUntil, tap} from "rxjs";
import {ClientOrder} from "../../interfaces/client.order";
import {AccountService} from "../../services/account.service";
import {NavigationService} from "../../services/navigation.service";
import {BreadcrumbsService} from "../../services/breadcrumbs.service";
import {TranslateService} from "@ngx-translate/core";
import {NavigationEnd, Router} from "@angular/router";
import {DialogService} from "../../services/dialog.service";
import {ClientOrderService} from "../../services/client-order.service";
import {OrderService} from "../../services/order.service";
import {AlertService} from "@full-fledged/alerts";
import {HttpErrorResponse} from "@angular/common/http";

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
  displayedColumns = ['id', 'productsNumber', 'totalPrice', 'orderState', 'show', 'observe', 'cancel'];
  dataSource = new MatTableDataSource<ClientOrder>(this.orders);
  destroy = new Subject<boolean>();

  constructor(
    private clientOrderService: ClientOrderService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private translate: TranslateService,
    private router: Router,
    private dialogService: DialogService,
    private orderService: OrderService,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.router.events
      .pipe(takeUntil(this.destroy))
      .subscribe((val) => {
        if (val instanceof NavigationEnd) {
          this.loading = true;
        }
      });
    this.clientOrderService.getAllClientOrders()
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
    this.clientOrderService.getAllClientOrders()
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

  canCancel(order: ClientOrder): boolean {
    return order.orderState == 'CREATED';
  }

  calculateTotalQuantity(order: any): number {
    let total = 0;
    for (const product of order.orderedProducts) {
      total += product.amount;
    }
    return total;
  }

  redirectToRatePage() {
    this.navigationService.redirectToRatePage();
  }

  onCancelClicked(order: ClientOrder) {
        this.translate.get("order.cancel.confirmation")
          .pipe(takeUntil(this.destroy))
          .subscribe(msg => {
            const ref = this.dialogService.openConfirmationDialog(msg, "primary")
            ref
              .afterClosed()
              .pipe(first(), takeUntil(this.destroy))
              .subscribe((result) => {
                if (result == 'action') {
                  this.cancelOrder(order);
                }
              });
          })
  }

  cancelOrder(order: ClientOrder) {
    console.log(order)
    this.loading = true;
    this.orderService.cancelOrder(order)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false
          this.translate.get("order.cancel.success")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              void this.navigationService.redirectToClientOrdersPage();
            })
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          const message = e.error.message as string;
          this.translate
            .get(message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
            })
        }
      })
  }

  // observeOrder(): void {
  //   observeOrder(order: ClientOrder): void {
  //     this.loading = true;
  //     this.orderService.observeOrder(order)
  //       .pipe(takeUntil(this.destroy))
  //       .subscribe( {
  //         next: () => {
  //           this.loading = false
  //           this.translate.get("order.observe.success")
  //             .pipe(takeUntil(this.destroy))
  //             .subscribe(msg => {
  //               this.alertService.success(msg);
  //               void this.navigationService.redirectToClientOrdersPage();
  //             })
  //         },
  //         error: (e: HttpErrorResponse) => {
  //           this.loading = false;
  //           const message = e.error.message as string;
  //           this.translate
  //             .get(message || 'exception.unknown')
  //             .pipe(takeUntil(this.destroy))
  //             .subscribe(msg => {
  //               this.alertService.danger(msg);
  //             })
  //         }
  //       })
  //   }
  //
  //   onObserveClicked(order: ClientOrder) {
  //     this.translate.get("order.observe.confirmation")
  //       .pipe(takeUntil(this.destroy))
  //       .subscribe(msg => {
  //         const ref = this.dialogService.openConfirmationDialog(msg, "primary")
  //         ref
  //           .afterClosed()
  //           .pipe(first(), takeUntil(this.destroy))
  //           .subscribe((result) => {
  //             if (result == 'action') {
  //               this.observeOrder(order);
  //             }
  //           });
  //       })
  //   }
}
