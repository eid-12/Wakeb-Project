const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();

// ضروري جداً لقراءة البيانات القادمة من المتصفح
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use('/api/auth', createProxyMiddleware({
    target: 'http://wakeb-application-auth-service-1:8080', 
    changeOrigin: true,
    onProxyReq: (proxyReq, req, res) => {
        // إذا كان هناك جسم للطلب، قم بكتابته يدوياً في طلب البروكسي
        if (req.body && Object.keys(req.body).length) {
            const bodyData = JSON.stringify(req.body);
            proxyReq.setHeader('Content-Type', 'application/json');
            proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
            proxyReq.write(bodyData);
        }
    }
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
