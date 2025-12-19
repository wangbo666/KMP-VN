@file:OptIn(ExperimentalMaterial3Api::class)

package com.kmp.vayone.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kmp.vayone.data.BankCardBean
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.AGREEMENT_ABOUT
import com.kmp.vayone.data.CacheManager.LEASE_AGREEMENT
import com.kmp.vayone.data.CacheManager.PAWN_AGREEMENT
import com.kmp.vayone.data.CacheManager.getLoginInfo
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.remote.json
import com.kmp.vayone.getPhoneModel
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.postAllPermissions
import com.kmp.vayone.ui.widget.ColoredTextPart
import com.kmp.vayone.ui.widget.LoadingBox
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.MultiColoredText
import com.kmp.vayone.ui.widget.SignPageParams
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.ui.widget.UiState
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isPositive
import com.kmp.vayone.util.log
import com.kmp.vayone.util.maskString
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.CertViewModel
import com.kmp.vayone.viewmodel.ProductViewModel
import io.ktor.util.sha1
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_003BC3
import theme.C_2B2621
import theme.C_2D3C52
import theme.C_40495C
import theme.C_524F4C
import theme.C_7E7B79
import theme.C_B4B0AD
import theme.C_DAE9FF
import theme.C_E3E0DD
import theme.C_F9F9F9
import theme.C_FC7700
import theme.C_FCFCFC
import theme.C_FEB201
import theme.C_FFBB48
import theme.C_FFF4E6
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.account_add_dialog
import vayone.composeapp.generated.resources.batch_check
import vayone.composeapp.generated.resources.card_icon
import vayone.composeapp.generated.resources.mine_right
import vayone.composeapp.generated.resources.order_check_green
import vayone.composeapp.generated.resources.product_install_arrow
import vayone.composeapp.generated.resources.product_plan
import kotlin.toString

