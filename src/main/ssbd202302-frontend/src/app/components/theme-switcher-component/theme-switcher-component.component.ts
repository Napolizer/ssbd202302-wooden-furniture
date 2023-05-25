import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { Mode } from 'src/app/interfaces/mode';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-theme-switcher-component',
  templateUrl: './theme-switcher-component.component.html',
  styleUrls: ['./theme-switcher-component.component.sass']
})
export class ThemeSwitcherComponentComponent implements OnInit{
  isDarkThemeActive: boolean;
  mode: string;
  changeMode: Mode = {
    mode: 'DARK'
  }


  constructor(@Inject(DOCUMENT) private document: Document,
              private accountService: AccountService) 
  {}

  onThemeClick(theme: string): void {
    switch(theme) {
      case 'DARK': 
      this.document.body.classList.add('dark-mode');
      this.changeMode.mode = 'DARK';
      this.accountService.changeMode(this.changeMode)
      .subscribe();
        break;
      case 'LIGHT': 
      this.document.body.classList.remove('dark-mode');
      this.changeMode.mode='LIGHT';
      this.accountService.changeMode(this.changeMode)
      .subscribe();
        break;
      default:
        break;
    }
  }


  loginInit(): void {
    this.accountService.retrieveOwnMode()
    .subscribe(mode => {
      this.mode = mode.mode;
      switch(this.mode) {
        case 'DARK': 
          this.isDarkThemeActive=true;
          this.document.body.classList.add('dark-mode');
          break;
        case 'LIGHT': 
          this.isDarkThemeActive=false;
          this.document.body.classList.remove('dark-mode');
          break;
        default:
          break;
      }
    })
  }

  ngOnInit(): void {
    this.accountService.retrieveOwnMode()
    .subscribe(mode => {
      this.mode = mode.mode;
      switch(this.mode) {
        case 'DARK': 
          this.isDarkThemeActive=false;
          this.document.body.classList.add('dark-mode');
          break;
        case 'LIGHT': 
          this.isDarkThemeActive=true;
          this.document.body.classList.remove('dark-mode');
          break;
        default:
          break;
      }
    })
  }
}
