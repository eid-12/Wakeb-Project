-- إلغاء شرط عمود email_verified من جدول users (تسجيل الدخول: اسم مستخدم + كلمة مرور فقط)
-- نفّذ على قاعدة Auth-Service (مثلاً mapping_db).

-- MySQL 8.0.29+ : حذف العمود إن وُجد
ALTER TABLE users DROP COLUMN IF EXISTS email_verified;

-- إن كان إصدار MySQL أقدم ولا يدعم IF EXISTS، استخدم أحد الخيارين يدوياً:
-- ALTER TABLE users DROP COLUMN email_verified;
-- أو إبقاء العمود لكن بدون إلزام (لا يُشترط قيمة):
-- ALTER TABLE users MODIFY COLUMN email_verified TINYINT(1) NULL DEFAULT NULL;
