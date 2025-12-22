package com.kmp.vayone.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.vayone.data.BankCardBean
import com.kmp.vayone.data.CacheManager.LEASE_AGREEMENT
import com.kmp.vayone.data.CacheManager.PAWN_AGREEMENT
import com.kmp.vayone.data.CacheManager.getLoginInfo
import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.Strings
import com.kmp.vayone.data.remote.json
import com.kmp.vayone.getPhoneModel
import com.kmp.vayone.navigation.Screen
import com.kmp.vayone.postAllPermissions
import com.kmp.vayone.ui.widget.LoadingDialog
import com.kmp.vayone.ui.widget.SignPageParams
import com.kmp.vayone.ui.widget.TopBar
import com.kmp.vayone.util.format
import com.kmp.vayone.util.isPositive
import com.kmp.vayone.util.log
import com.kmp.vayone.util.maskString
import com.kmp.vayone.util.toAmountString
import com.kmp.vayone.viewmodel.CertViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.C_2B2621
import theme.C_2D3C52
import theme.C_40495C
import theme.C_524F4C
import theme.C_6A707D
import theme.C_7E7B79
import theme.C_B6DFFF
import theme.C_F8F4F0
import theme.C_FC7700
import theme.C_FCFCFC
import theme.C_FFBB48
import theme.white
import vayone.composeapp.generated.resources.Res
import vayone.composeapp.generated.resources.mine_right
import vayone.composeapp.generated.resources.product_icon
import vayone.composeapp.generated.resources.product_plan

