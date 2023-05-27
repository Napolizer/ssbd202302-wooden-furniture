import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Account} from "../../interfaces/account";

@Component({
  selector: 'app-admin-actions-menu',
  templateUrl: './admin-actions-menu.component.html',
  styleUrls: ['./admin-actions-menu.component.sass']
})
export class AdminActionsMenuComponent implements OnInit {
  @Input()
  account: Account;

  constructor() { }

  ngOnInit(): void {
  }

}
