import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { portfolioReducer } from './store/portfolio.reducer';
import { ordersReducer } from './store/orders.reducer';
import { strategiesReducer } from './store/strategies.reducer';
import { PortfolioEffects } from './store/portfolio.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),
    provideStore({
      portfolio: portfolioReducer,
      orders: ordersReducer,
      strategies: strategiesReducer
    }),
    provideEffects([PortfolioEffects]),
    provideStoreDevtools({ maxAge: 25 })
  ]
};
