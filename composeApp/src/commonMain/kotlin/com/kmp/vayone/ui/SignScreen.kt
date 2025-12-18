package com.kmp.vayone.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.ui.widget.SignPageParams
import com.kmp.vayone.ui.widget.SignatureController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import com.kmp.vayone.SignatureFileManager
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.getSignatureFileManager
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.openSystemPermissionSettings
import com.kmp.vayone.postAllPermissions
import com.kmp.vayone.ui.widget.ConfirmDialog
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.util.format
import com.kmp.vayone.util.log
import com.kmp.vayone.util.permissionToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_E3E0DD
import theme.C_FC7700
import theme.white

@Composable
fun SignScreen(
    signPageParams: SignPageParams,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val controller = remember { SignatureController() }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var permissionText by mutableStateOf("")
    var showPermissionGuideDialog by remember { mutableStateOf(false) }

    // 控制是否显示提示 "Please sign"
    val showHint = controller.isEmpty()

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBar(Strings["sign"]) {
            if (signPageParams.isShowBackHome) {
                CacheManager.setSignBackHome(true)
                navigate(Screen.Home())
            } else {
                onBack()
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .onSizeChanged { canvasSize = it }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { controller.start(it) },
                            onDrag = { change, _ ->
                                change.consume()
                                controller.move(change.position)
                            },
                            onDragEnd = { controller.end() }
                        )
                    }
            ) {
                // 绘制已保存的笔画
                controller.strokes.forEach { stroke ->
                    val path = Path().apply {
                        if (stroke.points.isNotEmpty()) {
                            moveTo(stroke.points.first().x, stroke.points.first().y)
                            stroke.points.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                    }
                    drawPath(
                        path, Color(stroke.color), style = Stroke(
                            stroke.width,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                // 绘制当前正在画的笔画
                if (controller.currentPoints.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(
                            controller.currentPoints.first().x,
                            controller.currentPoints.first().y
                        )
                        controller.currentPoints.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(
                        path,
                        Color.Black,
                        style = Stroke(8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            if (showHint) {
                Text(
                    text = Strings["please_sign_name"],
                    modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.Center),
                    color = C_E3E0DD,
                    fontSize = 30.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .height(48.dp)
        ) {
            if (signPageParams.isShowBackHome) {
                Text(
                    text = Strings["back_to_home"],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = white,
                    lineHeight = 48.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(end = 10.dp)
                        .weight(1f)
                        .background(C_FC7700, RoundedCornerShape(30.dp))
                        .clip(RoundedCornerShape(30.dp))
                        .clickable {
                            CacheManager.setSignBackHome(true)
                            navigate(Screen.Home())
                        }
                )
            }
            Text(
                text = Strings["sign_borrow"],
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = white,
                lineHeight = 48.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(C_FC7700, RoundedCornerShape(30.dp))
                    .clip(RoundedCornerShape(30.dp))
                    .clickable {
                        if (showHint) {
                            toast(true, Strings["please_sign"])
                            return@clickable
                        }
                        scope.launch {
                            postAllPermissions(refuseAction = { isNever, permissions ->
                                if (isNever) {
                                    permissionText =
                                        permissions.joinToString { it.permissionToString() }
                                    showPermissionGuideDialog = true
                                }
                            }) {
                                scope.launch {
                                    val fileManager = getSignatureFileManager()
                                    val originalSize = SignatureFileManager.Size(
                                        canvasSize.width,
                                        canvasSize.height
                                    )

                                    // 获取裁剪区域
                                    val cropRect = controller.getCropRect(
                                        canvasSize.width.toFloat(),
                                        canvasSize.height.toFloat()
                                    )

                                    val path = withContext(Dispatchers.IO) {
                                        fileManager.saveSignatureImage(
                                            strokes = controller.strokes,
                                            cropRect = cropRect,
                                            originalSize = originalSize
                                        )
                                    }
                                    "SignPath:$path".log()
                                    if (path != null) {
                                        onBack()
                                        navigate(Screen.LoanResult(signPageParams.apply {
                                            signPath = path
                                        }))
                                    }
                                }
                            }
                        }
                    }
            )
        }
    }
    ConfirmDialog(
        showPermissionGuideDialog,
        title = Strings["dialog_permission_title"].format(permissionText),
        content = "",
        cancel = Strings["closed"],
        confirm = Strings["sure"],
        confirmAction = {
            openSystemPermissionSettings()
        }
    ) {
        showPermissionGuideDialog = false
    }
}

@Preview
@Composable
fun PreSign() {
    SignScreen(SignPageParams(isShowBackHome = true), onBack = {}) {}
}
