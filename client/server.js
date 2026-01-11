const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const bodyParser = require('body-parser');

const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const fixRequestBody = (proxyReq, req, res) => {
    if (req.body && Object.keys(req.body).length) {
        const bodyData = JSON.stringify(req.body);
        proxyReq.setHeader('Content-Type', 'application/json');
        proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
        proxyReq.write(bodyData);
        proxyReq.end();
    }
};

// الإعداد بناءً على طلبك (بدون pathRewrite)
app.use('/api/auth', createProxyMiddleware({
    // نضع البادئة مباشرة في الرابط
    target: 'http://wakeb-application-auth-service-1:8080/api/auth', 
    changeOrigin: true,
    // نجعل الـ rewrite يحذف البادئة القديمة لكي لا تتكرر
    pathRewrite: { '^/api/auth': '' }, 
    onProxyReq: fixRequestBody,
    proxyTimeout: 120000,
    timeout: 120000
}));

app.use('/api', createProxyMiddleware({
    target: 'http://wakeb-application-api-gateway-1:8080',
    changeOrigin: true,
    onProxyReq: fixRequestBody,
    proxyTimeout: 120000,
    timeout: 120000
}));

app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(3000, () => console.log('✅ Proxy is running and forwarding directly'));
