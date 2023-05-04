import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
    selector: 'app-confirm-page',
    template: ''
  })
  export class ConfirmPageComponent implements OnInit {
    constructor(private route: ActivatedRoute, private router: Router) {}
  
    ngOnInit(): void {
      this.route.queryParams.subscribe((params) => {
        const token = params["token"];
        if(token) {
          this.router.navigate(['/login'], { state: {token: token} });
        } else {
          this.router.navigate(['/'])
          // change to 404 page
        }
      });
    }
  }