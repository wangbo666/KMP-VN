package com.kmp.vayone.data

import com.kmp.vayone.data.CacheManager.APPCODE
import com.kmp.vayone.util.isCertPass
import kotlinx.serialization.Serializable


@Serializable
data class ParamBean(
    val version: String? = version_Name,
    val mobileType: String = "1",
    val appCode: String = APPCODE,
    val phone: String? = null,
    val company: String? = null,
    val otp: String? = null,
    val coordinate: String? = null,
    val regClient: String? = null,
    val smsCode: String? = null,
    val appsflyerId: String? = null,
    val content: String? = null,
    val channelCode: String? = null,
    val phoneMark: String? = null,
    val newPasswd: String? = null,
    val rid: Long? = null,
    val passwd: String? = null,
    val loginType: Int? = null,
    val phoneModel: String? = null,
    val phoneBrand: String? = null,
    val appVersion: String? = null,
    val parentId: String? = null,
    val education: String? = null,
    val purpose: String? = null,
    val email: String? = null,
    val sex: String? = null,
    val address: String? = null,
    val marryState: String? = null,
    val firstName: String? = null,
    val userName: String? = null,
    val cardNo: String? = null,
    val birthDate: String? = null,
    val currentAddress: String? = null,
    val province: String? = null,
    val city: String? = null,
    val salary: String? = null,
    val region: String? = null,
    val jobNature: String? = null,
    val otherRelatives: String? = null,
    val otherMobile: String? = null,
    val otherName: String? = null,
    val relativesName: String? = null,
    val relativesMobile: String? = null,
    val relatives: String? = null,
    val companyTel: String? = null,
    val companyAddress: String? = null,
    val industry: String? = null,
    val companyName: String? = null,
    val zaloAccount: String? = null,
    val facebookUid: String? = null,
    val telegramAccount: String? = null,
    val facebookAccount: String? = null,
    val bankId: String? = null,
    val accountUser: String? = null,
    val bankNo: String? = null,
    val bankCode: String? = null,
    val bankName: String? = null,
    val bankInfoId: String? = null,
    val productIds: List<Long>? = null,
    val ip: String? = null,
    val imei: String? = null,
    val payWay: String? = null,
    val userId: String? = null,
    val auditKey: String? = null,
    val amount: String? = null,
    val productId: String? = null,
    val orderId: Long? = null,
    val pageNum: Int? = null,
    val pageSize: Int? = null,
    val userCommunicationRecordStr: String? = null,
    val relativesInfoVOList: List<RelativesBean>? = null,
    val orderNoList: List<String>? = null,
    val orderNo: String? = null,
    val planNumList: List<Int?>? = null,
    val imgType: String? = null,
)

@Serializable
data class RelativesBean(
    val relatives: Int?,
    val relativesName: String? = null,
    val relativesMobile: String? = null,
)

@Serializable
data class HomeBean(
    val maxAmount: String? = null,
    val annualizedInterestRate: String? = null,
    val loanTerm: String? = null,
    val customerEmail: String? = null,
    val customerPhone: String? = null,
    val currencySymbol: String? = null,
    val currency: String? = null,
    val appApplyJumpPage: String? = null,
    val customerConfigs: List<HomeConfigBean>? = null,
)

@Serializable
data class SignBean(val verifySignSecret: String?)

@Serializable
data class LoginBean(
    val token: String,
    val id: Long,
    val phone: String,
    val appId: Long?,
    val channelId: Long?,
    val passwdSign: Int,
)

