export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  wsUrl: 'ws://localhost:8080/ws/events',
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'expotrade',
    clientId: 'expotrade-mobile'
  }
};
