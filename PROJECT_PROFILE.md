# ملف المشروع | Project Profile

## العربية

### الغرض
تطبيق أندرويد لمسح رموز QR والباركود بالكاميرا، مع قراءة رمز من صورة يختارها المستخدم، والتعامل مع المحتوى المكتشف. يصف `README.md` و`PRD.MD` المسح المحلي عبر ML Kit، وواجهة Compose ذات نمط داكن، والتحكم بالفلاش، وفحص الإصدارات عبر GitHub Releases وتثبيت APK. وتؤيد ملفات المصدر مسارات الكاميرا وتحليل الباركود وتحديث التطبيق؛ تفاصيل الميزات المعلنة في README قد تتغير، لذا راجع الشيفرة للتنفيذ الحالي.

### المكوّنات والتدفقات الأساسية
- `app/src/main/java/com/example/MainActivity.kt`: نقطة دخول التطبيق وتركيب واجهة Compose، وأحداث الاختيار/الصلاحيات التي توصل الشاشة بمكوّن المسح.
- `app/src/main/java/com/example/qr/CameraScannerView.kt` و`FastQrAnalyzer.kt`: واجهة/تحليل إطارات الكاميرا باستخدام CameraX وواجهة ML Kit لقراءة الباركود.
- `app/src/main/java/com/example/qr/QrScannerViewModel.kt`: حالة وتنسيق شاشة الماسح.
- `app/src/main/java/com/example/ui/QrScannerOverlay.kt` و`ui/theme/`: مكونات الواجهة والمظهر.
- `app/src/main/java/com/example/update/UpdateChecker.kt`, `GitHubModels.kt`, `SemVer.kt`, `UpdateCheckerEffect.kt`: جلب بيانات الإصدار من GitHub API ومقارنتها وعرض/تنسيق حالة التحديث.
- `app/src/main/java/com/example/update/ApkInstaller.kt`: تنزيل APK وتدفق التثبيت؛ يوضح `AndroidManifest.xml` صلاحيات الكاميرا والإنترنت وطلب تثبيت الحزم، وإعداد `app/src/main/res/xml/file_paths.xml` لمسارات FileProvider.

### شجرة مختصرة
```text
.
├── README.md                         وصف ومزايا وتقنيات وترخيص
├── PRD.MD                            ملخص متطلبات/مزايا المنتج
├── settings.gradle.kts               إعداد مشروع Gradle ووحدة :app
├── build.gradle.kts                  إضافات Gradle على مستوى الجذر
├── gradle/
│   ├── libs.versions.toml             كتالوج الإصدارات والتبعيات والإضافات
│   └── wrapper/gradle-wrapper.properties إصدار Gradle Wrapper
└── app/
    ├── build.gradle.kts               إعداد Android والتبعيات والاختبارات
    └── src/
        ├── main/                      manifest، Kotlin، الموارد والثيمات
        ├── test/                      اختبارات JVM/Robolectric ولقطة واجهة
        └── androidTest/                اختبار Android instrumented
```

### الملفات المحورية والتقنية
- إعدادات التطبيق في `app/build.gradle.kts`: Android application، Compose وKSP وإضافات Google Services/Secrets وRoborazzi؛ `namespace` و`applicationId` كلاهما `com.example`، `compileSdk` 36 (minor API 1)، `minSdk` 24، `targetSdk` 36، Java source/target 11، ونسخة التطبيق 1.0 (code 1).
- كتالوج `gradle/libs.versions.toml` يحدد AGP 9.1.1، Kotlin 2.2.10، CameraX 1.5.0، ML Kit Barcode Scanning 17.3.0، Retrofit 2.12.0، Coroutines 1.10.2، Compose BOM 2024.09.00، Room 2.7.0، Firebase BOM 34.17.0، Robolectric 4.16.1 وRoborazzi 1.59.0. راجع الملف للتبعيات الأخرى والإصدارات التفصيلية؛ الكتالوج يتضمن أيضًا عناصر غير مستخدمة أو معلّقة، فلا تُعامل جميعها كتبعيات فعلية.
- التبعيات المفعّلة في `app/build.gradle.kts` تشمل CameraX وML Kit وCompose، Retrofit مع Gson/Moshi وOkHttp، Coroutines، Firebase AI/App Check، Room، وأدوات الاختبار. نقطة النهاية الأساسية لخدمة التحديث هي `https://api.github.com/` في `UpdateChecker.kt`.
- `gradle/wrapper/gradle-wrapper.properties` يحدد Gradle 9.3.1.

### الإعداد والتشغيل والاختبار والبناء
المستودع يهيئ Gradle Wrapper ومهام اختبار محلية/أدوات Android، لكن README وملفات المشروع التي فُحصت لا توثق تعليمات إعداد SDK أو أوامر تشغيل أو اختبار أو بناء صريحة؛ لذلك لا تُدرج هنا أوامر مفترضة. إعداد التوقيع يقرأ أسماء متغيرات البيئة `KEYSTORE_PATH` و`STORE_PASSWORD` و`KEY_PASSWORD` في `app/build.gradle.kts`، مع مسار افتراضي لملف keystore؛ لم تُقرأ ملفات أسرار ولم تُدرج أي قيم. يستخدم Secrets Gradle Plugin اسمي ملفّي الإعداد `.env` و`.env.example` وفق إعداد Gradle، لكنهما غير موثقين هنا كمتطلبات تشغيل.

