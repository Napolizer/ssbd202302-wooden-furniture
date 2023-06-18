import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpResponse,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {NavigationService} from "./navigation.service";

@Injectable({
  providedIn: 'root'
})
export class RedirectInterceptorService {

  constructor(private router: NavigationService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {

        }
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Redirect to your custom page
          this.router.redirectToUnauthorizedPage();
        } else if (error.status === 500) {
          // Redirect to your custom page
          this.router.redirectToServerErrorPage();
        }
        return throwError(error);
      })
    );
  }
}