@Serializable
data class UserAuthBean(
    val id: Long = 0,
    val userId: Long = 0,
    var idState: String? = null,
    val idTime: String? = null,
    var bankCardState: String? = null,
    val bankCardTime: String? = null,
    var workInfoState: String? = null,
    val workInfoTime: String? = null,
    var kycState: String? = null,
    val kycTime: String? = null,
    val userAuthState: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val telecomPermissionState: String? = null,
) {
    fun isAuthPass(configList: List<String>): Boolean {
        val configList = configList.filterNot { it.isBlank() }
        return isCertPass(
            mapOf(
                "KYC" to (configList.contains("KYC") to kycState),
                "ID" to (configList.contains("ID") to idState),
                "TELECOM" to (configList.contains("TELECOM") to telecomPermissionState)
            )
        )
    }

    fun isFillBank(): Boolean {
        return bankCardState == "30"
    }
}
@Serializable
data class KycInfoBean(
    val id: Long,
    val userId: Long,
    val frontImageUrl: String? = null,
    val backImageUrl: String? = null,
    val liveImageUrl: String? = null,
)
@Serializable
data class PersonalInfoEnumBean(
    val gender: MutableList<EnumBean>? = null,
    val language: MutableList<EnumBean>? = null,
    val maritalStatus: MutableList<EnumBean>? = null,
    val purpose: MutableList<EnumBean>? = null,
    val education: MutableList<EnumBean>? = null,
)
@Serializable
data class WorkInfoEnumBean(
    val relatives: MutableList<EnumBean>? = null,
    val salaryRange: MutableList<EnumBean>? = null,
    val otherRelatives: MutableList<EnumBean>? = null,
    val jobnature: MutableList<EnumBean>? = null,
    val staffSize: MutableList<EnumBean>? = null,
)
@Serializable
data class EnumBean(
    val id: Int = 0,
    val state: Int = 0,
    val info: String,
)
@Serializable
data class PersonalInfoBean(
    val id: Long,
    val userId: Long,
    val firstName: String? = null,
    val marryState: Int?,
    val sex: Int?,
    val birthDate: String?,
    val purpose: Int?,
    val education: Int?,
    val currentAddress: String? = null,
    val cardNo: String? = null,
    val province: Long?,
    val city: Long?,
    val region: Long?,
    val marryStateStr: String? = null,
    val address: String? = null,
    val purposeStr: String? = null,
    val educationStr: String? = null,
    val sexStr: String? = null,
    val birthDateStr: String? = null,
    val provinceStr: String? = null,
    val cityStr: String? = null,
    val regionStr: String? = null,
    val salary: Long? = null,
    val telecomPhone: String? = null,
    val telecom: String? = null,
    val email: String? = null,
    val zaloAccount: String? = null,
)
@Serializable
data class AddressBean(
    val id: Int,
    val parentId: Long,
    val name: String? = null,
    val otherName: String? = null,
    val type: Int,
    val countryId: Int,
)
@Serializable
data class CallLogBean(
    var name: String? = "",
    var phone: String? = "",
    var callType: Int = 0,
    var lastCallTime: String? = "",
    var callTime: Int = 0,
)
@Serializable
data class ContactsInfoBean(
    val id: Long,
    val userId: Long,
    val companyName: String? = null,
    val salaryRange: Int? = null,
    val jobNature: Int? = null,
    val relatives: Int? = null,
    val relativesName: String? = null,
    val relativesMobile: String? = null,
    val otherRelatives: Int? = null,
    val otherName: String? = null,
    val otherMobile: String? = null,
    val accountUser: String? = null,
    val staffSize: Int? = null,
    val companyTel: String? = null,
    val relativesStr: String? = null,
    val collectionRelType: Int? = null,
    val collectionRelStr: String? = null,
    val otherRelativesStr: String? = null,
    val collectionOtherRelType: String? = null,
    val collectionOtherRelStr: String? = null,
    val jobNatureStr: String? = null,
    val zaloAccount: String? = null,
    val facebookUid: String? = null,
    val facebookAccount: String? = null,
    val telegramAccount: String? = null,
    val industryStr: String? = null,
    val companyAddress: String? = null,
)
@Serializable
data class PayChannelBean(
    val id: Int,
    var status: Int = 0,
    var bankCode: String? = null,
    var bankName: String? = null,
    var longCode: String? = null,
    var logoUrl: String? = null,
    var isSelect: Boolean = false,
    var countryId: Long? = null,
)
@Serializable
data class BankCardBean(
    var id: Long? = 0,
    var countryId: Int = 0,
    val userId: Int = 0,
    val bindTime: String = "",
    var bankNo: String? = "",
    val bankPhone: String = "",
    val appId: Int = 0,
    var isDefault: Int = 0,
    val createTime: String = "",
    val updateTime: String = "",
    val accountUser: String = "",
    val loanState: Int = 0,
    val payOutFailSign: Boolean = false,
    var bankCode: String = "",
    var bankName: String = "",
    var cardNo: String = "",
    var status: Int = 0,
    var isSelect: Boolean = false,
    val type: Int? = null,
    val account: String? = null,
    val name: String? = null,
    val infoStr: String? = null,
    val branchName: String? = null,
    val bankAccount: String? = null,
)
@Serializable
data class HomeLoanBean(
    val customerPhone: String? = null,
    val customerEmail: String? = null,
    val calmFlag: Boolean = false,
    val enableLoanStr: String? = null,
    val loanAmountRange: String? = null,
    val bankErrorFlag: Boolean,
    val showMultipleRepaySign: Int = 0,
    val showProducts: List<ProductBean>? = null,
    val repayProducts: List<ProductBean>? = null,
    val canNotApplyProducts: List<ProductBean>? = null,
    val userCreditAmount: String? = null,
    val userCreditCurrency: String? = null,
    val userCreditCurrencySymbol: String? = null,
    val togetherLoanSign: Int? = null,
    val canApplyAmount: String? = null,
    val allAmount: String? = null,
    val bankInfoId: Long? = null,
    val bankNo: String? = null,
    val currency: String? = null,
    val currencySymbol: String? = null,
    val isNew: Int = 1,
    val totalCreditAmount: String? = null,
    val usedAmount: String? = null,
    val userCreditStatus: Int? = null,
)
@Serializable
data class ProductBean(
    val productId: Long,
    val productName: String? = null,
    val minLoanAmount: String? = null,
    val maxLoanAmount: String? = null,
    val timeLimit: Int? = null,
    val interestRate: String? = null,
    val jumpType: Int? = null,
    val downloadUrl: String? = null,
    val tagList: List<String>? = null,
    val productImageUrl: String? = null,
    var canApply: Boolean = true,
    val showConditionTypeSign: String? = null,
    val creditStatus: Int? = null,
    val enableLoanStr: String? = null,
    var currency: String? = null,
    var currencySymbol: String? = null,
    val loanTermRange: String? = null,
    val bankErrorFlag: Boolean = false,
    val newSign: Int = 0,
    val orderId: Long? = null,
    val orderNo: String? = null,
    val loanAmount: String? = null,
    var pushStatus: Int,
    val pushMessage: String? = null,
    val repayTimeStr: String? = null,
    val orderStatus: Int? = null,
    var isTogether: Boolean = false,
    val loanAmountRange: String? = null,
    val applyDateStr: String? = null,
    val actualRepayAmount: String? = null,
    var pDetail: ProductDetailBean? = null,
    val actualAmount: String? = null,
    val interestAmount: String? = null,
    var isCheck: Boolean = true,
    val appProductHandleFeeConfigDtos: List<ProductFeeBean>? = null,
    val appRepaymentPlanDTOList: List<ProductPlanBean>? = null,
    val productInstallmentPlanDTOList: List<ProductDetailBean>? = null,
    var isFillBank: Boolean = false,
    val loanTermConfigDTOList: List<ProductDetailBean>? = null,
)  {
    fun isNormalProduct(): Boolean {
        return showConditionTypeSign == null || showConditionTypeSign == "0"
    }

    fun isAddInfoProduct(): Boolean {
        return showConditionTypeSign == "1"
    }
}

