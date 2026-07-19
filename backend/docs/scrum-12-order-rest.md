Feature: Order REST exposure

Scenario: Authenticated user places a valid order
Given a valid JWT whose subject is the current user id
When the user posts a valid payload to POST /api/v1/orders
Then the REST adapter builds a PlaceOrderCommand with the JWT user id
And the API returns the mapped order response.

Scenario: Invalid order payload is rejected
Given a valid JWT
When the user posts an invalid payload to POST /api/v1/orders
Then Bean Validation rejects the request with HTTP 400.

Scenario: User lists only their own orders
Given a valid JWT
When the user calls GET /api/v1/orders
Then the controller queries orders by the JWT user id.

Scenario: User filters only their own strategy orders
Given a valid JWT
When the user calls GET /api/v1/orders/strategy/{strategyId}
Then the controller queries orders by both JWT user id and strategy id.

Scenario: User cancels only an owned order
Given a valid JWT
When the user calls DELETE /api/v1/orders/{orderId}
Then the application service verifies order.userId against the JWT user id before calling the broker.

DDD decisions:
- OrderController is the inbound REST adapter.
- OrderService owns the application use cases.
- CancelOrderUseCase carries the current user id so authorization stays in the application flow.
- OrderRepository remains the outbound port and exposes a user plus strategy query for user-scoped reads.
