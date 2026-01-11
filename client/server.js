const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const bodyParser = require('body-parser'); // ضروري لمعالجة البيانات

const app = express();

// قراءة بيانات الـ JSON والـ URL-encoded قبل البروكسي
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const fixRequestBody = (proxyReq, req, res) => {
    if (req.body && Object.keys(req.body).length) {
        const bodyData = JSON.stringify(req.body);
        // تأكد من ضبط الرؤوس قبل الكتابة
        proxyReq.setHeader('Content-Type', 'application/json');
        proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
        // كتابة البيانات
        proxyReq.write(bodyData);
    }
};

app.use('/api/auth', createProxyMiddleware({
    target: 'http://wakeb-application-auth-service-1:8080/api/auth', 
    changeOrigin: true,

    pathRewrite: { '^/api/auth': '/api/auth' },
    onProxyReq: fixRequestBody, // تأكد من استخدام دالة تمرير البيانات التي برمجناها
    proxyTimeout: 120000, // دقيقتين
    timeout: 120000

}));

app.use('/api', createProxyMiddleware({
    target: 'http://wakeb-application-api-gateway-1:8080',
    changeOrigin: true,
    onProxyReq: fixRequestBody
}));

app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(3000, () => console.log('✅ Proxy is successfully forwarding with Body data'));
