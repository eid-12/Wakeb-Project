const express = require('express');
const path = require('path');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

/**
 * Proxy targets (override per environment — e.g. Railway / Docker service names).
 * Defaults match the existing deployment hostnames so production keeps working.
 */
const AUTH_SERVICE_ORIGIN =
  process.env.AUTH_SERVICE_ORIGIN || 'http://wakeb-application-auth-service-1:8080';
const API_GATEWAY_ORIGIN =
  process.env.API_GATEWAY_ORIGIN || 'http://wakeb-application-api-gateway-1:8080';

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
