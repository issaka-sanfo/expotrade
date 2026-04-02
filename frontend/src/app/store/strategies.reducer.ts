import { createAction, createReducer, on, props } from '@ngrx/store';
import { Strategy } from '../shared/models/models';
import { StrategyState } from './app.state';

export const loadStrategies = createAction('[Strategies] Load');
export const loadStrategiesSuccess = createAction('[Strategies] Load Success', props<{ strategies: Strategy[] }>());
export const loadStrategiesFailure = createAction('[Strategies] Load Failure', props<{ error: string }>());

const initialState: StrategyState = { strategies: [], loading: false, error: null };

export const strategiesReducer = createReducer(
  initialState,
  on(loadStrategies, (state) => ({ ...state, loading: true })),
  on(loadStrategiesSuccess, (state, { strategies }) => ({ ...state, strategies, loading: false })),
  on(loadStrategiesFailure, (state, { error }) => ({ ...state, loading: false, error }))
);
