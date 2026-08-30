export const environment = {
  production: false,
  // Absolute URL so local dev works whether the dev server is started via
  // `npm start` (proxy-config) or plain `ng serve` (no proxy) -- see
  // backend's CorsConfig, which allows any origin by default for exactly
  // this reason.
  apiBaseUrl: 'http://localhost:8080',
};
