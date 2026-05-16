import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then(m => m.LoginPage)
  },
  {
    path: 'tabs',
    loadComponent: () => import('./pages/tabs.page').then(m => m.TabsPage),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard.page').then(m => m.DashboardPage)
      },
      {
        path: 'market-data',
        loadComponent: () => import('./pages/market-data/market-data.page').then(m => m.MarketDataPage)
      },
      {
        path: 'orders',
        loadComponent: () => import('./pages/orders/orders.page').then(m => m.OrdersPage)
      },
      {
        path: 'trades',
        loadComponent: () => import('./pages/trades/trades.page').then(m => m.TradesPage)
      },
      {
        path: 'strategies',
        loadComponent: () => import('./pages/strategies/strategies.page').then(m => m.StrategiesPage)
      }
    ]
  }
];
