const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

// 1. تفعيل قراءة بيانات الـ JSON (ضروري جداً لاستلام بيانات التسجيل والدخول)
app.use(express.json());

// 2. إعدادات البروكسي لخدمة الأوثنتيكيشن
app.use('/api/auth', createProxyMiddleware({
    // استخدام الاسم الكامل المكتشف في اختبار curl
    target: 'http://wakeb-application-auth-service-1:8080', 
    changeOrigin: true,
    // معالجة وقت الانتظار لتجنب خطأ 504
    proxyTimeout: 20000, 
    timeout: 20000,
    // تمرير البيانات (Body) بشكل يدوي لضمان وصولها لـ Spring Boot
    onProxyReq: (proxyReq, req, res) => {
        if (req.body && Object.keys(req.body).length > 0) {
            const bodyData = JSON.stringify(req.body);
            proxyReq.setHeader('Content-Type', 'application/json');
            proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
            proxyReq.write(bodyData);
        }
    },
    // المسار يبقى كما هو لأن الكنترولر يبدأ بـ /api/auth
    pathRewrite: { '^/api/auth': '/api/auth' } 
}));

// 3. إعدادات البروكسي للجيت واي
app.use('/api', createProxyMiddleware({
    target: 'http://wakeb-application-api-gateway-1:8080',
    changeOrigin: true,
    proxyTimeout: 20000,
    timeout: 20000,
    // تمرير البيانات أيضاً للجيت واي في حال وجود طلبات POST/PUT
    onProxyReq: (proxyReq, req, res) => {
        if (req.body && Object.keys(req.body).length > 0) {
            const bodyData = JSON.stringify(req.body);
            proxyReq.setHeader('Content-Type', 'application/json');
            proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
            proxyReq.write(bodyData);
        }
    }
}));

// 4. تقديم الملفات الثابتة للفرونت إند (Vue.js)
app.use(express.static(path.join(__dirname, 'dist')));

// 5. دعم Vue Router (توجيه كافة المسارات لـ index.html)
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`🚀 Proxy server is running on port ${PORT}`);
    console.log(`🔗 Routing /api/auth to -> http://wakeb-application-auth-service-1:8080`);
    console.log(`🔗 Routing /api to -> http://wakeb-application-api-gateway-1:8080`);
});
