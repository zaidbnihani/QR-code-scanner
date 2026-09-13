<p align="center">
  <img src="https://file-uploader-free.onrender.com/uploads/1789326100978_c4at3q.jpeg" width="30%">
</p>

# Fast QR Scanner - ماسح رموز QR السريع


تطبيق أندرويد مفتوح المصدر ومبني بأحدث تقنيات أندرويد لتقديم أسرع تجربة مسح وقراءة لرموز QR Code والباركوود على الجهاز مباشرة مع دعم التحديث التلقائي المباشر من GitHub Releases.

---

## المميزات الرئيسية

- معالجة فورية On-Device ML Kit: مسح دقيق وفوري لرموز QR بمجرد توجيه الكاميرا بدون الحاجة لاتصال بالإنترنت لمعالجة الرموز.
- تصميم عصري وجذاب Material 3: واجهة مستخدم مظلمة أنيقة Dark Theme مع إطار استهداف مضيء وتجربة ملء الشاشة Immersive Mode.
- قراءة الرموز من المعرض: إمكانية اختيار صورة تحتوي على QR Code من المعرض وتحليلها فوراً.
- تحكم بالفلاش: إمكانية تشغيل وإيقاف فلاش الكاميرا بنقرة واحدة للمسح في الإضاءة المنخفضة.
- تحديث تلقائي مدمج Auto Update Engine:
  - فحص تلقائي لأحدث الإصدارات المنشورة على GitHub API عند فتح التطبيق.
  - مقارنة دقيقة لرقم الإصدار باستخدام Semantic Versioning.
  - تنزيل ملف APK وتثبيته مباشرة داخل التطبيق عبر DownloadManager و FileProvider دون فتح المتصفح.

---

## التقنيات المستخدمة

- لغة البرمجة: Kotlin
- واجهة المستخدم: Jetpack Compose Material 3
- الكاميرا والمسح الضوئي: AndroidX CameraX + Google ML Kit Barcode Scanning
- الشبكات والـ API: Retrofit 2 + Gson Converter + Kotlin Coroutines
- إدارة التنزيل والتثبيت: Android DownloadManager + FileProvider

---

## الترخيص

هذا المشروع مفتوح المصدر وتحت رخصة MIT.