@Serializable
data class ProductPlanBean(
    val planPart: Int? = null,
    val planPartStr: String? = null,
    val timeLimit: Int? = null,
    val repayActualAmountRate: String? = null,
    val repayAfterHandleAmountRate: String? = null,
    val repayServiceFee: String? = null,
    val repayInterestAmountRate: String? = null,
    val repayActualAmount: String? = null,
    val repayAfterHandleAmount: String? = null,
    val repayInterestAmount: String? = null,
    val totalRepayment: String? = null,
    val repayTime: String? = null,
    val id: Long? = null,
    val orderNo: String? = null,
    val installmentOrderNo: String? = null,
    val loanAmount: String? = null,
    val needRepayLoanAmount: String? = null,
    val needRepayInterestSum: String? = null,
    val interestSum: String? = null,
    val actualNeedRepayAmount: String? = null,
    val afterHandleAmount: String? = null,
    var planStatus: Int? = null,
    val planStatusStr: Int? = null,
    val needRepayPenaltyAmount: String? = null,
    val needRepayAfterHandleAmount: String? = null,
    var isSelect: Boolean = false,
    var isExpend: Boolean = false,
)  {
    fun isDueAndSettle(): Boolean {
        return when (planStatus) {
            34, 35, 40, 41, 42, 43 -> true
            else -> false
        }
    }

    fun isDue(): Boolean {
        return planStatus == 34 || planStatus == 35
    }

    fun isSettle(): Boolean {
        return when (planStatus) {
            40, 41, 42, 43 -> true
            else -> false
        }
    }

    fun isProcess(): Boolean {
        return planStatus == 31
    }
}