@Composable
fun TogetherScreen(
    loanBean: HomeLoanBean,
    toast: (show: Boolean, message: String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    val productList = remember { loanBean.showProducts ?: emptyList() }
    val certViewModel = remember { CertViewModel() }
    val cardList by certViewModel.accountList.collectAsState()
    var showCardDialog by remember { mutableStateOf(false) }
    val loading by certViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    var showLoanDialog by remember { mutableStateOf(false) }
    val leaseUrl = remember {
        LEASE_AGREEMENT + "userId=${getLoginInfo()?.id}"
    }
    val pawnUrl = remember {
        PAWN_AGREEMENT + "userId=${getLoginInfo()?.id}"
    }
    var cardInfo by remember {
        mutableStateOf<BankCardBean?>(
            BankCardBean(
                id = loanBean.bankInfoId,
                bankNo = loanBean.bankNo
            )
        )
    }
    val termMap = remember { mutableStateMapOf<Long, Long>() }
    val installmentMap = remember { mutableStateMapOf<Long, Int>() }

    val listState = rememberLazyListState()

    LaunchedEffect(certViewModel) {
        launch {
            certViewModel.errorEvent.collect { event ->
                toast(event.showToast, event.message)
            }
        }
        launch {
            certViewModel.getBankCardList(false)
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
            onBack()
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(top = 6.dp)) {
            items(productList.size, key = { it }) { index ->
                TogetherProduct(
                    item = productList[index],
                    onTermChanged = { productId, termId ->
                        termMap[productId] = termId
                    },
                    onInstallmentChanged = { productId, planNums ->
                        installmentMap[productId] = planNums
                    }) {
                    scope.launch {
                        listState.animateScrollToItem(index, scrollOffset = if (it) 1000 else 0)
                    }
                }
            }
        }
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
                if (cardInfo?.bankNo == null) {
                    cardInfo = cardList.firstOrNull { it.isDefault == 1 }
                }
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
                            if (cardList.isEmpty()) {
                                certViewModel.getBankCardList(true)
                            }
                        })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["total_repayment"],
                    fontSize = 13.sp,
                    lineHeight = 23.sp,
                    color = C_40495C,
                )
                Text(
                    text = loanBean.canApplyAmount.toAmountString(loanBean.currencySymbol),
                    fontSize = 20.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = C_40495C,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["together_num"],
                    fontSize = 13.sp,
                    lineHeight = 23.sp,
                    color = C_40495C,
                )
                Text(
                    text = productList.size.toString(),
                    fontSize = 20.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = C_40495C,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
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
    ShowLoanAgreementDialog(showLoanDialog, agreementClick = {
        navigate(
            Screen.WebView(
                if (it == 0) Strings["lease_contract"] else Strings["mortgage_contract"],
                if (it == 0) leaseUrl else pawnUrl
            )
        )
    }, confirmClick = {
        json.encodeToString(
            installmentMap.toMap()
        ).log()
        json.encodeToString(termMap.toMap()).log()
        navigate(
            Screen.LoanResult(
                SignPageParams(
                    cardInfo?.id,
                    productList,
                    null,
                    cardInfo?.id,
                    null,
                    if (installmentMap.isEmpty()) null else json.encodeToString(
                        installmentMap.toMap()
                    ),
                    json.encodeToString(termMap.toMap()),
                )
            )
        )
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
fun PreTogether() {
    TogetherScreen(HomeLoanBean(), onBack = {}) {}
}

@Composable
fun TogetherProduct(
    item: ProductBean,
    onTermChanged: (Long, Long) -> Unit,
    onInstallmentChanged: (Long, Int) -> Unit,
    onExpand: (Boolean) -> Unit
) {
    var showPlan by remember { mutableStateOf(false) }
    var showDetail by remember { mutableStateOf(false) }
    var timeLimit by remember { mutableStateOf(item.timeLimit) }
    var actualAmount by remember { mutableStateOf(item.actualAmount) }
    var interestRate by remember { mutableStateOf(item.interestRate) }
    var interestAmount by remember { mutableStateOf(item.interestAmount) }
    var repayTimeStr by remember { mutableStateOf(item.repayTimeStr) }
    var appProductHandleFeeConfigDtos by remember { mutableStateOf(item.appProductHandleFeeConfigDtos) }
    var loanTermConfigDTOList by remember { mutableStateOf(item.loanTermConfigDTOList) }
    var defaultSelectPlan by remember {
        mutableStateOf(
            loanTermConfigDTOList?.indexOfFirst { it1 -> it1.defaultSign == 1 }
                ?.coerceIn(0, Int.MAX_VALUE) ?: 0
        )
    }
    val data = loanTermConfigDTOList!![defaultSelectPlan]

    LaunchedEffect(defaultSelectPlan) {
        onTermChanged(item.productId, data.id!!)
        data.productInstallmentPlanDTOList?.firstOrNull()?.let {
            onInstallmentChanged(item.productId, it.planNums!!)
        }
    }
    Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 10.dp)) {
        if (showDetail) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 102.dp)
                    .background(
                        C_F8F4F0,
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 42.dp, start = 10.dp, end = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            .height(20.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["loan_period_days"],
                            fontSize = 13.sp,
                            color = C_6A707D,
                        )
                        Text(
                            text = timeLimit.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_40495C,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            .height(20.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["actually_amount"].replace("(%s)", ""),
                            fontSize = 13.sp,
                            color = C_6A707D,
                        )
                        Text(
                            text = actualAmount.toAmountString(item.currencySymbol),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_40495C,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            .height(20.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["interest_day"].format("$interestRate%"),
                            fontSize = 13.sp,
                            color = C_6A707D,
                        )
                        Text(
                            text = interestAmount.toAmountString(
                                null
                            ),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_40495C,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            .height(20.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["due_date"],
                            fontSize = 13.sp,
                            color = C_6A707D,
                        )
                        Text(
                            text = repayTimeStr.orEmpty(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_40495C,
                        )
                    }
                    appProductHandleFeeConfigDtos?.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                                .height(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.getFeeName(),
                                fontSize = 13.sp,
                                color = C_6A707D,
                            )
                            Text(
                                text = item.amount.toAmountString(null),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 14.sp,
                                color = C_40495C,
                            )
                        }
                    }
                    if (!loanTermConfigDTOList.isNullOrEmpty()) {
                        val data = loanTermConfigDTOList!![defaultSelectPlan]
                        if (data.installmentServiceFee.isPositive()) {
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
                                    text = data.installmentServiceFee.toAmountString(item.currencySymbol),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    fontSize = 14.sp,
                                    color = C_524F4C,
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            .height(20.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings["phone_model"],
                            fontSize = 13.sp,
                            color = C_6A707D,
                        )
                        Text(
                            text = getPhoneModel(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            color = C_40495C,
                        )
                    }
                    if (!loanTermConfigDTOList.isNullOrEmpty()) {
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
                                        onExpand(false)
                                    }
                            )
                        }
                        ProductPlans(
                            showPlan,
                            loanTermConfigDTOList,
                            defaultSelectPlan,
                            onExpand = {
                                onExpand(true)
                            }
                        ) {
                            defaultSelectPlan = it
                            val newItem = loanTermConfigDTOList!![defaultSelectPlan]
                            timeLimit = newItem.timeLimit
                            actualAmount = newItem.actualAmount
                            interestRate = newItem.interestRate.toString()
                            interestAmount = newItem.interestAmount
                            repayTimeStr = newItem.repayTimeStr
                            appProductHandleFeeConfigDtos = newItem.appProductHandleFeeConfigDtos
                        }
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().height(102.dp).background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            C_FC7700, C_FFBB48
                        )
                    ),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.product_icon),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 10.dp, top = 5.dp).size(38.dp)
                    )
                    Text(
                        text = item.productName.orEmpty(),
                        fontSize = 14.sp,
                        lineHeight = 38.sp,
                        color = white,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp),
                        textAlign = TextAlign.Center
                    )
                    if (item.loanTermConfigDTOList?.any { loanTermConfig ->
                            loanTermConfig.productInstallmentPlanDTOList?.any { productInstallmentPlanDTO ->
                                !productInstallmentPlanDTO.appRepaymentPlanDTOList.isNullOrEmpty()
                            } == true
                        } == true) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = Strings["installment_product"],
                            color = C_2D3C52,
                            fontSize = 10.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(start = 7.dp)
                                .background(C_B6DFFF, RoundedCornerShape(30.dp))
                                .padding(horizontal = 9.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                ) {
                    Text(
                        text = Strings["borrow_days_str"],
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = white,
                        maxLines = 1,
                        modifier = Modifier.weight(3f)
                    )
                    Text(
                        text = timeLimit.toString(),
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        color = white,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp)
                ) {
                    Text(
                        text = Strings["l_amount"],
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = white,
                        modifier = Modifier.weight(3f)
                    )
                    Text(
                        text = item.maxLoanAmount.toAmountString(item.currencySymbol),
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        color = white,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(33.dp)
                    .background(
                        white,
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings["product_details"],
                    fontSize = 12.sp,
                    color = C_FC7700,
                    modifier = Modifier.padding(start = 16.dp).weight(1f),
                )
                Image(
                    painter = painterResource(Res.drawable.mine_right),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp).size(24.dp)
                        .graphicsLayer {
                            rotationZ = if (showDetail) 270f else 90f
                        }.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            showDetail = !showDetail
                            onExpand(false)
                        }
                )
            }
        }
    }
}