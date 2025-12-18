package com.kmp.vayone.ui.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.remote.json
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.ui.widget.AutoSizeText
import com.kmp.vayone.ui.widget.Banner
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.MarqueeText
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.SignPageParams
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isLoggedIn
import com.kmp.vayone.util.isPositive
import com.kmp.vayone.util.jumpCert
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_132247
import theme.C_190E30
import theme.C_2B2621
import theme.C_2D3C52
import theme.C_3E4845
import theme.C_45FD12
import theme.C_524F4C
import theme.C_72C4FF
import theme.C_75707E
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_C4C4C4
import theme.C_EAF393
import theme.C_ED190E
import theme.C_F8F4F0
import theme.C_FC7700
import theme.C_FEB201
import theme.C_FF652E
import theme.C_FFD8AE
import theme.C_FFD96E
import theme.C_FFE070
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.dialog_close
import vayone.composeapp.generated.resources.empty_product
import vayone.composeapp.generated.resources.home_arrow
import vayone.composeapp.generated.resources.home_calm
import vayone.composeapp.generated.resources.home_fill_bg_dialog
import vayone.composeapp.generated.resources.home_gift_dialog
import vayone.composeapp.generated.resources.home_icon
import vayone.composeapp.generated.resources.home_pre_dialog
import vayone.composeapp.generated.resources.home_question1
import vayone.composeapp.generated.resources.home_question2
import vayone.composeapp.generated.resources.home_question3
import vayone.composeapp.generated.resources.home_question4
import vayone.composeapp.generated.resources.home_refuse_dialog
import vayone.composeapp.generated.resources.home_star_dialog
import vayone.composeapp.generated.resources.home_tag
import vayone.composeapp.generated.resources.product_icon
import kotlin.toString