### نقاط عملية وغير محسومة
- ابدأ بمراجعة `README.md` و`PRD.MD` ثم `app/build.gradle.kts` وكتالوج الإصدارات والشيفرة في `app/src/main`.
- توجد اختبارات عينة في `app/src/test/java/com/example/` واختبار instrumented في `app/src/androidTest/java/com/example/`; اختبار Roborazzi يستخدم/يحدّث صورة `app/src/test/screenshots/greeting.png`.
- لم يُعثر على ملفات CI ضمن `.github/workflows/` في قائمة الملفات المتعقبة.
- README يذكر معالجة المعرض والتحديث التلقائي، لكن تدفق المعرض/التحديث الفعلي يجب التحقق منه من تفاصيل التنفيذ قبل تغيير السلوك. توجد تبعيات عديدة في الكتالوج لا تُستخدم أو معلّقة في إعداد التطبيق، ولا توثق الملفات التي فُحصت تعليمات إصدار مكتملة أو بيانات اعتماد Firebase؛ لا تفترض توفرها.

## English

### Purpose
An Android application for scanning QR codes and barcodes with the camera, reading a code from a user-selected image, and handling detected content. `README.md` and `PRD.MD` describe on-device ML Kit scanning, a dark Compose interface, flashlight control, and checking GitHub Releases and installing an APK. Source files support the camera, barcode-analysis, and app-update paths; README feature claims may evolve, so consult source for current implementation details.

### Main components and flows
- `app/src/main/java/com/example/MainActivity.kt`: application entry point and Compose UI setup, including selection/permission events connecting the screen to scanner components.
- `app/src/main/java/com/example/qr/CameraScannerView.kt` and `FastQrAnalyzer.kt`: camera UI/frame analysis using CameraX and ML Kit's barcode API.
- `app/src/main/java/com/example/qr/QrScannerViewModel.kt`: scanner screen state and coordination.
- `app/src/main/java/com/example/ui/QrScannerOverlay.kt` and `ui/theme/`: UI components and visual theme.
- `app/src/main/java/com/example/update/UpdateChecker.kt`, `GitHubModels.kt`, `SemVer.kt`, `UpdateCheckerEffect.kt`: fetch release data from GitHub API, compare versions, and present/update state.
- `app/src/main/java/com/example/update/ApkInstaller.kt`: APK download and install flow; `AndroidManifest.xml` declares camera, internet, and package-install permissions, while `app/src/main/res/xml/file_paths.xml` configures FileProvider paths.

### Short tree
```text
.
├── README.md                         Description, features, technologies, license
├── PRD.MD                            Product requirements/features summary
├── settings.gradle.kts               Gradle project setup and :app module
├── build.gradle.kts                  Root-level Gradle plugins
├── gradle/
│   ├── libs.versions.toml             Dependency/plugin version catalog
│   └── wrapper/gradle-wrapper.properties Gradle Wrapper version
└── app/
    ├── build.gradle.kts               Android, dependency, and test configuration
    └── src/
        ├── main/                      Manifest, Kotlin sources, resources and themes
        ├── test/                      JVM/Robolectric tests and UI screenshot
        └── androidTest/                Instrumented Android test
```

### Key files and technology
- `app/build.gradle.kts` configures an Android application, Compose, KSP, Google Services/Secrets and Roborazzi plugins; `namespace` and `applicationId` are both `com.example`, `compileSdk` is 36 (minor API 1), `minSdk` 24, `targetSdk` 36, Java source/target 11, and app version 1.0 (code 1).
- `gradle/libs.versions.toml` specifies AGP 9.1.1, Kotlin 2.2.10, CameraX 1.5.0, ML Kit Barcode Scanning 17.3.0, Retrofit 2.12.0, Coroutines 1.10.2, Compose BOM 2024.09.00, Room 2.7.0, Firebase BOM 34.17.0, Robolectric 4.16.1, and Roborazzi 1.59.0. Consult that file for other exact versions; the catalog also contains unused or commented-out entries, so not all catalog entries are active dependencies.
- Active dependencies in `app/build.gradle.kts` include CameraX, ML Kit, Compose, Retrofit with Gson/Moshi and OkHttp, Coroutines, Firebase AI/App Check, Room, and test tooling. The update service's base endpoint is `https://api.github.com/` in `UpdateChecker.kt`.
- `gradle/wrapper/gradle-wrapper.properties` specifies Gradle 9.3.1.

### Setup, run, test, and build
The repository configures Gradle Wrapper and local test/Android tooling, but the README and project files reviewed do not document explicit SDK setup or run, test, or build commands; no speculative commands are provided here. Signing configuration reads environment variable names `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` in `app/build.gradle.kts`, with a default keystore path; secret files were not read and no values are included. The Secrets Gradle Plugin is configured with `.env` and `.env.example` as property file names, but these are not documented here as runtime requirements.

### Practical notes and open questions
- Start with `README.md` and `PRD.MD`, then review `app/build.gradle.kts`, the version catalog, and sources under `app/src/main`.
- Sample tests are under `app/src/test/java/com/example/`, with an instrumented test under `app/src/androidTest/java/com/example/`; the Roborazzi test uses/updates `app/src/test/screenshots/greeting.png`.
- No CI files were found under `.github/workflows/` in the tracked-file listing.
- README mentions gallery input and automatic updates, but verify their actual flows in source before changing behavior. The catalog contains multiple unused or commented dependencies, and the files reviewed do not document a complete release procedure or Firebase credentials; do not assume these are available.
