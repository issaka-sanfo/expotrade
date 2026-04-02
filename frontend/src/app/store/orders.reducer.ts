import { createAction, createReducer, on, props } from '@ngrx/store';
import { Order } from '../shared/models/models';
import { OrderState } from './app.state';

export const loadOrders = createAction('[Orders] Load');
export const loadOrdersSuccess = createAction('[Orders] Load Success', props<{ orders: Order[] }>());
export const loadOrdersFailure = createAction('[Orders] Load Failure', props<{ error: string }>());
export const placeOrder = createAction('[Orders] Place', props<{ order: Partial<Order> }>());
export const placeOrderSuccess = createAction('[Orders] Place Success', props<{ order: Order }>());

const initialState: OrderState = { orders: [], loading: false, error: null };

export const ordersReducer = createReducer(
  initialState,
  on(loadOrders, (state) => ({ ...state, loading: true })),
  on(loadOrdersSuccess, (state, { orders }) => ({ ...state, orders, loading: false })),
  on(loadOrdersFailure, (state, { error }) => ({ ...state, loading: false, error })),
  on(placeOrderSuccess, (state, { order }) => ({ ...state, orders: [order, ...state.orders] }))
);
