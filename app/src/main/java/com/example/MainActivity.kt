package com.example

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr.CameraScannerView
import com.example.qr.QrScannerViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.update.UpdateCheckerEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide system bars for true full screen (Immersive Mode)
        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        }

        setContent {
            MyApplicationTheme(darkTheme = true) {
                FastQrApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FastQrApp(
    modifier: Modifier = Modifier,
    viewModel: QrScannerViewModel = viewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Automatically check for GitHub updates silently on app open
    UpdateCheckerEffect()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraPermissionState.status.isGranted) {
            MainScannerScreen(viewModel = viewModel)
        } else {
            CameraPermissionScreen(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScannerScreen(viewModel: QrScannerViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showHistorySheet by remember { mutableStateOf(false) }

    // Photo picker for selecting image from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    viewModel.processBitmapFromGallery(context, bitmap)
                } else {
                    Toast.makeText(context, "تعذر تحميل الصورة", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطأ في فتح الصورة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Ultra-Fast Camera Preview with On-Device ML Kit Barcode Analyzer
        CameraScannerView(
            isFlashOn = uiState.isFlashOn,
            onQrDetected = { qrText ->
                viewModel.onBarcodeDetected(context, qrText)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Simple Layout: Centered Blue Square as explicitly requested ("مربع صغير باللون الأزرق بالنص")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cornerLength = 40.dp.toPx()
                        val strokeWidth = 4.dp.toPx()
                        val cornerRadius = 20.dp.toPx()
                        val color = Color(0xFF0066FF)
                        val width = size.width
                        val height = size.height

                        // Draw inner transparent blue background
                        drawRoundRect(
                            color = Color(0x1A0066FF),
                            size = Size(width, height),
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )

                        // Top Left Corner
                        drawPath(
                            path = Path().apply {
                                moveTo(0f, cornerLength)
                                lineTo(0f, cornerRadius)
                                quadraticTo(0f, 0f, cornerRadius, 0f)
                                lineTo(cornerLength, 0f)
                            },
                            color = color,
                            style = Stroke(width = strokeWidth)
                        )

                        // Top Right Corner
                        drawPath(
                            path = Path().apply {
                                moveTo(width - cornerLength, 0f)
                                lineTo(width - cornerRadius, 0f)
                                quadraticTo(width, 0f, width, cornerRadius)
                                lineTo(width, cornerLength)
                            },
                            color = color,
                            style = Stroke(width = strokeWidth)
                        )

                        // Bottom Left Corner
                        drawPath(
                            path = Path().apply {
                                moveTo(0f, height - cornerLength)
                                lineTo(0f, height - cornerRadius)
                                quadraticTo(0f, height, cornerRadius, height)
                                lineTo(cornerLength, height)
                            },
                            color = color,
                            style = Stroke(width = strokeWidth)
                        )

                        // Bottom Right Corner
                        drawPath(
                            path = Path().apply {
                                moveTo(width, height - cornerLength)
                                lineTo(width, height - cornerRadius)
                                quadraticTo(width, height, width - cornerRadius, height)
                                lineTo(width - cornerLength, height)
                            },
                            color = color,
                            style = Stroke(width = strokeWidth)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Target QR",
                        tint = Color(0xFF0066FF).copy(alpha = 0.85f),
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPermissionScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color(0xFF0066FF).copy(alpha = 0.2f), CircleShape)
                .border(2.dp, Color(0xFF0066FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = Color(0xFF0066FF),
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "أسرع ماسح QR Code بالعالم",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "قم بتمكين الكاميرا لبدء المسح الفوري. التطبيق يعمل بنظام ML Kit محلي على الجهاز لفتح الروابط مباشرة في المتصفح.",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("grant_camera_permission_button")
        ) {
            Text(
                text = "افتح الكاميرا وابدأ المسح",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


