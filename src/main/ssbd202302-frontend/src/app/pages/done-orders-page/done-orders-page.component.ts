import {
  Component,
  OnInit,
  OnDestroy,
} from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { merge, Observable, of as observableOf, of, Subject } from 'rxjs';
import {
  catchError,
  first,
  map,
  max,
  startWith,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import { MatTableDataSource} from '@angular/material/table';
import { OrderService } from 'src/app/services/order.service';
import { OrderDetailsDto } from 'src/app/interfaces/order.details.dto';
import {
  trigger,
  state,
  style,
  transition,
  animate,
} from '@angular/animations';
import { Router, NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-done-orders-page',
  templateUrl: './done-orders-page.component.html',
  styleUrls: ['./done-orders-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state(
        'loaded',
        style({
          opacity: 1,
          backgroundColor: 'rgba(221, 221, 221, 1)',
        })
      ),
      state(
        'unloaded',
        style({
          opacity: 0,
          paddingTop: '80px',
          backgroundColor: 'rgba(0, 0, 0, 0)',
        })
      ),
      transition('loaded => unloaded', [animate('0.5s ease-in')]),
      transition('unloaded => loaded', [animate('0.5s ease-in')]),
    ]),
  ],
})
export class DoneOrdersPageComponent implements OnInit, OnDestroy {
  orders: OrderDetailsDto[] = [];
  loading = true;
  listLoading = false;
  displayedColumns = ['id', 'accountId', 'productsNumber', 'totalPrice'];
  dataSource = new MatTableDataSource<OrderDetailsDto>(this.orders);
  destroy = new Subject<boolean>();
  minPrice: number;
  maxPrice: number;
  amount: number;
  maxAmount: number;
  maxTotalPrice: number;
  formGroup: FormGroup;
  maxSliderValue: number;
  minSliderValue: number = 0;
  amountSliderValue: number;
  amounts: number[] = [];

  constructor(
    private orderService: OrderService,
    private navigationService: NavigationService,
    private translate: TranslateService,
    private router: Router,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.router.events.pipe(takeUntil(this.destroy)).subscribe((val) => {
      if (val instanceof NavigationEnd) {
        this.loading = true;
      }
    });
    this.orderService
      .getDoneOrders()
      .pipe(
        tap(() => (this.loading = true)),
        takeUntil(this.destroy)
      )
      .subscribe((orders) => {
        console.log(orders);
        this.orders = orders;
        for(let i=0;i<this.orders.length;i++) {
          this.amounts[i] = this.calculateMaxAmount(this.orders[i]);
      }
      this.maxAmount = Math.max(...this.amounts);
      this.amountSliderValue = this.maxAmount;
      this.maxSliderValue = this.maxTotalPrice = this.orders.reduce((max, element) => {
        return element.totalPrice > max ? element.totalPrice : max;
      }, Number.MIN_VALUE);

        this.loading = false;
        this.dataSource = new MatTableDataSource<OrderDetailsDto>(this.orders);
      });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  validateNumber(event: KeyboardEvent): void {
    const input = event.key;
    const regex = /[0-9]|\./; // Regular expression to match numbers

    if (!regex.test(input) && event.key !== 'Backspace') {
      event.preventDefault();
    }
  }

  isNumberValid(): boolean {
    return !isNaN(this.minPrice);
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onBackClicked(): void {
    void this.navigationService.redirectToMainPage();
  }

  handleApplyFiltersButton(): void {
    this.loading=true;
    this.listLoading = true;
      this.orderService
        .getDoneFilterOrders(this.minSliderValue, this.maxSliderValue, this.amountSliderValue)
        .subscribe((orders) => {
          this.orders = orders;
          this.dataSource.data = this.orders;
          this.listLoading = false;
          this.loading=false;
        });
  }

  onResetClicked(): void {
      this.listLoading = true;
      this.orderService
        .getDoneOrders()
        .subscribe((orders) => {
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

  showOrder(): void {}

  observeOrder(): void {}

  calculateTotalQuantity(order: any): number {
    let total = 0;
    for (const product of order.orderedProducts) {
      total += product.amount;
    }
    return total;
  }

  openDisplayStatsDialog(): void {
    this.dialogService.openDisplayStatsDialog();
  }

  calculateMaxAmount(order: OrderDetailsDto): number {
    let maxAmount=0;
    let total = 0;
    for(let i=0;i<order.orderedProducts.length;i++) {
      total+=order.orderedProducts[i].amount;
    }
    if(maxAmount<total) {
      maxAmount=total;
    }
    return maxAmount
  }
}
