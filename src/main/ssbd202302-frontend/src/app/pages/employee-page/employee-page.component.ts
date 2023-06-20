import { animate, state, style, transition, trigger } from '@angular/animations';
import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Subject, first, takeUntil, tap, combineLatest, timer, map} from 'rxjs';
import { DialogService } from 'src/app/services/dialog.service';
import {OrderDetailsDto} from "../../interfaces/order.details.dto";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {MatRipple} from "@angular/material/core";
import {OrderService} from "../../services/order.service";
import {NavigationService} from "../../services/navigation.service";
import {BreadcrumbsService} from "../../services/breadcrumbs.service";
import {TranslateService} from "@ngx-translate/core";
import {NavigationEnd, Router} from "@angular/router";

@Component({
  selector: 'app-employee-page',
  templateUrl: './employee-page.component.html',
  styleUrls: ['./employee-page.component.sass'],
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
export class EmployeePageComponent implements OnInit {
  loadedOrders: OrderDetailsDto[] = [];
  orders: OrderDetailsDto[] = [];
  loading = true;
  listLoading = false;
  searchPhrase: string = '';
  fullNames: string[] = [];
  displayedColumns = ['id', 'orderState', 'recipient', 'address', 'productsAmount',  'accountLogin', 'totalPrice', 'show', 'action'];
  dataSource = new MatTableDataSource<OrderDetailsDto>(this.orders);
  destroy = new Subject<boolean>();

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @ViewChild(ElementRef)
  searchInput: ElementRef;

  @ViewChild(MatAutocompleteTrigger)
  autocomplete: MatAutocompleteTrigger;

  @ViewChild(MatRipple)
  ripple: MatRipple;

  constructor(
    private orderService: OrderService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private translate: TranslateService,
    private router: Router,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.router.events
      .pipe(takeUntil(this.destroy))
      .subscribe((val) => {
        if (val instanceof NavigationEnd) {
          this.loading = true;
        }
      });
    this.orderService.getOrders()
      .pipe(tap(() => this.loading = true), takeUntil(this.destroy))
      .subscribe(orders => {
        this.orders = orders;
        this.loadedOrders = orders;
        this.loading = false;
        this.dataSource = new MatTableDataSource<OrderDetailsDto>(this.orders);
        this.dataSource.paginator = this.paginator;
      });
    this.initPaginator();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  initPaginator(): void {
    this.paginator._intl.firstPageLabel = this.translate.instant('paginator.firstPageLabel');
    this.paginator._intl.lastPageLabel = this.translate.instant('paginator.lastPageLabel');
    this.translate.get('paginator.ordersPerPage').subscribe((res: string) => {
      this.paginator._intl.itemsPerPageLabel = res;
    });
    this.paginator._intl.nextPageLabel = this.translate.instant('paginator.nextPageLabel');
    this.paginator._intl.previousPageLabel = this.translate.instant('paginator.previousPageLabel');
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  redirectToOrderPage(order: OrderDetailsDto): void {
    void this.navigationService.redirectToEmployeeOrdersPage(order.id);
  }

  onBackClicked(): void {
    void this.navigationService.redirectToMainPage();
  }

  onSearchClicked(): void {
    this.orders = this.loadedOrders.filter(order =>
      order.recipientFirstName.toLowerCase().includes(this.searchPhrase.toLowerCase())
      || order.recipientLastName.toLowerCase().includes(this.searchPhrase.toLowerCase())
    );
    this.dataSource.data = this.orders;
  }

  onResetClicked(): void {
    this.listLoading = true;
    this.searchPhrase = '';
    combineLatest([
      this.orderService.getOrders(),
      timer(1000)
    ])
      .pipe(takeUntil(this.destroy), map(obj => obj[0]))
      .subscribe(orders => {
        this.orders = orders;
        this.loadedOrders = orders;
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

  getFormAnimationState(): string {
    return this.loading ? 'loaded' : 'unloaded';
  }

  openAddProductGroupDialog(): void {
    this.dialogService.openAddProductGroupDialog()
    .afterClosed()
    .pipe(first(), takeUntil(this.destroy))
    .subscribe((result) => {
      if (result === 'success') {
        // refresh list if there is any list
      }
    });
  }


  openAddProductDialog(): void {
    this.dialogService.openAddProductDialog()
    .afterClosed()
    .pipe(first(), takeUntil(this.destroy))
    .subscribe((result) => {
      if (result === 'success') {
        // refresh list if there is any list
      }
    });
  }

  openArchiveProductGroupDialog(): void {
    this.dialogService.openArchiveProductGroupDialog()
      .afterClosed()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe((result) => {
        if (result === 'success') {

        }
      })
  }

  openActivateProductGroupDialog(): void {
    this.dialogService.openActivateProductGroupDialog()
      .afterClosed()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe((result) => {
        if (result === 'success') {

        }
      });
  }

  openEditProductGroupNameDialog(): void {
    this.dialogService.openEditProductGroupNameDialog()
      .afterClosed()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe((result) => {
        if (result === 'success') {

        }
      })
  }

  getRecipientName(order: OrderDetailsDto): string {
    return `${order.recipientFirstName} ${order.recipientLastName}`;
  }

  getAddress(order: OrderDetailsDto): string {
    const address = order.recipientAddress;
    return `${address.country} ${address.city} ${address.street} ${address.streetNumber}`;
  }

  getProductsAmount(order: OrderDetailsDto): number {
    let sum = 0;
    for (const product of order.orderedProducts) {
      sum += product.amount;
    }
    return sum;
  }

  round(num: number): string {
    return String(+parseFloat(String(num)).toFixed(2));
  }
}