@Composable
fun ProductScreen(
    productDetail: ProductDetailBean,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val productViewModel = remember { ProductViewModel() }
    val certViewModel = remember { CertViewModel() }
    val loading by certViewModel.isLoading.collectAsState()
    val cardList by certViewModel.accountList.collectAsState()
    var showCardDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val loadingState by productViewModel.loadingState.collectAsState()
    var product by remember { mutableStateOf(productDetail) }
    var showLoanDialog by remember { mutableStateOf(false) }
    val leaseUrl = remember(product.id, product.maxLoanAmount) {
        LEASE_AGREEMENT + "userId=${getLoginInfo()?.id}" + "&productId=${product.id}" + "&amount=${product.maxLoanAmount}"
    }
    val pawnUrl = remember(product.id, product.maxLoanAmount) {
        PAWN_AGREEMENT + "userId=${getLoginInfo()?.id}" + "&productId=${product.id}" + "&amount=${product.maxLoanAmount}"
    }
    var cardInfo by remember { mutableStateOf<BankCardBean?>(null) }
    var showPlan by remember { mutableStateOf(true) }
    val productInstallmentMap: MutableMap<Long?, Int?> = remember { HashMap() }
    val termIdMap: MutableMap<Long?, Long?> = remember { HashMap() }
    var defaultSelectPlan by remember {
        mutableStateOf(
            product.loanTermConfigDTOList?.indexOfFirst { it1 -> it1.defaultSign == 1 }
                ?.coerceIn(0, Int.MAX_VALUE) ?: 0
        )
    }
    val listState = rememberLazyListState()
    var needScrollToBottom by remember { mutableStateOf(false) }

    LaunchedEffect(needScrollToBottom) {
        if (needScrollToBottom) {
            // ⚠️ 用 MAX_VALUE，保证滚到真正的底部
            listState.animateScrollToItem(
                index = listState.layoutInfo.totalItemsCount - 1,
                scrollOffset = Int.MAX_VALUE
            )
            needScrollToBottom = false
        }
    }


    LaunchedEffect(productViewModel) {
        launch {
            productViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            productViewModel.productDetailResult.collect {
                it?.let {
                    product = it
                    cardInfo = BankCardBean(id = product.bankInfoId, bankNo = product.bankNo)
                }
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                productViewModel.getProductDetail(
                    productDetail.id.toString(), productDetail.maxLoanAmount
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LoadingBox(
        UiState.Success, onRetry = {
            productViewModel.getProductDetail(
                productDetail.id.toString(), productDetail.maxLoanAmount
            )
        }, modifier = Modifier.fillMaxSize().background(C_FCFCFC).navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth().background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            C_FC7700, C_FFBB48
                        )
                    ), shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
            ) {
                TopBar(
                    title = Strings["loan_application"],
                    tintColor = white,
                    modifier = Modifier.statusBarsPadding().fillMaxWidth().height(44.dp)
                ) {
                    onBack()
                }
                Row(
                    modifier = Modifier.wrapContentWidth().padding(top = 8.dp).height(38.dp)
                        .background(C_FFF4E6, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .padding(horizontal = 17.dp).align(Alignment.CenterHorizontally).clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            navigate(Screen.WebView(Strings["about_lease"], AGREEMENT_ABOUT))
                        }, verticalAlignment = Alignment.CenterVertically
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
                        }, modifier = Modifier.padding(start = 6.dp).size(24.dp)
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
                    modifier = Modifier.padding(top = 17.dp).align(Alignment.CenterHorizontally)
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                state = listState
            ) {
                item {
                    val item =
                        if (product.loanTermConfigDTOList.isNullOrEmpty()) product else product.loanTermConfigDTOList?.get(
                            defaultSelectPlan
                        )
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                .height(20.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings["loan_period_days"],
                                fontSize = 13.sp,
                                color = C_7E7B79,
                            )
                            Text(
                                text = item?.timeLimit.toString(),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_524F4C,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                .height(20.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings["actually_amount"].replace("(%s)", ""),
                                fontSize = 13.sp,
                                color = C_7E7B79,
                            )
                            Text(
                                text = item?.actualAmount.toAmountString(product.currencySymbol),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_524F4C,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                .height(20.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings["interest_day"].format(item?.interestRate.toString() + "%"),
                                fontSize = 13.sp,
                                color = C_7E7B79,
                            )
                            Text(
                                text = item?.interestAmount.toAmountString(
                                    null
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_524F4C,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                .height(20.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings["due_date"],
                                fontSize = 13.sp,
                                color = C_7E7B79,
                            )
                            Text(
                                text = item?.repayTimeStr.orEmpty(),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_524F4C,
                            )
                        }
                        item?.appProductHandleFeeConfigDtos?.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                    .height(20.dp), verticalAlignment = Alignment.CenterVertically
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
                        if (!product.loanTermConfigDTOList.isNullOrEmpty()) {
                            val item = product.loanTermConfigDTOList!![defaultSelectPlan]
                            if (item.installmentServiceFee.isPositive()) {
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
                                        text = item.installmentServiceFee.toAmountString(product.currencySymbol),
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End,
                                        fontSize = 14.sp,
                                        color = C_524F4C,
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
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
                                color = C_524F4C,
                            )
                        }
                        if (!product.loanTermConfigDTOList.isNullOrEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Strings["plan_desc"],
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    color = C_2B2621,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Image(
                                    painter = painterResource(Res.drawable.product_plan),
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 16.dp).size(30.dp)
                                        .graphicsLayer {
                                            rotationZ = if (showPlan) 0f else 180f
                                        }.clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            showPlan = !showPlan
                                            needScrollToBottom = true
                                        }
                                )
                            }
                            ProductPlans(
                                showPlan,
                                product.loanTermConfigDTOList,
                                defaultSelectPlan,
                                onExpand = {
                                    needScrollToBottom = true
                                }
                            ) {
                                defaultSelectPlan = it
                            }
                        }
                    }
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
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
            }
            Spacer(
                modifier = Modifier.padding(top = 10.dp).background(C_E3E0DD).height(0.5.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth().background(white)
                    .padding(top = 8.dp, bottom = 16.dp)
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
                        text = cardInfo?.bankNo.maskString().orEmpty(),
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
                            .padding(horizontal = 5.dp).clip(RoundedCornerShape(3.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showCardDialog = true
                                certViewModel.getBankCardList(true)
                            })
                }
                Text(
                    text = Strings["apply"],
                    color = white,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 48.sp,
                    modifier = Modifier.navigationBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp).fillMaxWidth()
                        .height(48.dp).clip(RoundedCornerShape(30.dp)).background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    C_FC7700, C_FC7700
                                ),
                            ), RoundedCornerShape(30.dp)
                        ).clickable {
                            scope.launch {
                                postAllPermissions {
                                    showLoanDialog = true
                                }
                            }
                        })
            }
        }
    }
    ShowLoanAgreementDialog(showLoanDialog, agreementClick = {
        navigate(
            Screen.WebView(
                if (it == 0) Strings["lease_contract"] else Strings["mortgage_contract"],
                if (it == 0) leaseUrl else pawnUrl
            )
        )
    }, confirmClick = {
        val item = product.loanTermConfigDTOList!![defaultSelectPlan]
        termIdMap.clear()
        termIdMap[product.id] = item.id
        if (!item.productInstallmentPlanDTOList.isNullOrEmpty()) {
            productInstallmentMap.clear()
            productInstallmentMap[product.id] =
                item.productInstallmentPlanDTOList[0].planNums
        }
        json.encodeToString(
            productInstallmentMap
        ).log()
        json.encodeToString(termIdMap).log()
        if (product.isSign == 0) {
            navigate(
                Screen.Sign(
                    SignPageParams(
                        cardInfo?.id,
                        null,
                        product.id.toString(),
                        cardInfo?.id,
                        product.loanAmount,
                        if (productInstallmentMap.isEmpty()) null else json.encodeToString(
                            productInstallmentMap
                        ),
                        json.encodeToString(termIdMap),
                    )
                )
            )
        } else {
            navigate(
                Screen.LoanResult(
                    SignPageParams(
                        cardInfo?.id,
                        null,
                        product.id.toString(),
                        cardInfo?.id,
                        product.loanAmount,
                        if (productInstallmentMap.isEmpty()) null else json.encodeToString(
                            productInstallmentMap
                        ),
                        json.encodeToString(termIdMap),
                    )
                )
            )
        }
    }) {
        showLoanDialog = false
    }
    ChooseAccountDialog(
        showCardDialog,
        cardList.indexOfFirst { it.id == cardInfo?.id },
        cardList,
        addAction = {
            navigate(Screen.AddAccount)
        },
        onSelection = {
            cardInfo = it
        }) {
        showCardDialog = false
    }
    LoadingDialog(loading)
}

