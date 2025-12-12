package com.kmp.vayone.ui.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.Image
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.Banner
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.MarqueeText
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isLoggedIn
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_132247
import theme.C_190E30
import theme.C_2B2621
import theme.C_2D3C52
import theme.C_3E4845
import theme.C_524F4C
import theme.C_72C4FF
import theme.C_75707E
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_EAF393
import theme.C_ED190E
import theme.C_F8F4F0
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FF652E
import theme.C_FFBB48
import theme.C_FFD8AE
import theme.C_FFE070
import theme.C_FFEADB
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.dialog_close
import vayone.composeapp.generated.resources.empty_product
import vayone.composeapp.generated.resources.home_arrow
import vayone.composeapp.generated.resources.home_calm
import vayone.composeapp.generated.resources.home_icon
import vayone.composeapp.generated.resources.home_question1
import vayone.composeapp.generated.resources.home_question2
import vayone.composeapp.generated.resources.home_question3
import vayone.composeapp.generated.resources.home_question4
import vayone.composeapp.generated.resources.home_tag
import vayone.composeapp.generated.resources.logout_icon
import vayone.composeapp.generated.resources.product_icon

@Composable
fun HomePage(
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    navigate: (Screen) -> Unit,
) {
    val mainViewModel = remember { MainViewModel() }
    val loadingState by mainViewModel.loadingState.collectAsState()
    val unAuthData by mainViewModel.homeUnAuthResult.collectAsState()
    val authData by mainViewModel.homeAuthResult.collectAsState()
    val isCert by mainViewModel.isCert.collectAsState()
    val bannerList by mainViewModel.bannerList.collectAsState()
    var isShowPaymentFail by remember { mutableStateOf(false) }
    val homeProducts by mainViewModel.homeProducts.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.errorEvent.collect { event ->
            toast(event.showToast, event.message)
        }
    }
    LaunchedEffect(Unit) {
        mainViewModel.getBannerList()
    }
    // 监听生命周期
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                mainViewModel.showLoadingBox()
                mainViewModel.getAuthStatus()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LoadingBox(
        state = loadingState, modifier = Modifier.fillMaxSize().padding(top = 38.dp), onRetry = {
            mainViewModel.showLoadingBox()
            mainViewModel.getAuthStatus()
        }) {
        PullToRefreshBox(
            isRefreshing = loadingState != UiState.Success, onRefresh = {
                mainViewModel.showLoadingBox()
                mainViewModel.getAuthStatus()
            }) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val refuse = isCert && authData?.userCreditStatus == 2
                        val preDenied = isCert && authData?.userCreditStatus == 0
                        if (isCert && !refuse && !preDenied && (homeProducts.isEmpty()
                                    || authData?.calmFlag == true)
                        ) {
                            if (homeProducts.isEmpty()) {
                                HomeEmptyProduct()
                            } else {
                                HomeCalm(authData?.enableLoanStr ?: "")
                            }
                        } else {
                            if (!isCert && unAuthData != null) {
                                HomeUnAuthTop(unAuthData!!)
                            }
                            if (!refuse && !preDenied) {
                                if (isCert && authData != null) {
                                    HomeAuthTop(authData!!)
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                        .padding(start = 16.dp, end = 16.dp).background(
                                            color = C_FFF4E6, shape = RoundedCornerShape(
                                                bottomStart = 16.dp, bottomEnd = 16.dp
                                            )
                                        ).padding(top = 5.dp, bottom = 5.dp)
                                ) {
                                    MarqueeText()
                                }
                                Text(
                                    text = if (isCert) Strings["withdrawal"] else Strings["borrow_now"],
                                    modifier = Modifier.padding(
                                        start = 40.dp,
                                        end = 40.dp,
                                        top = 6.dp
                                    )
                                        .fillMaxWidth().height(48.dp)
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    C_FC7700, C_FEB201
                                                ),
                                            ), RoundedCornerShape(30.dp)
                                        ).clickable {

                                        },
                                    color = white,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 48.sp,
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                                    text = if (isCert) Strings["quick_withdraw"] else Strings["refuse_18"],
                                    fontSize = 12.sp,
                                    color = C_B4B0AD,
                                    textAlign = TextAlign.Center
                                )
                            }
                            RefuseLoan(refuse) {
                                mainViewModel.showLoadingBox()
                                mainViewModel.getAuthStatus()
                            }
                            PreDenied(preDenied, authData?.enableLoanStr ?: "")
                            Banner(bannerList ?: arrayListOf()) {
                                navigate(Screen.WebView(it.name ?: "", it.activityH5Url ?: ""))
                            }
                            RepaymentError(isShowPaymentFail && authData?.bankErrorFlag == true) {
                                isShowPaymentFail = false
                            }
                            HomeQuestion(!isCert)
                            HomeProductList(homeProducts, navigate)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeQuestion(isShow: Boolean = true) {
    if (!isShow) return
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp).border(
            width = 1.dp, color = _root_ide_package_.theme.C_E5E0DC, RoundedCornerShape(16.dp)
        ).background(shape = RoundedCornerShape(16.dp), color = C_F8F4F0)
            .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 28.dp)
    ) {
        Text(
            text = "“${Strings["home_question_title"]}”",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = C_524F4C,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(
            text = Strings["home_question_desc"],
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = C_7E7B79,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Column(
                modifier = Modifier.weight(1f).height(78.dp).padding(end = 10.dp)
                    .background(color = white, RoundedCornerShape(8.dp)),
            ) {
                Image(
                    painter = painterResource(Res.drawable.home_question1),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 8.dp).size(32.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = Strings["home_question1"],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
            Column(
                modifier = Modifier.weight(1f).height(78.dp).padding(end = 10.dp)
                    .background(color = white, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(Res.drawable.home_question2),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 8.dp).size(32.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = Strings["home_question2"],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Column(
                modifier = Modifier.weight(1f).height(78.dp).padding(end = 10.dp)
                    .background(color = white, RoundedCornerShape(8.dp)),
            ) {
                Image(
                    painter = painterResource(Res.drawable.home_question3),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 8.dp).size(32.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = Strings["home_question3"],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
            Column(
                modifier = Modifier.weight(1f).height(78.dp).padding(end = 10.dp)
                    .background(color = white, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(Res.drawable.home_question4),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 8.dp).size(32.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = Strings["home_question4"],
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun HomeUnAuthTop(item: HomeBean = HomeBean()) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(187.dp).background(
            brush = Brush.horizontalGradient(
                listOf(
                    C_FF652E, C_EAF393
                )
            ), shape = RoundedCornerShape(16.dp)
        )
    ) {
        Image(
            painter = painterResource(Res.drawable.home_icon),
            contentDescription = null,
            modifier = Modifier.padding(end = 15.dp).size(135.dp, 112.dp).align(Alignment.TopEnd)
        )
        Text(
            text = Strings["current_available_credit_str"],
            color = white,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(start = 20.dp, top = 33.dp)
        )
        Text(
            text = item.maxAmount.toAmountString(item.currencySymbol),
            color = white,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(start = 20.dp, top = 52.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, start = 10.dp, end = 10.dp)
                .height(64.dp).background(
                    color = white.copy(0.3f), RoundedCornerShape(50.dp)
                ).align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier.weight(3f).padding(start = 22.dp).fillMaxHeight()
            ) {
                Text(
                    modifier = Modifier.padding(top = 13.dp),
                    text = item.annualizedInterestRate ?: "",
                    color = C_2D3C52,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = Strings["annual_rate"],
                    color = C_2D3C52,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                )
            }
            Column(
                modifier = Modifier.padding(start = 15.dp, end = 16.dp).fillMaxHeight().weight(2.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 13.dp),
                ) {
                    Text(
                        text = item.loanTerm ?: "",
                        color = C_2D3C52,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp,
                    )
                    Text(
                        text = Strings["days"],
                        color = C_2D3C52,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp,
                    )

                }
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = Strings["loan_period"],
                    color = C_2D3C52,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

@Composable
fun HomeAuthTop(item: HomeLoanBean = HomeLoanBean()) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(193.dp).background(
            brush = Brush.horizontalGradient(
                listOf(
                    C_FF652E, C_EAF393
                )
            ), shape = RoundedCornerShape(16.dp)
        )
    ) {
        Image(
            painter = painterResource(Res.drawable.home_icon),
            contentDescription = null,
            modifier = Modifier.padding(end = 15.dp).size(135.dp, 112.dp).align(Alignment.TopEnd)
        )
        Text(
            text = Strings["l_amount"],
            color = white,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(start = 20.dp, top = 33.dp)
        )
        Text(
            text = item.userCreditAmount.toAmountString(item.currencySymbol),
            color = white,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(start = 20.dp, top = 52.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, start = 10.dp, end = 10.dp)
                .height(72.dp).background(
                    color = white.copy(0.3f), RoundedCornerShape(50.dp)
                ).align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier.padding(start = 32.dp).fillMaxHeight().weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(top = 13.dp),
                    text = item.totalCreditAmount.toAmountString(item.currencySymbol),
                    color = C_2D3C52,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = Strings["maximum_mortgage_amount"],
                    color = C_2D3C52,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                )
            }
            Column(
                modifier = Modifier.padding(start = 25.dp, end = 22.dp).fillMaxHeight().weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(top = 13.dp),
                    text = item.usedAmount.toAmountString(item.currencySymbol),
                    color = C_2D3C52,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = Strings["used_credit_str"],
                    color = C_2D3C52,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

@Composable
fun RepaymentError(isVisible: Boolean = true, onClose: () -> Unit) {
    if (!isVisible) return
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 16.dp, end = 16.dp)
            .border(width = 1.dp, color = C_ED190E, RoundedCornerShape(16.dp))
            .background(color = white, RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                textAlign = TextAlign.Center,
                color = C_ED190E,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                text = Strings["home_payment_failed"],
                modifier = Modifier.padding(top = 15.dp).align(Alignment.Center)
            )
            Image(
                contentDescription = null,
                painter = painterResource(Res.drawable.dialog_close),
                modifier = Modifier.padding(top = 10.dp, end = 10.dp).align(Alignment.TopEnd)
                    .clickable {
                        onClose()
                    })
        }
        Text(
            textAlign = TextAlign.Start,
            color = C_ED190E,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            text = Strings["home_payment_failed_desc"],
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 15.dp, start = 16.dp, end = 16.dp),
        )
        Text(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp, top = 12.dp, bottom = 16.dp).height(36.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(shape = RoundedCornerShape(30.dp), color = C_ED190E).clickable {

                },
            text = Strings["modify_bank_card"],
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = white,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )
    }
}

@Composable
fun RefuseLoan(isShow: Boolean, onRefresh: () -> Unit) {
    if (!isShow) return
    var timeLeft by remember { mutableStateOf(59) }
    LaunchedEffect(Unit) {
        timeLeft = 59
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onRefresh()
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .background(color = white, RoundedCornerShape(16.dp))
    ) {
        Text(
            text = Strings["home_refuse"],
            fontSize = 16.sp,
            color = C_FEB201,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 15.dp),
            textAlign = TextAlign.Center,
        )
        MultiColoredText(
            fullText = Strings["home_refuse_times"].format(timeLeft.toString()),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 7.dp),
            defaultColor = C_72C4FF,
            textAlign = TextAlign.Center,
            coloredParts = listOf(
                ColoredTextPart(timeLeft.toString(), C_FC7700, 14.sp, FontWeight.Bold)
            )
        )
        Text(
            text = Strings["home_refuse_tips"],
            fontSize = 16.sp,
            color = C_75707E,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 8.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 25.dp, end = 25.dp, top = 9.dp)
                .height(36.dp).clip(RoundedCornerShape(30.dp))
                .background(shape = RoundedCornerShape(30.dp), color = C_FC7700).clickable {
                    onRefresh()
                },
            text = Strings["refresh"],
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = white,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )
        Text(
            text = Strings["refuse_18"],
            fontSize = 12.sp,
            color = C_B4B0AD,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 10.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun PreDenied(isShow: Boolean, date: String) {
    if (!isShow) return
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .background(color = white, RoundedCornerShape(16.dp))
    ) {
        Text(
            text = Strings["home_pre"],
            fontSize = 16.sp,
            color = C_ED190E,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = Strings["home_pre_tips"].format(date),
            fontSize = 12.sp,
            color = C_75707E,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 9.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )
    }
}


@Preview
@Composable
fun PreHomePage() {
    ProductItem(ProductBean(),{}){}
}

@Composable
fun HomeEmptyProduct() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_product),
            contentDescription = null,
            modifier = Modifier.size(155.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = Strings["empty_home"],
            fontSize = 14.sp,
            color = C_B4B0AD,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 9.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun HomeCalm(date: String = "") {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.home_calm),
            contentDescription = null,
            modifier = Modifier.size(155.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = Strings["home_calm"],
            fontSize = 14.sp,
            color = C_75707E,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(6.dp).background(C_B4B0AD, RoundedCornerShape(3.dp)))
            Text(
                text = Strings["home_calm_tips1"],
                fontSize = 12.sp,
                color = C_B4B0AD,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                textAlign = TextAlign.Start,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(6.dp).background(C_B4B0AD, RoundedCornerShape(3.dp)))
            Text(
                text = Strings["home_calm_tips2"],
                fontSize = 12.sp,
                color = C_B4B0AD,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                textAlign = TextAlign.Start,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(6.dp).background(C_B4B0AD, RoundedCornerShape(3.dp)))
            Text(
                text = Strings["home_calm_tips3"].format(date),
                fontSize = 12.sp,
                color = C_B4B0AD,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
fun HomeProductList(
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
            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "“${Strings["products"]}”",
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            textAlign = TextAlign.Center,
            color = C_524F4C,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        list.forEach { product ->
            ProductItem(product, navigate) {
                // 点击回调
            }
        }
    }
}

@Composable
fun ProductItem(
    item: ProductBean,
    navigate: (Screen) -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .background(shape = RoundedCornerShape(16.dp), color = white)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.product_icon),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp).size(38.dp)
                )
                Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.productName ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = C_132247,
                        )
                        if (item.newSign == 1 && !item.isTogether) {
                            Box(modifier = Modifier.padding(start = 7.dp)) {
                                Image(
                                    painter = painterResource(Res.drawable.home_tag),
                                    contentDescription = null,
                                    modifier = Modifier.wrapContentWidth().height(12.dp)
                                )
                                Text(
                                    text = Strings["new_str"],
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 10.sp,
                                    color = white,
                                    lineHeight = 10.sp,
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                            }
                        }
                    }
                    Row(modifier = Modifier.wrapContentHeight()) {
                        item.tagList?.distinct()?.forEachIndexed { index, string ->
                            Box(
                                modifier = Modifier.height(16.dp)
                                    .padding(end = 4.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = if (index % 2 == 0) listOf(
                                                C_FFE070,
                                                C_FFE070.copy(0.2f)
                                            )
                                            else listOf(
                                                Color(0xFFB6F68F), Color(0xFFB6F68F).copy(0.2f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(26.dp)
                                    )
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = string,
                                    fontSize = 11.sp,
                                    color = Color(if (index % 2 == 0) 0xFFFF9A03 else 0xFF42A406),
                                    lineHeight = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["borrow_days_str"],
                    fontSize = 13.sp,
                    color = C_3E4845,
                    lineHeight = 13.sp,
                )
                Text(
                    text = (item.timeLimit ?: 0).toString(),
                    fontSize = 14.sp,
                    color = C_524F4C,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            if (item.showConditionTypeSign == "1") {
                Box(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 6.dp)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(C_FFF4E6, RoundedCornerShape(30.dp))
                ) {
                    Text(
                        text = Strings["go_add_info_str"],
                        fontSize = 14.sp,
                        color = C_2B2621,
                        lineHeight = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Image(
                        painter = painterResource(Res.drawable.home_arrow),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp).size(30.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 6.dp)
                    .background(
                        C_FFD8AE,
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = Strings["l_amount"],
                    fontSize = 13.sp,
                    color = C_2B2621,
                    lineHeight = 13.sp,
                )
                Text(
                    text = if (item.canApply && item.isFillBank) item.maxLoanAmount.toAmountString(
                        item.currencySymbol
                    ) else item.loanAmountRange ?: "",
                    fontSize = 18.sp,
                    color = C_2B2621,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (item.showConditionTypeSign != "1") {
            Text(
                text = Strings["apply"],
                fontSize = 14.sp,
                color = white,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(end = 10.dp, top = 40.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(C_FC7700, RoundedCornerShape(30.dp))
                    .padding(horizontal = 18.dp)
                    .clickable(enabled = item.canApply) {
                        onClick()
                    }
            )
        }
        if (!item.canApply) {
            Box(
                modifier = Modifier.matchParentSize()
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Strings["unlocked"],
                    fontSize = 20.sp,
                    color = white,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}