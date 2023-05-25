import { Injectable } from "@angular/core";
import { NavigationService } from "./navigation.service";
import { Subject } from "rxjs/internal/Subject";

@Injectable({
    providedIn: 'root'
  })
  export class BreadcrumbsService {
    breadcrumbs: string[];
    currentUrl: string = window.location.href;
    constructor(private navigationService: NavigationService) {}

    initBreadcrumbs(): string[] {
        if(this.currentUrl.includes("/admin")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel']
          }
          else if(this.currentUrl.includes("/create-account")) {
            this.breadcrumbs=['toolbar.home', 'create.account.title']
          }
          else if(this.currentUrl.includes("/self")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.myAccount']
          }
          else if(this.currentUrl.includes("/admin")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel']
          }
          else if(this.currentUrl.includes("/account/") && this.currentUrl.includes("/edit")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel', 'toolbar.account' , 'account.edit']
          }
          else if(this.currentUrl.includes("/account")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel', 'toolbar.account']
          }
          else if(this.currentUrl.includes("/edit-own-account")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.myAccount', 'account.edit']
          }
          else if(this.currentUrl.includes("/change-password")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.myAccount', 'account.change.password']
          }
          else if(this.currentUrl.includes("/account-role-add")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel', 'toolbar.account' , 'account.addrole']
          }
          else if(this.currentUrl.includes("/account-role-remove")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel', 'toolbar.account', 'account.removerole']
          }
          else if(this.currentUrl.includes("/account-role-change")) {
            this.breadcrumbs=['toolbar.home', 'toolbar.adminPanel', 'toolbar.account', 'account.changerole']
          }
          else if(this.currentUrl.includes("/register")) {
            this.breadcrumbs=['toolbar.home', 'register.title']
          }
          else if(this.currentUrl.includes("/home")) {
            this.breadcrumbs=['toolbar.home']
          }
          else if(this.currentUrl.includes("/login")) {
            this.breadcrumbs=['toolbar.home', 'register.label.login']
          }
          return this.breadcrumbs;
    }

    navigate(breadcrumb: string): void {
        switch(breadcrumb) {
            case 'toolbar.home': 
                void this.navigationService.redirectToMainPage();
                break;
            case 'toolbar.adminPanel':
                void this.navigationService.redirectToAdminPage();
                break;
        }
    }

    private refreshSubject = new Subject<void>();

  refreshToolbar() {
    this.refreshSubject.next();
  }

  getRefreshObservable() {
    return this.refreshSubject.asObservable();
  }
  
  }