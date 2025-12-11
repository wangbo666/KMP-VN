package com.kmp.vayone.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.calculateAmount
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.RepaymentViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_132247
import theme.C_190E30
import theme.C_2B2621
import theme.C_3E4845
import theme.C_E3E0DD
import theme.C_E5E0DC
import theme.C_F8F4F0
import theme.C_FC7700
import theme.C_FFD8AE
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.batch_check
import vayone.composeapp.generated.resources.batch_uncheck
import vayone.composeapp.generated.resources.mine_right
import vayone.composeapp.generated.resources.product_icon

@Composable
fun BatchRepayment(
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val viewModel = remember { RepaymentViewModel() }
    val loadingState by viewModel.loadingState.collectAsState()
    val togetherRepaymentList by viewModel.togetherRepaymentList.collectAsState()
    var totalAmount by remember {
        mutableStateOf(
            calculateAmount(togetherRepaymentList?.filter { it.isCheck }
                ?.map { it.actualRepayAmount })
        )
    }
//    val totalNum by viewModel.togetherRepaymentList.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getTogetherRepaymentList()
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(white).statusBarsPadding(), topBar = {
        TopBar(Strings["batch_repayment_orders"]) {
            onBack()
        }
    }, bottomBar = {
        Column(modifier = Modifier.background(white).navigationBarsPadding()) {
            Spacer(
                modifier = Modifier.fillMaxWidth().height(0.5.dp).background(C_E3E0DD)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["total_repayment"],
                    color = C_190E30,
                    fontSize = 14.sp,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = totalAmount,
                    color = C_2B2621,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["together_num"],
                    color = C_190E30,
                    fontSize = 14.sp,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = (togetherRepaymentList?.size ?: 0).toString(),
                    color = C_2B2621,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }
            Text(
                text = Strings["batch_repayment"],
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = white,
                lineHeight = 48.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .height(48.dp).background(color = C_FC7700, shape = RoundedCornerShape(30.dp))
                    .clip(RoundedCornerShape(30.dp))
                    .clickable {

                    }
            )
        }
    }) { paddingValues ->
        LoadingBox(
            state = loadingState,
            modifier = Modifier.background(white)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
                    .background(color = C_F8F4F0, shape = RoundedCornerShape(16.dp))
                    .border(width = 1.dp, color = C_E5E0DC, shape = RoundedCornerShape(16.dp))
                    .padding(top = 12.dp),
            ) {

                togetherRepaymentList?.forEach {
                    item {
                        BatchRepaymentItem(navigate, it) {
                            totalAmount =
                                calculateAmount(togetherRepaymentList?.filter { it.isCheck }
                                    ?.map { it.actualRepayAmount })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BatchRepaymentItem(
    navigate: (Screen) -> Unit,
    productBean: ProductBean,
    checkAction: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 13.dp, end = 13.dp, bottom = 8.dp)
            .wrapContentHeight()
            .background(shape = RoundedCornerShape(12.dp), color = white)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.product_icon), contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                    .size(38.dp)
            )
            Text(
                text = productBean.productName ?: "",
                fontSize = 14.sp,
                color = C_132247,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
                    .weight(1f)
            )
            Image(
                painter = painterResource(if (productBean.isCheck) Res.drawable.batch_check else Res.drawable.batch_uncheck),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp, top = 4.dp, bottom = 4.dp, end = 12.dp)
                    .size(24.dp)
                    .clickable {
                        productBean.isCheck = !productBean.isCheck
                        checkAction()
                    }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = C_FFD8AE,
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["total_repayment"],
                    color = C_3E4845,
                    fontSize = 14.sp,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = productBean.actualRepayAmount.toAmountString(productBean.currencySymbol),
                    color = C_2B2621,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    lineHeight = 16.sp,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = Strings["details"],
                    color = C_3E4845,
                    fontSize = 14.sp,
                )
                Image(
                    modifier = Modifier.size(24.dp)
                        .clickable {

                        },
                    painter = painterResource(Res.drawable.mine_right),
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreBatch() {
    BatchRepayment({}) {}
}