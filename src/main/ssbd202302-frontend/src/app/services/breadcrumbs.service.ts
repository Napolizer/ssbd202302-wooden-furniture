import { Injectable } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

@Injectable({
    providedIn: 'root'
  })
  export class BreadcrumbsService {

    constructor(
        private translate: TranslateService
      ) { }

    
    breadcrumbsData: string[] = [];

    getHomeBreadcrumb(): void{
        this.translate.get('toolbar.home')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });
    }

    //ADMIN
    // ADMIN PANEL

    getAdminBreadcrumb(): string[] {
        this.emptyList();
        this.getHomeBreadcrumb();
        this.translate.get('toolbar.adminPanel')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });
        return this.breadcrumbsData;
    }

    getUserAccountBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getAdminBreadcrumb();
        this.translate.get('toolbar.account')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    getBlockAccountBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getUserAccountBreadcrumb();
        this.translate.get('account.block')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    getUnblockAccountBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getUserAccountBreadcrumb();
        this.translate.get('account.unblock')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    //TODO breadcrumbs dla zmian ról
    //  
    //


    getEditAccountBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getUserAccountBreadcrumb();
        this.translate.get('account.edit')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    getChangeEmailBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getUserAccountBreadcrumb();
        this.translate.get('change.email.button')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    // CREATE ACCOUNT
    getCreateAccountBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getAdminBreadcrumb();
        this.translate.get('toolbar.createAccount')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }


    //USER ACCOUNT
    getSelfBreadcrumb(): string[] {
        this.emptyList();
        this.getHomeBreadcrumb();
        this.translate.get('toolbar.myAccount')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });
        return this.breadcrumbsData;
    }

    getChangeOwnMailBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getSelfBreadcrumb();
        this.translate.get('change.email.button')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    getChangeOwnPasswordBreadcrumb(): string[] {
        this.emptyList();
        this.getHomeBreadcrumb();
        this.translate.get('change.password.button')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    getEditOwnAccountBreadcrumb(): string[] {
        this.emptyList();
        this.breadcrumbsData=this.getSelfBreadcrumb();
        this.translate.get('edit.account')
            .subscribe((translation: string) => {
            this.breadcrumbsData.push(translation);
          });

          return this.breadcrumbsData;
    }

    emptyList(): void {
        this.breadcrumbsData=[];
    }
}