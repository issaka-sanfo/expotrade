import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./features/dashboard/login.component').then(m => m.LoginComponent) },
  { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [authGuard] },
  { path: 'trading', loadComponent: () => import('./features/trading/trading.component').then(m => m.TradingComponent), canActivate: [authGuard] },
  { path: 'strategies', loadComponent: () => import('./features/strategy/strategy.component').then(m => m.StrategyComponent), canActivate: [authGuard] },
  { path: 'monitoring', loadComponent: () => import('./features/monitoring/monitoring.component').then(m => m.MonitoringComponent), canActivate: [authGuard] }
];
