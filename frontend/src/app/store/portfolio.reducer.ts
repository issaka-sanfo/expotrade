import { createAction, createReducer, on, props } from '@ngrx/store';
import { Portfolio } from '../shared/models/models';
import { PortfolioState } from './app.state';

export const loadPortfolio = createAction('[Portfolio] Load');
export const loadPortfolioSuccess = createAction('[Portfolio] Load Success', props<{ portfolio: Portfolio }>());
export const loadPortfolioFailure = createAction('[Portfolio] Load Failure', props<{ error: string }>());

const initialState: PortfolioState = { portfolio: null, loading: false, error: null };

export const portfolioReducer = createReducer(
  initialState,
  on(loadPortfolio, (state) => ({ ...state, loading: true, error: null })),
  on(loadPortfolioSuccess, (state, { portfolio }) => ({ ...state, portfolio, loading: false })),
  on(loadPortfolioFailure, (state, { error }) => ({ ...state, loading: false, error }))
);