@Serializable
data class ProductDetailBean(
    val firstRepayment: String? = null,
    val productId: Long? = null,
    val planNums: Int? = null,
    val serviceFeeType: Int? = null,
    val dailyInterest: String? = null,
    val isDefault: Int = 0,
    val defaultSign: Int = 0,
    val isDelete: Int = 0,
    val cardName: String? = null,
    val id: Long? = null,
    val appId: Long? = null,
    val productName: String? = null,
    val loanAmount: String? = null,
    val productState: Int? = null,
    val minLoanAmount: String? = null,
    val maxLoanAmount: String? = null,
    val currency: String? = null,
    val timeLimit: Int? = null,
    val loanTermRange: String? = null,
    val interestRate: Double? = null,
    val dailyAmount: String? = null,
    val interestRateType: Int? = null,
    val handleFeeState: Int? = null,
    val overdueType: Int? = null,
    val overdueValue: Double? = null,
    val maxPenaltyType: Int? = null,
    val loanAmountRange: String? = null,
    val maxPenaltyValue: Int? = null,
    val jumpType: Int? = null,
    val downloadUrl: String? = null,
    val tags: String? = null,
    val canLoanAmount: String? = null,
    val loanTermId: Long? = null,
    val repayTimeStr: String? = null,
    val installmentServiceFee: String? = null,
    val actualRepayAmount: String? = null,
    val nowTimeStr: String? = null,
    val bankNo: String? = null,
    val interestAmount: String? = null,
    val actualAmount: String? = null,
    val serviceAmount: String? = null,
    val bankInfoId: Long? = null,
    val isSign: Int? = null,
    val isNew: Int? = null,
    var canApply: Boolean,
    val loanTermList: List<LoanTermBean>? = null,
    val currencySymbol: String? = null,
    val bankInfoPayOutFailSign: Boolean = false,
    val appProductHandleFeeConfigDtos: List<ProductFeeBean>? = null,
    val appRepaymentPlanDTOList: List<ProductPlanBean>? = null,
    val productInstallmentPlanDTOList: List<ProductDetailBean>? = null,
    val repayInterestAmountRate: String? = null,
    val repayActualAmount: String? = null,
    val loanTermConfigDTOList: List<ProductDetailBean>? = null,
)

@Serializable
data class ProductFeeBean(
    val productId: Long,
    val name: String? = null,
    val nameConfig: String? = null,
    val amount: String? = null,
)