@Preview
@Composable
fun PreProduct() {
    ProductPlans(true, arrayListOf(ProductDetailBean()), onExpand = {}) {

    }
}

@Composable
fun ShowLoanAgreementDialog(
    show: Boolean = true,
    agreementClick: (Int) -> Unit,
    confirmClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = Strings["agreement_confirmation"],
                fontSize = 20.sp,
                lineHeight = 20.sp,
                color = C_FC7700,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = Strings["agreement_confirmation_desc"].format(CacheManager.HTTP_HOST),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = C_7E7B79,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
            Text(
                text = Strings["please_review_agreement_confirmation"],
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = C_FC7700,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    .height(44.dp).clip(RoundedCornerShape(4.dp))
                    .background(C_F9F9F9, RoundedCornerShape(4.dp)).clickable {
                        agreementClick(0)
                    }) {
                Text(
                    text = Strings["lease_contract"],
                    fontSize = 14.sp,
                    lineHeight = 44.sp,
                    color = C_2D3C52,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp).weight(1f),
                    textAlign = TextAlign.Start
                )
                Image(
                    painter = painterResource(Res.drawable.mine_right),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    .height(44.dp).clip(RoundedCornerShape(4.dp))
                    .background(C_F9F9F9, RoundedCornerShape(4.dp)).clickable {
                        agreementClick(1)
                    }) {
                Text(
                    text = Strings["mortgage_contract"],
                    fontSize = 14.sp,
                    lineHeight = 44.sp,
                    color = C_2D3C52,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp).weight(1f),
                    textAlign = TextAlign.Start
                )
                Image(
                    painter = painterResource(Res.drawable.mine_right),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp).size(24.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = Strings["confirm"],
                fontSize = 18.sp,
                color = white,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    .background(C_FC7700, RoundedCornerShape(30.dp)).clip(RoundedCornerShape(30.dp))
                    .fillMaxWidth().clickable {
                        confirmClick()
                        onDismiss()
                    })
        }
    }
}

