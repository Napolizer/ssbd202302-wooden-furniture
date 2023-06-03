import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-add-product-group',
  templateUrl: './add-product-group.component.html',
  styleUrls: ['./add-product-group.component.sass']
})
export class AddProductGroupComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;

  constructor() { }

  ngOnInit(): void {
    this.loading = false;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onConfirm(): void {
    
  }

}