@Composable
fun HomePage(
    isFromCertSuccess: Boolean = false,
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
    var showPreDialog by remember { mutableStateOf(false) }
    var enableLoanStr by remember { mutableStateOf("") }
    var showRefuseDialog by remember { mutableStateOf(false) }
    var showFillBankDialog by remember { mutableStateOf(isFromCertSuccess) }
    val isLoading by mainViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            mainViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
                when (event.code) {
                    401, 402 -> {
                        CacheManager.setLoginInfo(null)
                        CacheManager.setToken("")
                        navigate(Screen.Login)
                    }

                    300 -> {
                        //VersionUpdate
                    }
                }
            }
        }
        launch {
            mainViewModel.getBannerList()
        }
        launch {
            mainViewModel.productDetailResult.collect {
                it?.let {
//                    CacheManager.setSignBackHome(false)
                    if (CacheManager.isSignBackHome()) {
                        val map: MutableMap<Long?, Int?> = HashMap()
                        if (!it.productInstallmentPlanDTOList.isNullOrEmpty()) {
                            val index =
                                it.productInstallmentPlanDTOList.indexOfFirst { it1 -> it1.isDefault == 1 }
                                    .coerceIn(0, Int.MAX_VALUE)
                            map[it.productInstallmentPlanDTOList[index].productId] =
                                it.productInstallmentPlanDTOList[index].planNums
                        }
                        val termMap: MutableMap<Long?, Long?> = HashMap()
                        if (!it.loanTermConfigDTOList.isNullOrEmpty()) {
                            val index =
                                it.loanTermConfigDTOList.indexOfFirst { it1 -> it1.defaultSign == 1 }
                                    .coerceIn(0, Int.MAX_VALUE)
                            termMap[it.id] = it.loanTermConfigDTOList[index].id
                        }
                        navigate(
                            Screen.Sign(
                                SignPageParams(
                                    it.bankInfoId,
                                    null,
                                    it.id.toString(),
                                    it.bankInfoId,
                                    it.maxLoanAmount,
                                    if (map.isEmpty()) null else json.encodeToString(map),
                                    json.encodeToString(termMap),
                                    true,
                                    null
                                )
                            )
                        )
                    } else {
                        navigate(Screen.Product(it))
                    }
                }
            }
        }
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
                                val isEnable =
                                    !isCert || (authData?.togetherLoanSign == 1 && authData?.userCreditAmount.isPositive())
                                Text(
                                    text = if (isCert) Strings["withdrawal"] else Strings["borrow_now"],
                                    modifier = Modifier.padding(
                                        start = 40.dp,
                                        end = 40.dp,
                                        top = 6.dp
                                    ).fillMaxWidth().height(48.dp)
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = if (isEnable) listOf(
                                                    C_FC7700, C_FEB201
                                                ) else listOf(C_C4C4C4, C_C4C4C4),
                                            ), RoundedCornerShape(30.dp)
                                        )
                                        .clickable(isEnable) {
                                            if (!isLoggedIn()) {
                                                navigate(Screen.Login)
                                                return@clickable
                                            }
                                            if (!isCert) {
                                                mainViewModel.authState.value?.jumpCert(
                                                    navigate,
                                                    false
                                                )
                                                return@clickable
                                            }
                                            if (mainViewModel.authState.value?.isFillBank() != true) {
                                                navigate(Screen.BankCert(false))
                                                return@clickable
                                            }
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
                            HomeProductList(homeProducts, navigate) { item ->
                                if (mainViewModel.authState.value?.isFillBank() != true) {
                                    navigate(Screen.BankCert(false))
                                    return@HomeProductList
                                }
                                if (item.creditStatus == 2) {
                                    showPreDialog = true
                                    enableLoanStr = item.enableLoanStr ?: ""
                                    return@HomeProductList
                                }
                                if (item.creditStatus == 0) {
                                    showRefuseDialog = true
                                    return@HomeProductList
                                }
                                if (item.showConditionTypeSign == "1") {
                                    navigate(Screen.SuppleInfo(false, item.maxLoanAmount ?: ""))
                                    return@HomeProductList
                                }
                                when (item.jumpType) {
                                    1 -> {
//                                        goExternalBrowser(item.downloadUrl ?: "")
                                    }

                                    2 -> {
//                                        goGooglePlay(item.downloadUrl ?: "")
                                    }

                                    else -> {
                                        mainViewModel.getProductDetail(
                                            item.productId.toString(),
                                            item.maxLoanAmount.toString()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ShowPreHomeDialog(showPreDialog, enableLoanStr) {
            showPreDialog = false
        }
        ShowRefuseDialog(showRefuseDialog) {
            showRefuseDialog = false
        }
        ShowFillBankDialog(
            showFillBankDialog && isFromCertSuccess,
            authData?.loanAmountRange ?: "0",
            navigate
        ) {
            showFillBankDialog = false
        }
        LoadingDialog(isLoading)
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
                AutoSizeText(
                    text = Strings["home_question1"],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    minFontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxFontSize = 12.sp,
                    maxLines = 2
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
                AutoSizeText(
                    text = Strings["home_question2"],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    minFontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxFontSize = 12.sp,
                    maxLines = 2,
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
                AutoSizeText(
                    text = Strings["home_question3"],
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    minFontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxFontSize = 12.sp,
                    maxLines = 2,
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
                AutoSizeText(
                    text = Strings["home_question4"],
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = C_190E30,
                    minFontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxFontSize = 12.sp,
                    maxLines = 2,
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
        AutoSizeText(
            text = Strings["current_available_credit_str"],
            color = white,
            maxFontSize = 16.sp,
            minFontSize = 13.sp,
            modifier = Modifier.padding(start = 20.dp, top = 33.dp)
                .widthIn(max = 200.dp)
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
                AutoSizeText(
                    modifier = Modifier.padding(top = 13.dp),
                    text = item.annualizedInterestRate ?: "",
                    color = C_2D3C52,
                    maxFontSize = 16.sp,
                    minFontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                AutoSizeText(
                    modifier = Modifier.padding(top = 4.dp),
                    text = Strings["annual_rate"],
                    color = C_2D3C52,
                    maxFontSize = 14.sp,
                    minFontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Column(
                modifier = Modifier.padding(start = 15.dp, end = 16.dp).fillMaxHeight().weight(2.5f)
            ) {
                AutoSizeText(
                    modifier = Modifier.padding(top = 13.dp),
                    text = "${item.loanTerm}${Strings["days"]}",
                    color = C_2D3C52,
                    maxFontSize = 16.sp,
                    minFontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                AutoSizeText(
                    modifier = Modifier.padding(top = 4.dp),
                    text = Strings["loan_period"],
                    color = C_2D3C52,
                    maxFontSize = 14.sp,
                    minFontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
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
            text = Strings["home_pre_dialog"],
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
    ShowFillBankDialog(true, navigate = {}) {}
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
    onClick: (ProductBean) -> Unit,
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
            ProductItem(product, navigate, onClick)
        }
    }
}

@Composable
fun ProductItem(
    item: ProductBean,
    navigate: (Screen) -> Unit,
    onClick: (ProductBean) -> Unit,
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
                        .clickable(enabled = item.canApply) {
                            navigate(Screen.SuppleInfo(false, item.maxLoanAmount ?: ""))
                        }
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
                    .clickable(
                        enabled = item.canApply,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onClick(item)
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

@Composable
fun ShowPreHomeDialog(isShow: Boolean = true, date: String, onDismiss: () -> Unit) {
    if (!isShow) return
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(white, RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(Res.drawable.dialog_close),
                contentDescription = null,
                modifier = Modifier.padding(top = 10.dp, end = 10.dp).size(24.dp)
                    .align(Alignment.End)
                    .clickable {
                        onDismiss()
                    },
            )
            Image(
                painter = painterResource(Res.drawable.home_pre_dialog),
                contentDescription = null,
                modifier = Modifier.padding(top = 20.dp).size(60.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 18.dp),
                text = Strings["pre_credit_has_expired"],
                fontSize = 16.sp,
                lineHeight = 16.sp,
                color = C_2B2621,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            MultiColoredText(
                Strings["pre_credit_has_expired_tips"].format(
                    date
                ),
                listOf(
                    ColoredTextPart(date, C_524F4C, 14.sp) {

                    }
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 12.dp),
                defaultColor = C_7E7B79,
                defaultFontSize = 12.sp,
                defaultFontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ShowRefuseDialog(isShow: Boolean = true, onDismiss: () -> Unit) {
    if (!isShow) return
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(white, RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(Res.drawable.dialog_close),
                contentDescription = null,
                modifier = Modifier.padding(top = 10.dp, end = 10.dp).size(24.dp)
                    .align(Alignment.End)
                    .clickable {
                        onDismiss()
                    },
            )
            Image(
                painter = painterResource(Res.drawable.home_refuse_dialog),
                contentDescription = null,
                modifier = Modifier.padding(top = 20.dp).size(70.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 18.dp),
                text = Strings["pre_credit_has_expired"],
                fontSize = 16.sp,
                lineHeight = 16.sp,
                color = C_2B2621,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 12.dp),
                color = C_7E7B79,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                text = Strings["dialog_home_refuse"]
            )
        }
    }
}

@Composable
fun ShowFillBankDialog(
    isShow: Boolean = true,
    amount: String = "1000",
    navigate: (Screen) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isShow) return
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(Res.drawable.home_fill_bg_dialog),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
            Image(
                painter = painterResource(Res.drawable.home_star_dialog),
                contentDescription = null,
                modifier = Modifier.padding(start = 18.dp, top = 22.dp).size(32.dp),
            )
            Text(
                text = Strings["dialog_auth_title"],
                modifier = Modifier.padding(start = 18.dp, top = 51.dp).width(158.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                color = C_45FD12,
            )
            Image(
                painter = painterResource(Res.drawable.home_gift_dialog),
                contentDescription = null,
                modifier = Modifier.padding(end = 22.dp, top = 16.dp).size(147.dp)
                    .align(Alignment.TopEnd),
            )
            Image(
                painter = painterResource(Res.drawable.dialog_close),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp, top = 10.dp).size(24.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        onDismiss()
                    })
            Column(
                modifier = Modifier.padding(
                    top = 160.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 89.dp
                ).fillMaxWidth()
                    .background(white, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = Strings["amount_range"],
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    color = C_190E30,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = amount,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    color = C_FC7700,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = Strings["dialog_auth_desc"],
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    color = C_75707E,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 15.dp, start = 8.dp, end = 8.dp, bottom = 17.dp),
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = Strings["immediate_loan"],
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = C_2B2621,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .clip(RoundedCornerShape((30.dp)))
                    .height(48.dp).background(C_FFD96E, RoundedCornerShape(30.dp))
                    .align(Alignment.BottomCenter)
                    .clickable {
                        navigate(Screen.BankCert(false))
                    }
            )
        }
    }
}