@Composable
fun ChooseAccountDialog(
    show: Boolean = true,
    initPosition: Int = 0,
    accountList: List<BankCardBean>? = null,
    addAction: () -> Unit,
    onSelection: (BankCardBean) -> Unit = {},
    onDismiss: () -> Unit
) {
    if (!show || accountList.isNullOrEmpty()) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectPosition by remember { mutableStateOf(initPosition) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white,
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = Strings["choose_account"],
                fontSize = 20.sp,
                lineHeight = 20.sp,
                color = C_FC7700,
                fontWeight = FontWeight.Bold,
            )
            accountList.forEachIndexed { index, bean ->
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                        .fillMaxWidth()
                        .height(66.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(C_FFF4E6, RoundedCornerShape(8.dp))
                        .clickable {
                            selectPosition = index
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.card_icon),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp).size(32.dp)
                    )
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(
                            text = bean.bankNo.orEmpty(),
                            color = C_524F4C,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                        )
                        Text(
                            text = bean.bankName,
                            color = C_7E7B79,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                        )
                    }
                    if (selectPosition == index) {
                        Image(
                            painter = painterResource(Res.drawable.order_check_green),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp).size(24.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.padding(end = 16.dp).size(24.dp))
                    }
                }
            }
            Row(
                modifier = Modifier.height(38.dp).align(Alignment.CenterHorizontally)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onDismiss()
                        addAction()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.account_add_dialog),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 10.dp),
                    text = Strings["add_account_receivable"],
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = C_524F4C,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = Strings["confirm"],
                fontSize = 18.sp,
                color = white,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 15.dp)
                    .background(C_FC7700, RoundedCornerShape(30.dp)).clip(RoundedCornerShape(30.dp))
                    .fillMaxWidth().clickable {
                        onDismiss()
                        onSelection(accountList[selectPosition])
                    })
        }
    }
}


