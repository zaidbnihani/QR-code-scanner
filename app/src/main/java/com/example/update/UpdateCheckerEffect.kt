package com.example.update

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Composable that checks GitHub for updates silently on launch.
 * If a new release is found via SemVer comparison, shows an AlertDialog.
 * Downloads the APK directly via DownloadManager and launches FileProvider package installer.
 */
@Composable
fun UpdateCheckerEffect(
    owner: String = "zaidbnihani",
    repo: String = "QR-code-scanner",
    currentVersion: String = BuildConfig.VERSION_NAME
) {
    val context = LocalContext.current
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isDownloading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (owner.isNotBlank() && repo.isNotBlank() && owner != "YOUR_GITHUB_OWNER" && repo != "YOUR_GITHUB_REPO") {
            withContext(Dispatchers.IO) {
                val checker = UpdateChecker(owner, repo)
                updateInfo = checker.checkForUpdate(currentVersion)
            }
        }
    }

    updateInfo?.let { info ->
        AlertDialog(
            onDismissRequest = {
                if (!isDownloading) {
                    updateInfo = null
                }
            },
            title = {
                Text(
                    text = "تحديث جديد متوفر (${info.latestVersion})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isDownloading) "جاري تنزيل التحديث في الخلفية... سيتم فتح شاشة التثبيت عند الانتهاء."
                        else "يتوفر إصدار أحدث من التطبيق. هل ترغب بالتحديث الآن؟",
                        fontSize = 14.sp
                    )
                    if (info.releaseNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ملاحظات التحديث:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = info.releaseNotes,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (info.downloadUrl.isNotBlank()) {
                            if (info.downloadUrl.contains(".apk", ignoreCase = true)) {
                                ApkInstaller.downloadAndInstall(
                                    context = context,
                                    downloadUrl = info.downloadUrl,
                                    onDownloadingStarted = {
                                        isDownloading = true
                                        updateInfo = null
                                    }
                                )
                            } else {
                                // Fallback to browser if no direct APK asset was attached to the release
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl))
                                context.startActivity(intent)
                                updateInfo = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF)),
                    enabled = !isDownloading
                ) {
                    Text(if (isDownloading) "جاري التنزيل..." else "تحديث الآن", color = Color.White)
                }
            },
            dismissButton = {
                if (!isDownloading) {
                    TextButton(
                        onClick = { updateInfo = null }
                    ) {
                        Text("لاحقاً")
                    }
                }
            }
        )
    }
}
