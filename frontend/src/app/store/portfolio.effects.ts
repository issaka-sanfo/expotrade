import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, of } from 'rxjs';
import { ApiService } from '../core/services/api.service';
import { loadPortfolio, loadPortfolioSuccess, loadPortfolioFailure } from './portfolio.reducer';

@Injectable()
export class PortfolioEffects {
  loadPortfolio$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadPortfolio),
      mergeMap(() => this.api.getPortfolio().pipe(
        map(portfolio => loadPortfolioSuccess({ portfolio })),
        catchError(err => of(loadPortfolioFailure({ error: err.message })))
      ))
    )
  );

  constructor(private actions$: Actions, private api: ApiService) {}
}
