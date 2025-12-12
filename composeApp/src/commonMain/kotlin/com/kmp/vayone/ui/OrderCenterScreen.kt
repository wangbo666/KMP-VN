package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.OrderBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.date
import com.kmp.vayone.util.dateTimeFormatAllBack
import com.kmp.vayone.util.ddMMyyyy
import com.kmp.vayone.util.format
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.OrderViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_202125
import theme.C_2B2621
import theme.C_505464
import theme.C_B4B0AD
import theme.C_ED190E
import theme.C_FC7700
import theme.C_FFBB48
import theme.C_FFE3BF
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.empty_order
import vayone.composeapp.generated.resources.mine_right

@Composable
fun OrderCenterScreen(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val orderViewModel = remember { OrderViewModel() }
    val loadingState by orderViewModel.loadingState.collectAsState()
    val orderList by orderViewModel.orderList.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                orderViewModel.getOrderList()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(white)) {
        Column(
            modifier = Modifier.fillMaxWidth().background(
                brush = Brush.horizontalGradient(
                    listOf(
                        C_FC7700, C_FFBB48
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
        ) {
            TopBar(
                title = Strings["order_center"],
                tintColor = white,
                modifier = Modifier.statusBarsPadding().fillMaxWidth()
                    .height(44.dp)
            ) {
                onBack()
            }
            Text(
                text = Strings["order_center"],
                fontWeight = FontWeight.Bold,
                color = white,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 14.dp)
            )
            Text(
                text = Strings["view_order"],
                fontWeight = FontWeight.Normal,
                color = white,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }
        LoadingBox(
            loadingState, onRetry = {}, modifier = Modifier.weight(1f),
        ) {
            if (orderList.isEmpty()) {
                EmptyOrder()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    items(orderList.size, key = { it }) {
                        OrderItem(orderList[it])
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(item: OrderBean) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
            .background(white, RoundedCornerShape(16.dp))
            .border(width = 3.dp, color = C_FFE3BF, RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = item.getStatusString(),
            color = if (item.isDue()) C_ED190E else C_2B2621,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 35.sp,
            modifier = Modifier.background(
                color = C_FFE3BF,
                RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
            )
                .padding(horizontal = 16.dp)
                .align(Alignment.TopStart)
        )
        Image(
            painter = painterResource(Res.drawable.mine_right),
            contentDescription = null,
            modifier = Modifier.padding(top = 5.dp, end = 16.dp).size(24.dp)
                .align(Alignment.TopEnd)
                .clickable {

                }
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 45.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    Strings["l_amount"],
                    fontSize = 13.sp,
                    color = C_505464,
                    lineHeight = 19.sp,
                )
                Text(
                    item.loanAmount.toAmountString(item.currencySymbol),
                    fontSize = 16.sp,
                    color = C_202125,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    Strings["loan_period"],
                    fontSize = 13.sp,
                    color = C_505464,
                    lineHeight = 19.sp,
                )
                Text(
                    Strings["num_days"].format(item.timeLimit.toString()),
                    fontSize = 14.sp,
                    color = C_202125,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    Strings["apply_date"],
                    fontSize = 13.sp,
                    color = C_505464,
                    lineHeight = 19.sp,
                )
                Text(
                    item.createTime.date(dateTimeFormatAllBack).ddMMyyyy(),
                    fontSize = 14.sp,
                    color = C_202125,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun EmptyOrder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_order),
            contentDescription = null,
            modifier = Modifier.size(155.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = Strings["empty_order"],
            fontSize = 12.sp,
            color = C_B4B0AD,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .padding(top = 9.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun PreOrderCenter() {
    EmptyOrder()
}