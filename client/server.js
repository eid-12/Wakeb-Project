const express = require('express');
const path = require('path');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

/**
 * Proxy targets (override per environment — Docker / Railway / local).
 * Local default: gateway 8080, auth 8081 (set Auth-Service server.port=8081 when running both on one machine).
 * Production: set AUTH_SERVICE_ORIGIN / API_GATEWAY_ORIGIN to your container URLs.
 */
const AUTH_SERVICE_ORIGIN =
  process.env.AUTH_SERVICE_ORIGIN ||
  (process.env.NODE_ENV === 'production'
    ? 'http://wakeb-application-auth-service-1:8080'
    : 'http://localhost:8081');
const API_GATEWAY_ORIGIN =
  process.env.API_GATEWAY_ORIGIN ||
  (process.env.NODE_ENV === 'production'
    ? 'http://wakeb-application-api-gateway-1:8080'
    : 'http://localhost:8080');

/** Order matters: register /api/auth before /api so auth hits the auth service first. */
app.use(
  '/api/auth',
  createProxyMiddleware({
    target: `${AUTH_SERVICE_ORIGIN}/api/auth`,
    changeOrigin: true,
    pathRewrite: { '^/api/auth': '' },
    proxyTimeout: 120000,
    timeout: 120000,
  })
);

app.use(
  '/api',
  createProxyMiddleware({
    target: `${API_GATEWAY_ORIGIN}/api`,
    changeOrigin: true,
    pathRewrite: { '^/api': '' },
    proxyTimeout: 120000,
    timeout: 120000,
  })
);

const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

const port = Number(process.env.PORT) || 3000;
app.listen(port, () => {
  console.log(`Server listening on ${port} (auth → ${AUTH_SERVICE_ORIGIN}, gateway → ${API_GATEWAY_ORIGIN})`);
});
