package com.kmp.vayone.ui.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_524F4C
import theme.C_75707E
import theme.C_7E7B79
import theme.C_ED190E
import theme.C_ED4744
import theme.C_F5F5F5
import theme.C_F8F4F0
import theme.C_FC7700
import theme.C_FFBB48
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.empty_home_order
import vayone.composeapp.generated.resources.home_order1
import vayone.composeapp.generated.resources.home_order2
import vayone.composeapp.generated.resources.home_order3
import vayone.composeapp.generated.resources.home_order_tip


@Composable
fun OrderPage(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    navigate: (Screen) -> Unit,
) {
    val mainViewModel = remember { MainViewModel() }
    val loadingState by mainViewModel.loadingState.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()
    val authData by mainViewModel.homeAuthResult.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                mainViewModel.getHomeAuthData(false)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LoadingBox(
        state = loadingState, modifier = Modifier.fillMaxSize().padding(top = 38.dp), onRetry = {
            mainViewModel.getHomeAuthData(false)
        }) {
        PullToRefreshBox(
            isRefreshing = loadingState != UiState.Success, onRefresh = {
                mainViewModel.getHomeAuthData(false)
            }) {
            EmptyOrderPage(authData?.repayProducts.isNullOrEmpty())
            val list = authData?.repayProducts?.filter { it1 ->
                it1.isPendingRepayment()||it1.isDue()
            }
            val size = list?.size ?: 0
            OrderPageBatch(
                size > 0 && authData?.showMultipleRepaySign == 1,
                size.toString(),
                navigate
            )
            HomeOrderList(list ?: listOf(), navigate)
        }
    }
}

@Composable
fun EmptyOrderPage(show: Boolean = true) {
    if (!show) return
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_home_order),
            contentDescription = null,
            modifier = Modifier.padding(top = 25.dp)
                .size(203.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp)
                .border(
                    width = 1.dp,
                    color = _root_ide_package_.theme.C_E5E0DC,
                    RoundedCornerShape(16.dp)
                ).background(shape = RoundedCornerShape(16.dp), color = C_F8F4F0)
                .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 28.dp)
        ) {
            Text(
                text = "“${Strings["benefits_of_paying"]}”",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = C_524F4C,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 16.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 20.dp)
                    .background(white, RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 10.dp, top = 2.dp).weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = Strings["home_order_title1"],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = C_FC7700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                    )
                    Text(
                        text = Strings["home_order_desc1"],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = C_75707E,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.home_order1),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 15.dp, bottom = 15.dp, end = 16.dp)
                        .size(54.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(white, RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 10.dp, top = 2.dp).weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = Strings["home_order_title2"],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = C_FC7700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = Strings["home_order_desc2"],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = C_75707E,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.home_order2),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 15.dp, bottom = 15.dp, end = 16.dp)
                        .size(54.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(white, RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 10.dp, top = 2.dp).weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = Strings["home_order_title3"],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = C_FC7700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                    )
                    Text(
                        text = Strings["home_order_desc3"],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = C_75707E,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.home_order3),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 15.dp, bottom = 15.dp, end = 16.dp)
                        .size(54.dp)
                )
            }
        }
    }
}

@Composable
fun OrderPageBatch(
    show: Boolean = true, num: String = "2",
    navigate: (Screen) -> Unit,
) {
    if (!show) return
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(69.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            C_FFBB48,
                            C_FC7700,
                        )
                    ), RoundedCornerShape(50.dp)
                ),
        ) {
            MultiColoredText(
                fullText = Strings["home_order_num"].format(num),
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp)
                    .align(Alignment.CenterVertically),
                defaultColor = white,
                textAlign = TextAlign.Center,
                coloredParts = listOf(
                    ColoredTextPart(num, white, 14.sp, FontWeight.Bold)
                )
            )
            Text(
                text = Strings["batch_repayment"],
                color = C_524F4C,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp,
                modifier = Modifier.padding(end = 9.dp).height(50.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                white, C_FFBB48
                            )
                        ),
                        RoundedCornerShape(30.dp)
                    ).padding(horizontal = 24.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        navigate(Screen.BatchRepayment)
                    }
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 50.dp)
                .fillMaxWidth()
                .background(C_FFF4E6, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.home_order_tip),
                contentDescription = null,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp, bottom = 5.dp)
                    .size(38.dp)
            )
            Text(
                Strings["batch_repayment_tips"],
                fontSize = 11.sp,
                color = Color(0xFFF68205),
                lineHeight = 11.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f).padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun HomeOrderList(
    list: List<ProductBean> = arrayListOf(),
    navigate: (Screen) -> Unit,
) {
    if (list.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .border(width = 1.dp, color = theme.C_E5E0DC, RoundedCornerShape(16.dp))
            .background(shape = RoundedCornerShape(16.dp), color = C_F8F4F0)
            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "“${Strings["orders"]}”",
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            textAlign = TextAlign.Center,
            color = C_524F4C,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        list.forEach { product ->
            HomeOrderItem(product, navigate)
        }
    }
}