@Composable
fun ProductPlans(
    show: Boolean = true,
    loanTermConfigDTOList: List<ProductDetailBean>?,
    defaultSelectPlan: Int = 0,
    onExpand: () -> Unit,
    onItemClick: (Int) -> Unit,
) {
    if (!show) return
    var showPlan by remember { mutableStateOf(true) }
    val list = loanTermConfigDTOList?.get(defaultSelectPlan)?.productInstallmentPlanDTOList
    val arrowList = remember(list) {
        mutableStateListOf<Boolean>().apply {
            addAll(
                list?.getOrNull(0)
                    ?.appRepaymentPlanDTOList
                    ?.map { it.isExpend }
                    ?: emptyList()
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 12.dp)
                .background(white, RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp)
        ) {
            LazyRow(modifier = Modifier.padding(horizontal = 4.dp)) {
                item {
                    loanTermConfigDTOList?.forEachIndexed { index, item ->
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                                .size(103.dp, 108.dp)
                                .background(
                                    if (defaultSelectPlan == index) C_DAE9FF else C_F9F9F9,
                                    RoundedCornerShape(12.dp)
                                ).clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onItemClick(index)
                                }
                        ) {
                            val isInstall = !item.productInstallmentPlanDTOList.isNullOrEmpty()
                            val size =
                                if (isInstall) item.productInstallmentPlanDTOList[0].appRepaymentPlanDTOList?.size
                                    ?: 0 else 1
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 5.dp, start = 5.dp, end = 4.dp)
                                    .height(20.dp)
                            ) {
                                Text(
                                    text = size.toString(),
                                    fontSize = 18.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (defaultSelectPlan == index) C_FEB201 else C_7E7B79
                                )
                                Text(
                                    text = Strings["phase"].replace("%s", ""),
                                    fontSize = 12.sp,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.weight(1f),
                                    color = if (defaultSelectPlan == index) C_2B2621 else C_B4B0AD,
                                )
                                if (index == defaultSelectPlan) {
                                    Image(
                                        painter = painterResource(Res.drawable.batch_check),
                                        contentDescription = null,
                                    )
                                }
                            }
                            if (isInstall) {
                                Text(
                                    text = Strings["loan_period"],
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_524F4C,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${item.timeLimit ?: ""}${Strings["days"]}",
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = C_FC7700,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = Strings["first_repayment"],
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_524F4C,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(top = 1.dp, start = 10.dp, end = 10.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = item.productInstallmentPlanDTOList[0].firstRepayment.toAmountString(
                                        null
                                    ),
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = C_524F4C,
                                    modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    color = C_7E7B79,
                                    text = Strings["no_installment"],
                                    modifier = Modifier.fillMaxWidth().padding(top = 21.dp)
                                )
                            }
                        }
                    }
                }
            }
            if (!list.isNullOrEmpty() && !list[0].appRepaymentPlanDTOList.isNullOrEmpty()) {
                Image(
                    painter = painterResource(Res.drawable.product_install_arrow),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 10.dp).align(Alignment.CenterHorizontally)
                        .size(24.dp)
                        .graphicsLayer {
                            rotationZ = if (showPlan) 0f else 180f
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            showPlan = !showPlan
                            onExpand()
                        })
                Text(
                    text = Strings["installment_details"],
                    fontSize = 18.sp,
                    lineHeight = 21.sp,
                    color = C_2B2621,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp)
                )
                if (showPlan) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 10.dp)
                    ) {
                        Text(
                            text = Strings["due_date"],
                            fontSize = 14.sp,
                            color = C_524F4C,
                            lineHeight = 14.sp,
                            modifier = Modifier.width(120.dp)
                        )
                        Text(
                            text = Strings["repayment"],
                            fontSize = 14.sp,
                            color = C_524F4C,
                            lineHeight = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    list[0].appRepaymentPlanDTOList?.forEachIndexed { index, item ->
                        val expanded = arrowList.getOrNull(index) ?: false
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 10.dp)
                                .height(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.repayTime.orEmpty().substringBefore(" "),
                                fontSize = 13.sp,
                                color = C_7E7B79,
                                lineHeight = 24.sp,
                                modifier = Modifier.width(120.dp)
                            )
                            Text(
                                text = item.totalRepayment.toAmountString(null),
                                fontSize = 13.sp,
                                color = C_7E7B79,
                                lineHeight = 24.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Image(
                                painter = painterResource(Res.drawable.product_plan),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                                    .graphicsLayer {
                                        rotationZ = if (arrowList[index]) 0f else 180f
                                    }
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        arrowList[index] = !expanded
                                        onExpand()
                                    }
                            )
                        }
                        if (expanded) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, start = 12.dp, end = 12.dp)
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
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 10.dp, start = 12.dp, end = 12.dp)
                                    .background(C_F9F9F9, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 11.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = Strings["l_amount"],
                                        fontSize = 13.sp,
                                        color = C_7E7B79,
                                        lineHeight = 18.sp,
                                    )
                                    Text(
                                        text = item.repayActualAmount.toAmountString(null),
                                        fontSize = 14.sp,
                                        color = C_40495C,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                    Text(
                                        text = Strings["interest"],
                                        fontSize = 13.sp,
                                        color = C_7E7B79,
                                        lineHeight = 18.sp,
                                    )
                                    Text(
                                        text = item.repayInterestAmount.toAmountString(null),
                                        fontSize = 14.sp,
                                        color = C_40495C,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                    Text(
                                        text = Strings["service_fee"],
                                        fontSize = 13.sp,
                                        color = C_7E7B79,
                                        lineHeight = 18.sp,
                                    )
                                    Text(
                                        text = item.repayAfterHandleAmount.toAmountString(null),
                                        fontSize = 14.sp,
                                        color = C_40495C,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}