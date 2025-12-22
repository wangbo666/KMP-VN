package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.CacheManager.LEASE_AGREEMENT
import com.kmp.vayone.data.CacheManager.PAWN_AGREEMENT
import com.kmp.vayone.data.OrderBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.getPhoneModel
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isPositive
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_ED190E
import theme.C_FC7700
import theme.C_FFBB48
import theme.C_FFD96E
import theme.C_FFF4E6
import theme.white

@Composable
fun OrderDetailScreen(
    orderId: Long?,
    isFromBatch: Boolean,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val orderViewModel = remember { OrderViewModel() }
    val loadingState by orderViewModel.loadingState.collectAsState()
    val item by orderViewModel.orderDetailResult.collectAsState()
    val order = remember(item) { item?.appOrderInfoDto ?: OrderBean() }
    val installList = remember(item) { item?.installmentRepaymentPlanDTOList }
    val leaseUrl = remember {
        LEASE_AGREEMENT + "userId=${item?.appOrderInfoDto?.userId}&productId=${item?.appOrderInfoDto?.productId}&amount=${item?.appOrderInfoDto?.loanAmount.toString()}"
    }
    val pawnUrl = remember {
        PAWN_AGREEMENT + "userId=${item?.appOrderInfoDto?.userId}&productId=${item?.appOrderInfoDto?.productId}&amount=${item?.appOrderInfoDto?.loanAmount.toString()}"
    }
    val buttonState by orderViewModel.buttonResult.collectAsState()
    var selectAmount by remember { mutableStateOf("") }

    LaunchedEffect(orderViewModel) {
        launch {
            orderViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            if (!isFromBatch) {
                orderViewModel.showButtonAndBorrow()
            }
        }
        launch {
            orderViewModel.installmentRepayResult.collect {
                it?.payUrl?.let { url ->
                    navigate(Screen.WebView(Strings["repayment"], url))
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                orderViewModel.getOrderDetail(orderId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(white).statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBar(
            title = Strings["order_detail"],
            tintColor = C_2B2621,
        ) {
            onBack()
        }
        LoadingBox(
            UiState.Success,
            modifier = Modifier.weight(1f)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 3.dp)
                        .height(61.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(C_FC7700, C_FFD96E)),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Spacer(
                        modifier = Modifier.padding(start = 11.dp, top = 17.dp)
                            .size(4.dp, 10.dp).background(
                                white,
                                RoundedCornerShape(10.dp)
                            )
                    )
                    Text(
                        text = order.productName.orEmpty(),
                        fontSize = 14.sp,
                        color = white,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(start = 9.dp, top = 15.dp)
                    )
                }
                LazyColumn(
                    modifier = Modifier.padding(top = 47.dp)
                        .fillMaxWidth()
                        .background(C_FFF4E6, RoundedCornerShape(16.dp))
                ) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 14.dp, bottom = 10.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["loan_amount"].format(order.currency.orEmpty())
                                        .replace("()", ""),
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = order.loanAmount.toAmountString(order.currencySymbol),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = C_2B2621,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["phone_model"],
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = getPhoneModel(),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["order_no"],
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = order.orderNo.orEmpty(),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["order_status"],
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = order.getStatusString(),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.isDue()) C_ED190E else C_FC7700,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["apply_date"],
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = item?.applyDateStr.orEmpty(),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                            if (order.isDue()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                        .height(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = Strings["due_date"],
                                        fontSize = 13.sp,
                                        color = C_7E7B79,
                                    )
                                    Text(
                                        text = item?.shouldRepayDateStr.orEmpty(),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = C_2B2621,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["loan_period"],
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = Strings["num_days"].format(order.timeLimit.toString()),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                            order.orderHandleFees?.forEach { fee ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                        .height(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fee.getFeeName(),
                                        fontSize = 13.sp,
                                        color = C_7E7B79,
                                    )
                                    Text(
                                        text = fee.amount.toAmountString(null),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = C_2B2621,
                                    )
                                }
                            }
                            if (item?.totalInstallmentServiceFee.isPositive()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                        .height(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = Strings["install_fee"],
                                        fontSize = 13.sp,
                                        color = C_7E7B79,
                                    )
                                    Text(
                                        text = item?.totalInstallmentServiceFee.toAmountString(order.currencySymbol),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = C_2B2621,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["interest_day"].format(item?.dayRateStr.orEmpty() + "%"),
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = item?.interestAmount.toAmountString(order.currencySymbol),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["actually_amount"].format(order.currency.orEmpty())
                                        .replace("()", ""),
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = item?.actualAmount.toAmountString(order.currencySymbol),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .height(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["account_receivable"],
                                    fontSize = 13.sp,
                                    color = C_7E7B79,
                                )
                                Text(
                                    text = item?.bankNo.orEmpty(),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_2B2621,
                                )
                            }
                        }
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp)) {
            MultiColoredText(
                Strings["product_detail_agreement"].format(
                    Strings["lease_contract"],
                    Strings["mortgage_contract"],
                ),
                listOf(
                    ColoredTextPart(Strings["lease_contract"], C_FC7700, 13.sp) {
                        navigate(
                            Screen.WebView(
                                Strings["lease_contract"], leaseUrl
                            )
                        )
                    },
                    ColoredTextPart(Strings["mortgage_contract"], C_FC7700, 13.sp) {
                        navigate(
                            Screen.WebView(
                                Strings["mortgage_contract"], pawnUrl
                            )
                        )
                    },
                ),
                defaultFontSize = 12.sp,
                defaultColor = C_B4B0AD,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 70.dp, end = 70.dp, top = 16.dp)
            )
            if (!installList.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                        .height(20.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings["select_amount"],
                        fontSize = 14.sp,
                        color = C_2B2621,
                    )
                    Text(
                        text = "",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = C_2B2621,
                    )
                }
            }
            if ((order.isDue() || order.isPendingRepayment()) && !isFromBatch) {
                Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                    if (buttonState == "1") {
                        Text(
                            text = Strings["repay"],
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 48.sp,
                            textAlign = TextAlign.Center,
                            color = if (order.isDue()) C_ED190E else C_FC7700,
                            modifier = Modifier.padding(end = 10.dp).height(48.dp)
                                .weight(1f)
                                .background(white, RoundedCornerShape(30.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (order.isDue()) C_ED190E else C_FC7700,
                                    RoundedCornerShape(30.dp)
                                )
                                .clip(RoundedCornerShape((30.dp)))
                                .clickable {
                                    if (!installList.isNullOrEmpty() && installList.none { it1 -> !it1.isSettle() && it1.isSelect }) {
                                        toast(true, Strings["toast_repayment_select"])
                                        return@clickable
                                    }
                                    if (!installList.isNullOrEmpty()) {
                                        orderViewModel.installmentRepay(
                                            ParamBean(
                                                orderNo = order.orderNo,
                                                planNumList = installList.filter { it1 -> !it1.isSettle() && it1.isSelect }
                                                    .map { it.planPart }
                                            )
                                        )
                                        return@clickable
                                    }
                                    val payGoUrl = order.payGoUrl
                                    if (payGoUrl.isNullOrBlank()) {
                                        navigate(
                                            Screen.Repayment(
                                                order.id.toString(),
                                                order.orderNo,
                                                order.actualNeedRepayAmount
                                            )
                                        )
                                        return@clickable
                                    }
                                    navigate(Screen.WebView(Strings["repayment"], pawnUrl))
                                },
                        )
                    }
                    Text(
                        text = if (buttonState == "1") Strings["borrow_and_repay"] else Strings["repay"],
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 48.sp,
                        textAlign = TextAlign.Center,
                        color = white,
                        modifier = Modifier.padding(end = 10.dp).height(48.dp)
                            .weight(1f)
                            .background(
                                if (order.isDue()) C_ED190E else C_FC7700,
                                RoundedCornerShape(30.dp)
                            )
                            .clip(RoundedCornerShape((30.dp)))
                            .clickable {
                                if (!installList.isNullOrEmpty() && installList.none { it1 -> !it1.isSettle() && it1.isSelect }) {
                                    toast(true, Strings["toast_repayment_select"])
                                    return@clickable
                                }
                                if (buttonState == "1") {
                                    orderViewModel.repayAndBorrow(order.orderId)
                                }
                                if (!installList.isNullOrEmpty()) {
                                    orderViewModel.installmentRepay(
                                        ParamBean(
                                            orderNo = order.orderNo,
                                            planNumList = installList.filter { it1 -> !it1.isSettle() && it1.isSelect }
                                                .map { it.planPart }
                                        )
                                    )
                                    return@clickable
                                }
                                val payGoUrl = order.payGoUrl
                                if (payGoUrl.isNullOrBlank()) {
                                    navigate(
                                        Screen.Repayment(
                                            order.id.toString(),
                                            order.orderNo,
                                            order.actualNeedRepayAmount
                                        )
                                    )
                                    return@clickable
                                }
                                navigate(Screen.WebView(Strings["repayment"], pawnUrl))
                            },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreOrderDetail() {
    OrderDetailScreen(0, false, onBack = {}) {

    }
}