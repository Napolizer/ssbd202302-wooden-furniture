import {Component, OnInit, ViewChild} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {combineLatest, first, map, Subject, takeUntil, tap, timer,} from "rxjs";
import {MatTableDataSource} from "@angular/material/table";
import {OrderStats} from "../../interfaces/order.stats";
import {OrderService} from "../../services/order.service";
import {ActivatedRoute} from "@angular/router";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {TranslateService} from "@ngx-translate/core";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-order-stats-page',
  templateUrl: './order-stats-page.component.html',
  styleUrls: ['./order-stats-page.component.sass'],
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
export class OrderStatsPageComponent implements OnInit {
  loading = true;
  destroy = new Subject<boolean>();
  orderStats: OrderStats[] = []
  dataSource = new MatTableDataSource<OrderStats>(this.orderStats);
  displayedColumns = ['productGroupName', 'averageRating', 'amount', 'soldAmount', 'totalIncome'];

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @ViewChild(MatSort)
  sort: MatSort;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private translate: TranslateService,
    private datePipe: DatePipe,
  ) { }

  ngOnInit(): void {
    this.orderService.getOrderStats(
      this.datePipe
        .transform(
          new Date(this.route.snapshot.paramMap.get('from')!),
          'MM/dd/yyyy'
        )?.toString() as string,
      this.datePipe
        .transform(
          new Date(this.route.snapshot.paramMap.get('to')!),
          'MM/dd/yyyy'
        )?.toString() as string
    ).pipe(tap(() => this.loading = true), takeUntil(this.destroy))
      .subscribe( (orderStats) => {
        this.orderStats = orderStats;
        this.loading = false;
        this.dataSource = new MatTableDataSource<OrderStats>(this.orderStats);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    this.initPaginator();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onResetClicked(): void {
    this.loading = true;
    this.orderService.getOrderStats(
      this.datePipe
        .transform(
          new Date(this.route.snapshot.paramMap.get('from')!),
          'MM/dd/yyyy'
        )?.toString() as string,
      this.datePipe
        .transform(
          new Date(this.route.snapshot.paramMap.get('to')!),
          'MM/dd/yyyy'
        )?.toString() as string
      ).pipe(tap(() => this.loading = true), takeUntil(this.destroy))
      .subscribe( (orderStats) => {
        this.orderStats = orderStats;
        this.dataSource.data = this.orderStats;
        this.loading = false;
      });
  }

  shouldDisplayTable(): boolean {
    return this.orderStats.length > 0;
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

  onSortClicked(event: Sort) {
    this.dataSource.data = this.dataSource.data.sort((a: OrderStats, b: OrderStats) => {
      const isAsc = event.direction === 'asc';
      const field = event.active;
      return this.compare(a[field], b[field], isAsc);
    })
  }

  compare(a: string | number | boolean, b: string | number | boolean, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }
}
