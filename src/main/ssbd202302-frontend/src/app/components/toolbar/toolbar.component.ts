import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.sass']
})
export class ToolbarComponent implements OnInit {

  constructor(
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  redirectToAdminPage(): void {
    void this.router.navigate(['/admin']);
  }

  redirectToAccountPage(): void {
    void this.router.navigate(['/account']);
  }
}
