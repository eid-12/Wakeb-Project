const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');

const app = express();

/** * 1. إعدادات البروكسي (يجب أن تكون قبل bodyParser) 
 * هذا الترتيب يضمن أن البيانات تمر مباشرة للجافا دون أن "تتعلق" أو تستهلك
 */

// بروكسي خدمة الأوث (Auth Service)
app.use('/api/auth', createProxyMiddleware({
    // نضع المسار كاملاً كما طلبت لضمان وصوله للجافا بشكل سليم
    target: 'http://wakeb-application-auth-service-1:8080/api/auth', 
    changeOrigin: true,
    // نحذف البادئة لكي لا تظهر مكررة في الرابط النهائي
    pathRewrite: { '^/api/auth': '' }, 
    proxyTimeout: 120000,
    timeout: 120000
}));

// بروكسي بوابة التطبيق (API Gateway)
app.use('/api', createProxyMiddleware({
    target: 'http://wakeb-application-api-gateway-1:8080/api',
    changeOrigin: true,
    pathRewrite: { '^/api': '' }, 
    proxyTimeout: 120000,
    timeout: 120000
}));

/** * 2. إعدادات معالجة البيانات (تأتي بعد البروكسي)
 */
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

/** * 3. ملفات الفرونت إند (Vue.js Dist)
 */
app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(3000, () => console.log('✅ Proxy is running and forwarding directly with optimized order'));
