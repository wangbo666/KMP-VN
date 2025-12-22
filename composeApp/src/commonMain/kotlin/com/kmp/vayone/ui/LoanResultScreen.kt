package com.kmp.vayone.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.SignPageParams
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_524F4C
import theme.C_5AE109
import theme.C_6A707D
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_E3E0DD
import theme.C_ED4744
import theme.C_F9A528
import theme.C_FC7700
import theme.C_FCFCFC
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.loan_check
import vayone.composeapp.generated.resources.loan_error
import vayone.composeapp.generated.resources.loan_fail
import vayone.composeapp.generated.resources.loan_fail_bg
import vayone.composeapp.generated.resources.loan_success
import vayone.composeapp.generated.resources.loan_success_bg

@Composable
fun LoanResultScreen(
    params: SignPageParams,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val productViewModel = remember { ProductViewModel() }
    val loadingState by productViewModel.loadingState.collectAsState()
    val loanResult by productViewModel.loanResult.collectAsState()

    var timeLeft by remember { mutableStateOf(10) }
    LaunchedEffect(key1 = timeLeft) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        // 结束后恢复状态
        navigate(Screen.Home())
    }
    LaunchedEffect(productViewModel) {
        launch {
            productViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            if (params.productList != null) {
                productViewModel.togetherLoan(params)
            } else {
                productViewModel.singleLoan(params)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(C_FCFCFC).statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBar(
            title = Strings["loan_application"],
            tintColor = C_2B2621,
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            navigate(Screen.Home())
        }
        LoadingBox(UiState.Success, modifier = Modifier.weight(1f).padding(top = 8.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    item {
                        if (loanResult == null) {
                            Column(
                                modifier = Modifier.padding(horizontal = 27.dp)
                                    .fillMaxWidth()
                                    .background(white, RoundedCornerShape(16.dp))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(199.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.loan_fail_bg),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Image(
                                        painter = painterResource(Res.drawable.loan_fail),
                                        contentDescription = null,
                                        modifier = Modifier.size(81.dp)
                                    )
                                }
                                Text(
                                    text = Strings["submit_failed"],
                                    fontSize = 20.sp,
                                    lineHeight = 23.sp,
                                    color = C_ED4744,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = Strings["submit_failed_desc"],
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = C_6A707D,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .height(0.5.dp)
                                ) {
                                    drawLine(
                                        color = C_E3E0DD,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(10f, 10f), // 实线长度, 间隔长度
                                            0f
                                        )
                                    )
                                }
                                Text(
                                    text = Strings["loan_result"],
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp,
                                    color = C_F9A528,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 8.dp, end = 8.dp, bottom = 49.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(horizontal = 27.dp)
                                    .fillMaxWidth()
                                    .background(white, RoundedCornerShape(16.dp))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(199.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.loan_success_bg),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Image(
                                        painter = painterResource(Res.drawable.loan_success),
                                        contentDescription = null,
                                        modifier = Modifier.size(81.dp)
                                    )
                                }
                                Text(
                                    text = Strings["submit_success"],
                                    fontSize = 20.sp,
                                    lineHeight = 23.sp,
                                    color = C_5AE109,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = Strings["application_submit_desc"],
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = C_7E7B79,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .height(0.5.dp)
                                ) {
                                    drawLine(
                                        color = C_E3E0DD,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(10f, 10f), // 实线长度, 间隔长度
                                            0f
                                        )
                                    )
                                }
                                loanResult?.forEach { item ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(start = 10.dp, end = 10.dp, bottom = 16.dp)
                                    ) {
                                        Text(
                                            text = item.productName.orEmpty(),
                                            fontSize = 13.sp,
                                            color = C_FC7700,
                                            lineHeight = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(top = 24.dp)
                                                .height(29.dp)
                                        ) {
                                            Text(
                                                text = Strings["l_amount"],
                                                fontSize = 13.sp,
                                                color = C_7E7B79,
                                                lineHeight = 19.sp,
                                                fontWeight = FontWeight.Normal,
                                            )
                                            Text(
                                                text = item.loanAmount.toAmountString(item.currencySymbol),
                                                fontSize = 16.sp,
                                                color = C_FC7700,
                                                lineHeight = 19.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(start = 16.dp)
                                            )
                                        }
                                        Image(
                                            painter = painterResource(if (item.pushStatus == 200) Res.drawable.loan_check else Res.drawable.loan_error),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                                .align(Alignment.CenterEnd),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                MultiColoredText(
                    Strings["back_to_home_tips"].format(
                        timeLeft.toString()
                    ),
                    listOf(
                        ColoredTextPart(timeLeft.toString(), C_524F4C, 18.sp) {
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
                    defaultColor = C_B4B0AD,
                    defaultFontSize = 14.sp,
                    defaultFontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = Strings["back_to_home"],
                    color = white,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 48.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                        .background(C_FC7700, RoundedCornerShape((30.dp)))
                        .clip(RoundedCornerShape((30.dp)))
                        .clickable {
                            navigate(Screen.Home())
                        }

                )
            }
        }
    }
}

@Preview
@Composable
fun PreLoanResult() {
    LoanResultScreen(SignPageParams(), onBack = {}) {}
}