@Serializable
data class LoanTermBean(
    val id: Long? = null,
    val name: String? = null,
    val status: Int? = null,
    val timeLimit: Int? = null,
    val interestRate: Double? = null,
    val interestRateType: Int? = null,
    val handleFeeState: Int? = null,
    val defaultSign: Int? = null,
    val actualAmount: String? = null,
    val serviceAmount: String? = null,
    val afterHandleAmount: String? = null,
    val interestAmount: String? = null,
    val dailyAmount: String? = null,
    val actualRepayAmount: String? = null,
    val repayTimeStr: String? = null,
    val applySign: Int? = null,
)

@Serializable
data class OrderDetailBean(
    val appOrderInfoDto: OrderBean? = null,
    val appOrderRepayDto: OrderBean? = null,
    val bankNo: String? = null,
    val interestAmount: String? = null,
    val dailyAmount: String? = null,
    val interestRateTypeStr: String? = null,
    val actualAmount: String? = null,
    val actualRepayAmount: String? = null,
    val actualNeedRepayAmount: String? = null,
    val totalInstallmentServiceFee: String? = null,
    val afterDeductionActualNeedRepayAmount: String? = null,
    val penaltyAmount: String? = null,
    val repayCode: String? = null,
    val applyDateStr: String? = null,
    val loanDateStr: String? = null,
    val shouldRepayDateStr: String? = null,
    val dayRateStr: String? = null,
    val reliefAmount: String? = null,
    val deductionFee: String? = null,
    val userCouponName: String? = null,
    val installmentRepaymentPlanDTOList: List<ProductPlanBean>? = null,
)

@Serializable
data class OrderBean(
    val id: Long? = null,
    val userId: Long? = null,
    val orderNo: String? = null,
    val historyOrders: Int? = null,
    val productId: Long? = null,
    val loanAmount: String? = null,
    val productName: String? = null,
    val closeReason: String? = null,
    val closeRemark: String? = null,
    val closeTime: String? = null,
    val statusStr: String? = null,
    val orderId: Long? = null,
    val currency: String? = null,
    val currencySymbol: String? = null,
    val timeLimit: Int? = null,
    val status: Int? = null,
    val cardNo: String? = null,
    val createTime: String? = null,
    val bankNo: String? = null,
    val orderHandleFees: List<ProductFeeBean>? = null,
    val actualNeedRepayAmount: String? = null,
    val penaltyAmount: String? = null,
    val payGoUrl: String? = null,
)

@Serializable
data class MessagePageBean(
    val total: Int = 0,
    val list: MutableList<MessageBean>? = null,
)

@Serializable
data class MessageBean(
    val id: Long? = null,
    var readStatus: Int = 1,
    val content: String? = null,
    val createTime: String? = null,
    val respTime: String? = null,
    val theme: String? = null,
    val unreadMark: Boolean = false,
    val unreadCount: Int = 0,
)
@Serializable
data class HomeConfigBean(
    val enTitle: String? = null,
    val vernacularTitle: String? = null,
    val content: String? = null,
    val buttonType: Int? = null,
)
@Serializable
data class AuthBean(
    var src: Int = 0,
    var title: String = "",
    var type: String = "",
    var isCertified: Boolean = false,
    val authConfig: String? = "",
)
@Serializable
data class BannerBean(
    val id: Long? = null,
    val name: String? = null,
    val summary: String? = null,
    val activityPicUrl: String? = null,
    val activityH5Url: String? = null,
)
@Serializable
data class TogetherRepaymentBean(
    val payUrl: String? = null,
    val reloanButtonSign: String? = null,
)

//private int KYC_BACK;   //KYC_BACK  身份证反面 0-不需要上传 1-上传 2- 上传并完成OCR
//    private int FACE_COMPARE;   //FACE_COMPARE 人脸对比 0-不需要对比 1-需要对比
//    private int KYC_FRONT;  //KYC_FRONT 身份证正面 0-不需要上传 1-上传 2- 上传并完成OCR
//    private int FACE;       //FACE      人脸      0-不需要上传 1-拍照（自拍） 2-活体
@Serializable
data class KycConfigBean(
    val KYC_BACK: Int,
    val FACE_COMPARE: Int,
    val KYC_FRONT: Int,
    val FACE: Int,
)

