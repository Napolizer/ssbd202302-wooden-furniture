import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-account-page',
  templateUrl: './account-page.component.html',
  styleUrls: ['./account-page.component.sass']
})
export class AccountPageComponent implements OnInit {
  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true}),
    // email: new FormControl('')
  });

  constructor() { }

  ngOnInit(): void {
  }

}
