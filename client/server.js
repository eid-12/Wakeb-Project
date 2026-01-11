const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

// ضروري جداً لقراءة بيانات الـ JSON القادمة من المتصفح
app.use(express.json()); 

app.use('/api/auth', createProxyMiddleware({
    target: 'http://wakeb-application-auth-service-1:8080', 
    changeOrigin: true,
    onProxyReq: (proxyReq, req, res) => {
        // إعادة كتابة الجسم (Body) للطلب لضمان وصوله لخدمة الجافا
        if (req.body) {
            const bodyData = JSON.stringify(req.body);
            proxyReq.setHeader('Content-Type', 'application/json');
            proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
            proxyReq.write(bodyData);
        }
    }
}));

app.use('/api', createProxyMiddleware({
    target: 'http://wakeb-application-api-gateway-1:8080',
    changeOrigin: true
}));

app.use(express.static(path.join(__dirname, 'dist')));
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(3000, () => console.log('✅ Proxy is up and forwarding requests'));
