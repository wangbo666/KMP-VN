package com.kmp.vayone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.kmp.vayone.data.CacheManager.AGREEMENT_ABOUT
import com.kmp.vayone.data.CacheManager.LEASE_AGREEMENT
import com.kmp.vayone.data.CacheManager.PAWN_AGREEMENT
import com.kmp.vayone.data.CacheManager.getLoginInfo
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.getPhoneModel
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isPositive
import com.kmp.vayone.util.maskString
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_003BC3
import theme.C_40495C
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_E3E0DD
import theme.C_FC7700
import theme.C_FCFCFC
import theme.C_FFBB48
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.mine_right

@Composable
fun ProductScreen(
    productDetail: ProductDetailBean,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val productViewModel = remember { ProductViewModel() }
    var product by remember { mutableStateOf(productDetail) }
    LaunchedEffect(productViewModel) {
        launch {
            productViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            productViewModel.productDetailResult.collect {
                it?.let { product = it }
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                productViewModel.getProductDetail(
                    productDetail.id.toString(),
                    productDetail.maxLoanAmount
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(C_FCFCFC).navigationBarsPadding()) {
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
                title = Strings["loan_application"],
                tintColor = white,
                modifier = Modifier.statusBarsPadding().fillMaxWidth()
                    .height(44.dp)
            ) {
                onBack()
            }
            Row(
                modifier = Modifier.wrapContentWidth().padding(top = 8.dp).height(38.dp)
                    .background(C_FFF4E6, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .padding(horizontal = 17.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        navigate(Screen.WebView(Strings["about_lease"], AGREEMENT_ABOUT))
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["about_lease"],
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = C_003BC3,
                )
                IconButton(
                    onClick = {
                        navigate(Screen.WebView(Strings["about_lease"], AGREEMENT_ABOUT))
                    },
                    modifier = Modifier.padding(start = 6.dp).size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.mine_right),
                        contentDescription = Strings["about_lease"],
                        tint = C_FC7700,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Text(
                text = Strings["l_amount"],
                color = white,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 17.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = product.loanAmount.toAmountString(product.currencySymbol),
                color = white,
                fontSize = 30.sp,
                lineHeight = 30.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["loan_period_days"],
                            fontSize = 13.sp,
                            color = C_7E7B79,
                        )
                        Text(
                            text = product.timeLimit.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_524F4C,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["actually_amount"].replace("(%s)", ""),
                            fontSize = 13.sp,
                            color = C_7E7B79,
                        )
                        Text(
                            text = product.actualAmount.toAmountString(product.currencySymbol),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_524F4C,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["interest_day"].format(product.interestRate.toString() + "%"),
                            fontSize = 13.sp,
                            color = C_7E7B79,
                        )
                        Text(
                            text = product.interestAmount.toAmountString(
                                null
                            ),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_524F4C,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["due_date"],
                            fontSize = 13.sp,
                            color = C_7E7B79,
                        )
                        Text(
                            text = product.repayTimeStr ?: "",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_524F4C,
                        )
                    }
                    product.appProductHandleFeeConfigDtos?.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.getFeeName(),
                                fontSize = 13.sp,
                                color = C_7E7B79,
                            )
                            Text(
                                text = item.amount.toAmountString(null),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_524F4C,
                            )
                        }
                    }
                    if (product.installmentServiceFee.isPositive()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings["install_fee"],
                                fontSize = 13.sp,
                                color = C_7E7B79,
                            )
                            Text(
                                text = product.installmentServiceFee.toAmountString(product.currencySymbol),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_524F4C,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            .height(20.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                            color = C_524F4C,
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            val leaseUrl =
                LEASE_AGREEMENT + "userId=${getLoginInfo()?.id}&productId=${product.id}&amount=${product.maxLoanAmount.toString()}"
            val pawnUrl =
                PAWN_AGREEMENT + "userId=${getLoginInfo()?.id}&productId=${product.id}&amount=${product.maxLoanAmount.toString()}"
            MultiColoredText(
                Strings["product_detail_agreement"].format(
                    Strings["lease_contract"],
                    Strings["mortgage_contract"],
                ),
                listOf(
                    ColoredTextPart(Strings["lease_contract"], C_FC7700, 13.sp) {
                        navigate(
                            Screen.WebView(
                                Strings["lease_contract"],
                                leaseUrl
                            )
                        )
                    },
                    ColoredTextPart(Strings["mortgage_contract"], C_FC7700, 13.sp) {
                        navigate(
                            Screen.WebView(
                                Strings["mortgage_contract"],
                                pawnUrl
                            )
                        )
                    },
                ),
                defaultFontSize = 12.sp,
                defaultColor = C_B4B0AD,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 70.dp)
            )
        }
        Spacer(
            modifier = Modifier.padding(top = 10.dp)
                .background(C_E3E0DD).height(0.5.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth().background(white).padding(top = 8.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["account_receivable"],
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = C_40495C,
                )
                Text(
                    text = product.bankNo.maskString().orEmpty(),
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.End,
                    color = C_40495C,
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                )
                Text(
                    text = Strings["change"],
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.background(C_FC7700, RoundedCornerShape(3.dp))
                        .padding(horizontal = 5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {

                        }
                )
            }
            Text(
                text = Strings["apply"],
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                C_FC7700,
                                C_FC7700
                            ),
                        ), RoundedCornerShape(30.dp)
                    ).clickable {

                    }
            )
        }
    }
}

@Preview
@Composable
fun PreProduct() {
    ProductScreen(ProductDetailBean(), onBack = {}) {

    }
}