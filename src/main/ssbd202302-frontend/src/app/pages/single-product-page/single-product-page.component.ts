import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { first, takeUntil, combineLatest, map, Subject } from 'rxjs';
import { Product } from 'src/app/interfaces/product';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-single-product-page',
  templateUrl: './single-product-page.component.html',
  styleUrls: ['./single-product-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state('loaded', style({
        opacity: 1,
        backgroundColor: "rgba(221, 221, 221, 1)"
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "80px",
        backgroundColor: "rgba(0, 0, 0, 0)"
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
export class SingleProductPageComponent implements OnInit {
  product: Product;
  id = '';
  destroy = new Subject<boolean>();
  loading = true;

  constructor(private productService: ProductService, 
    private activatedRoute: ActivatedRoute, 
    private translate: TranslateService, 
    private navigationService: NavigationService,
    private dialogService: DialogService) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id') || '';
    this.productService.retrieveProduct(this.id)
    .pipe(first(), takeUntil(this.destroy))
    .subscribe({
      next: product => {
        this.product = product;
        console.log(this.product)
        this.loading = false;
      }
    }
    );

  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }
  shouldDisplaySpinner(): boolean {
    return this.loading;
  }

  getStarArray(averageRating: number): string[] {
    const starArray = [];
    const fullStars = Math.floor(averageRating);
    const halfStars = Math.ceil(averageRating - fullStars);
    const emptyStars = 5 - fullStars - halfStars;

    for (let i = 0; i < fullStars; i++) {
      starArray.push('star');
    }

    for (let i = 0; i < halfStars; i++) {
      starArray.push('star_half');
    }

    for (let i = 0; i < emptyStars; i++) {
      starArray.push('star_border');
    }

    return starArray;
  }
}
