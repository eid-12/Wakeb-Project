const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

app.use('/api/auth', createProxyMiddleware({
    // نستخدم اسم الحاوية الموضح في Docker Desktop
    target: 'http://auth-service-1:8080', 
    changeOrigin: true,
    // تأكد من بقاء المسار كما هو /api/auth/login
    pathRewrite: { '^/api/auth': '/api/auth' } 
}));

app.use('/api', createProxyMiddleware({
    target: 'http://api-gateway-1:8080',
    changeOrigin: true,
}));

app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Proxy server is running and routing to:`);
    console.log(`- Auth: http://auth-service-1:8080/api/auth`);
    console.log(`- Gateway: http://api-gateway-1:8080/api`);
});