@Composable
fun HomeOrderItem(item: ProductBean, navigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .background(white, RoundedCornerShape(12.dp))
            .padding(bottom = 10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = when {
                    item.isPendingCash() -> Strings["pending_cash"]
                    item.isRepaymentProcessing() -> Strings["repayment_processing"]
                    item.isPendingRepayment() -> Strings["pending_repayment"]
                    else -> Strings["overdue"]
                },
                modifier = Modifier.weight(1f)
                    .padding(start = 12.dp, top = 10.dp),
                textAlign = TextAlign.Start,
                color = if (item.isDue()) C_ED190E else C_FC7700,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 28.sp,
            )
            Text(
                text = item.productName ?: "",
                modifier = Modifier.weight(1f)
                    .padding(end = 12.dp, top = 10.dp),
                textAlign = TextAlign.End,
                color = C_524F4C,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 28.sp,
            )
        }
        Text(
            text = when {
                item.isPendingCash() -> Strings["pending_cash_desc"]
                item.isRepaymentProcessing() -> Strings["pending_repayment_desc"]
                item.isPendingRepayment() -> Strings["pending_repayment_desc"]
                else -> Strings["overdue_desc"]
            },
            modifier = Modifier.wrapContentHeight().padding(start = 12.dp, end = 12.dp, top = 4.dp),
            textAlign = TextAlign.Start,
            color = if (item.isDue()) C_ED4744 else C_7E7B79,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        )
        Column(
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                .background(C_F5F5F5, RoundedCornerShape(8.dp))
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (item.isPendingCash()) Strings["l_amount"] else Strings["total_repayment"],
                    modifier = Modifier.weight(1f)
                        .padding(start = 8.dp, top = 8.dp),
                    textAlign = TextAlign.Start,
                    color = C_7E7B79,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                )
                Text(
                    text = if (item.isPendingCash()) item.loanAmount.toAmountString(item.currencySymbol)
                    else item.actualRepayAmount.toAmountString(
                        item.currencySymbol
                    ),
                    modifier = Modifier.weight(1f)
                        .padding(end = 8.dp, top = 8.dp),
                    textAlign = TextAlign.End,
                    color = C_524F4C,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(
                    text = if (item.isPendingCash()) Strings["apply_date"] else Strings["due_date"],
                    modifier = Modifier.weight(1f)
                        .padding(start = 8.dp, top = 4.dp),
                    textAlign = TextAlign.Start,
                    color = C_7E7B79,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
                Text(
                    text = (if (item.isPendingCash()) item.applyDateStr else item.repayTimeStr)
                        ?: "",
                    modifier = Modifier.weight(1f)
                        .padding(end = 8.dp, top = 4.dp),
                    textAlign = TextAlign.End,
                    color = C_524F4C,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                )
            }
        }
        if (item.isPendingRepayment() || item.isDue()) {
            Text(
                text = Strings["repay"],
                fontSize = 14.sp,
                lineHeight = 36.sp,
                color = white,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 21.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        if (item.isDue()) C_ED190E else C_FC7700,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable {
//                    navigate()
                    },
            )
        }
    }
}

@Preview
@Composable
fun PreOrderPage() {
    HomeOrderItem(ProductBean()) {}
}