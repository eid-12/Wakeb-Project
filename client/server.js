const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

// إعدادات البروكسي لخدمة الأوثنتيكيشن
app.use('/api/auth', createProxyMiddleware({
    // استخدام الاسم الكامل الذي نجح في اختبار curl
    target: 'http://wakeb-application-auth-service-1:8080', 
    changeOrigin: true,
    // زيادة وقت الانتظار لتجنب خطأ 504 أثناء معالجة البيانات
    proxyTimeout: 20000, 
    timeout: 20000,
    // التأكد من تمرير رؤوس الطلب الأصلية (مهم لـ Spring Security)
    onProxyReq: (proxyReq, req, res) => {
        if (req.body) {
            const bodyData = JSON.stringify(req.body);
            proxyReq.setHeader('Content-Type', 'application/json');
            proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
            proxyReq.write(bodyData);
        }
    },
    // المسار يبقى كما هو لأن الكنترولر يبدأ بـ /api/auth
    pathRewrite: { '^/api/auth': '/api/auth' } 
}));

// إعدادات البروكسي للجيت واي
app.use('/api', createProxyMiddleware({
    target: 'http://wakeb-application-api-gateway-1:8080',
    changeOrigin: true,
    proxyTimeout: 20000,
    timeout: 20000
}));

// تقديم الملفات الثابتة للفرونت إند
app.use(express.static(path.join(__dirname, 'dist')));

// توجيه أي مسار آخر لملف index.html (لدعم Vue Router)
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`🚀 Proxy server is running on port ${PORT}`);
    console.log(`🔗 Routing /api/auth to -> http://wakeb-application-auth-service-1:8080`);
    console.log(`🔗 Routing /api to -> http://wakeb-application-api-gateway-1:8080`);
});
