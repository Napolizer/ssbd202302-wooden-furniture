import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {ProductHistoryDto} from "../../interfaces/product.history.dto";
import {MatTableDataSource} from "@angular/material/table";
import {combineLatest, map, Subject, takeUntil, tap, timer} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {MatRipple} from "@angular/material/core";
import {NavigationService} from "../../services/navigation.service";
import {BreadcrumbsService} from "../../services/breadcrumbs.service";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute} from "@angular/router";
import {ProductService} from "../../services/product.service";
import {ProductField} from "../../enums/product.field";

@Component({
  selector: 'app-product-edition-history-page',
  templateUrl: './product-edition-history-page.component.html',
  styleUrls: ['./product-edition-history-page.component.sass'],
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
  ],
})
export class ProductEditionHistoryPageComponent implements OnInit {
  productHistoryList: ProductHistoryDto[] = [];
  productId: number;
  loading = true;
  listLoading = false;
  displayedColumns = ['fieldName', 'oldValue', 'newValue', 'editDate', 'editedBy'];
  dataSource = new MatTableDataSource<ProductHistoryDto>(this.productHistoryList);
  destroy = new Subject<boolean>();

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @ViewChild(MatSort)
  sort: MatSort;

  @ViewChild(ElementRef)
  searchInput: ElementRef;

  @ViewChild(MatRipple)
  ripple: MatRipple;

  constructor(
    private productService: ProductService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private translate: TranslateService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.productId = Number(params.get('id'));
      this.productService
        .findProductHistory(this.productId)
        .pipe(tap(() => this.loading = true), takeUntil(this.destroy))
        .subscribe(productHistoryList => {
          this.productHistoryList = productHistoryList;
          this.loading = false;
          this.dataSource = new MatTableDataSource<ProductHistoryDto>(this.productHistoryList);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        });
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
    this.translate.get('paginator.elementsPerPage').subscribe((res: string) => {
      this.paginator._intl.itemsPerPageLabel = res;
    });
    this.paginator._intl.nextPageLabel = this.translate.instant('paginator.nextPageLabel');
    this.paginator._intl.previousPageLabel = this.translate.instant('paginator.previousPageLabel');
  }

  compare(a: string | number | boolean, b: string | number | boolean, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  onSortClicked(event: Sort) {
    this.dataSource.data = this.dataSource.data.sort((a: ProductHistoryDto, b: ProductHistoryDto) => {
      const isAsc = event.direction === 'asc';
      const field = event.active;
      return this.compare(a[field], b[field], isAsc);
    });
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onBackClicked(): void {
    void this.navigationService.redirectToMainPage();
  }

  onResetClicked(): void {
    this.listLoading = true;
    combineLatest([
      this.productService.findProductHistory(this.productId),
      timer(1000)
    ])
      .pipe(takeUntil(this.destroy), map(obj => obj[0]))
      .subscribe(productHistoryList => {
        this.productHistoryList = productHistoryList;
        this.dataSource.data = this.productHistoryList;
        this.listLoading = false;
      });
  }

  shouldDisplayTable(): boolean {
    return this.productHistoryList.length > 0;
  }

  shouldDisplaySpinner(): boolean {
    return this.loading || this.listLoading;
  }

  getFormAnimationState(): string {
    return this.loading ? 'loaded' : 'unloaded';
  }

  round(num: number): string {
    return String(+parseFloat(String(num)).toFixed(2));
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleString();
  }

  getFieldOldValue(history: ProductHistoryDto): string {
    if (history.fieldName !== ProductField.ARCHIVE) {
      console.log(history.fieldName)
      return history.oldValue.toString();
    } else {
      return history.oldValue === 0 ? 'history.false' : 'history.true';
    }
  }

  getFieldNewValue(history: ProductHistoryDto): string {
    if (history.fieldName !== ProductField.ARCHIVE) {
      return history.newValue.toString();
    } else {
      return history.newValue === 0 ? 'history.false' : 'history.true';
    }
  }
}
