package com.example.qr

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationAttributes
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScannedQrItem(
    val id: Long = System.currentTimeMillis(),
    val rawValue: String,
    val isUrl: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class QrUiState(
    val lastScannedValue: String? = null,
    val isAutoOpenEnabled: Boolean = true,
    val isFlashOn: Boolean = false,
    val scannedHistory: List<ScannedQrItem> = emptyList(),
    val statusText: String = "ضع الـ QR code داخل المربع الأزرق",
    val isProcessing: Boolean = false
)

class QrScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QrUiState())
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    private var lastScannedTime: Long = 0L
    private var lastScannedString: String? = null

    // Pre-warmed ML Kit scanner for gallery processing
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_ALL_FORMATS)
        .build()
    private val galleryScanner = BarcodeScanning.getClient(options)

    fun toggleAutoOpen() {
        _uiState.update { it.copy(isAutoOpenEnabled = !it.isAutoOpenEnabled) }
    }

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    }

    fun onBarcodeDetected(context: Context, rawValue: String) {
        val now = System.currentTimeMillis()
        // Debounce exact same value for 2.5 seconds to prevent spam
        if (rawValue == lastScannedString && now - lastScannedTime < 2500) {
            return
        }

        lastScannedString = rawValue
        lastScannedTime = now

        val formattedUrl = formatToValidUrl(rawValue)
        val isUrl = isUrlPattern(formattedUrl)

        val newItem = ScannedQrItem(rawValue = rawValue, isUrl = isUrl)

        _uiState.update { state ->
            val updatedHistory = (listOf(newItem) + state.scannedHistory).distinctBy { it.rawValue }.take(20)
            state.copy(
                lastScannedValue = rawValue,
                scannedHistory = updatedHistory,
                statusText = if (isUrl) "تم العثور على رابط!" else "تم مسح الرمز بنجاح"
            )
        }

        triggerVibration(context)

        if (_uiState.value.isAutoOpenEnabled) {
            openLinkInBrowser(context, rawValue)
        }
    }

    fun openLinkInBrowser(context: Context, rawContent: String) {
        viewModelScope.launch {
            try {
                val targetUrl = formatToValidUrl(rawContent)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                _uiState.update { it.copy(statusText = "تم فتح الرابط تلقائياً في المتصفح 🚀") }
            } catch (e: Exception) {
                // Fallback to web search if not a direct URL format
                try {
                    val searchUrl = "https://www.google.com/search?q=" + Uri.encode(rawContent)
                    val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(searchIntent)
                } catch (_: Exception) {
                    _uiState.update { it.copy(statusText = "تعذر فتح الرابط") }
                }
            }
        }
    }

    fun processBitmapFromGallery(context: Context, bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        galleryScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val firstBarcode = barcodes.firstOrNull()
                    val rawValue = firstBarcode?.rawValue ?: firstBarcode?.url?.url
                    if (!rawValue.isNullOrBlank()) {
                        onBarcodeDetected(context, rawValue)
                    } else {
                        _uiState.update { it.copy(statusText = "لم يتم العثور على QR code في الصورة") }
                    }
                } else {
                    _uiState.update { it.copy(statusText = "لم يتم العثور على QR code في الصورة") }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(statusText = "فشل في تحليل الصورة") }
            }
    }

    private fun formatToValidUrl(input: String): String {
        val trimmed = input.trim()
        return if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            trimmed
        } else if (Patterns.WEB_URL.matcher(trimmed).matches() || trimmed.contains(".com") || trimmed.contains(".org") || trimmed.contains(".net") || trimmed.contains(".io") || trimmed.contains(".app") || trimmed.contains(".co")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }

    private fun isUrlPattern(input: String): Boolean {
        return input.startsWith("http://") || input.startsWith("https://") || Patterns.WEB_URL.matcher(input).matches()
    }

    private fun triggerVibration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE),
                    VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(80)
            }
        } catch (_: Exception) {
            // Ignore if permission or hardware not available
        }
    }
}
