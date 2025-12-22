package com.kmp.vayone.data.remote

import com.kmp.vayone.currentTimeMillis
import com.kmp.vayone.data.AddressBean
import com.kmp.vayone.data.AuthBean
import com.kmp.vayone.data.BankCardBean
import com.kmp.vayone.data.BannerBean
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.CacheManager.getLocation
import com.kmp.vayone.data.CacheManager.getLoginInfo
import com.kmp.vayone.data.ContactsInfoBean
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.KycConfigBean
import com.kmp.vayone.data.KycInfoBean
import com.kmp.vayone.data.LoginBean
import com.kmp.vayone.data.MessagePageBean
import com.kmp.vayone.data.OrderBean
import com.kmp.vayone.data.OrderDetailBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.PayChannelBean
import com.kmp.vayone.data.PersonalInfoBean
import com.kmp.vayone.data.PersonalInfoEnumBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.ProductDetailBean
import com.kmp.vayone.data.SignBean
import com.kmp.vayone.data.TogetherRepaymentBean
import com.kmp.vayone.data.UserAuthBean
import com.kmp.vayone.data.WorkInfoEnumBean
import com.kmp.vayone.data.version_Name
import com.kmp.vayone.getDeviceId
import com.kmp.vayone.getLocalIpAddress
import com.kmp.vayone.mobileType
import com.kmp.vayone.randomUUID
import com.kmp.vayone.readImageBytes
import com.kmp.vayone.util.parseLongIntMap
import com.kmp.vayone.util.parseLongLongMap
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlin.collections.set
import kotlin.toString

// 使用示例
object UserRepository {
    private val networkManager = NetworkManager(
        baseUrl = CacheManager.HTTP_HOST
    )

    suspend fun getSecret(): ApiResponse<SignBean?> {
        return networkManager.get("api/user/app/common/secret")
    }

    suspend fun getHomeUnCertData(): ApiResponse<HomeBean?> {
        return networkManager.post("api/loan/app/common/index")
    }

    suspend fun sendOTP(phone: String): ApiResponse<Boolean?> {
        return networkManager.post("api/user/app/login/sms", ParamBean(phone = phone))
    }

    suspend fun login(paramBean: ParamBean): ApiResponse<LoginBean?> {
        return networkManager.post("api/user/app/login", paramBean)
    }

    suspend fun postDeviceInfo(paramBean: ParamBean): ApiResponse<String?> {
        return networkManager.post("api/user/app/userEquipment/save", paramBean)
    }

    suspend fun setPassword(paramBean: ParamBean): ApiResponse<LoginBean?> {
        return networkManager.post("api/user/app/password/set", paramBean)
    }

    suspend fun logout(): ApiResponse<String?> {
        return networkManager.post(
            "api/user/app/delete/user",
            ParamBean(rid = CacheManager.getLoginInfo()?.id)
        )
    }

    suspend fun updatePassword(paramBean: ParamBean): ApiResponse<LoginBean?> {
        return networkManager.post("api/user/app/password/update", paramBean)
    }

    suspend fun getHomeAuthData(): ApiResponse<HomeLoanBean?> {
        return networkManager.post("api/loan/app/index/v3")
    }

    suspend fun getTogetherRepaymentList(): ApiResponse<List<ProductBean>?> {
        return networkManager.post("api/finance/app/multiple/order/list")
    }

    suspend fun togetherRepayment(orderList: List<String>): ApiResponse<TogetherRepaymentBean?> {
        return networkManager.post(
            "api/finance/app/multiple/order/repay",
            ParamBean(orderNoList = orderList)
        )
    }

    suspend fun getAuthStatus(): ApiResponse<UserAuthBean?> {
        return networkManager.post("api/user/app/userAuth/detail")
    }

    suspend fun getAuthConfig(): ApiResponse<AuthBean?> {
        return networkManager.get("api/user/app/application/config/auth/config")
    }

    suspend fun getBannerList(): ApiResponse<List<BannerBean>?> {
        return networkManager.get("api/user/app/activity/list")
    }

    suspend fun getMessageList(): ApiResponse<MessagePageBean?> {
        return networkManager.post(
            "api/data/app/fcm/sendRecord/list", ParamBean(pageNum = 1, pageSize = 999)
        )
    }

    suspend fun markMessagesRead(recordIdList: List<Long>): ApiResponse<String?> {
        return networkManager.post(
            "api/data/app/fcm/sendRecord/update",
            ParamBean(recordIdList = recordIdList)
        )
    }

    suspend fun getOrderList(): ApiResponse<List<OrderBean>?> {
        return networkManager.post("api/loan/app/order/oldList")
    }

    suspend fun getBankcardList(): ApiResponse<List<BankCardBean>?> {
        return networkManager.post("api/user/app/bank/myCard")
    }

    suspend fun setCardDefault(id: String?): ApiResponse<String?> {
        return networkManager.post("api/user/app/bank/setDefault", ParamBean(bankInfoId = id))
    }

    suspend fun unbindCard(id: String?): ApiResponse<String?> {
        return networkManager.post("api/user/app/bank/unbind", ParamBean(bankInfoId = id))
    }

    suspend fun getKycConfig(): ApiResponse<KycConfigBean?> {
        return networkManager.post("api/user/app/kyc/config")
    }

    suspend fun getKycInfo(): ApiResponse<KycInfoBean?> {
        return networkManager.post("api/user/app/kyc/info")
    }

    suspend fun submitKycCard(imgType: String, imageBytes: ByteArray): ApiResponse<String?> {
        val list = withContext(Dispatchers.IO) {
            formData {
                // 添加图片文件
                append(
                    key = "image",
                    value = imageBytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${currentTimeMillis()}\""
                        )
                    }
                )
                // 添加其他参数
                append("mobileType", mobileType())
                append("appCode", CacheManager.APPCODE)
                append("version", version_Name)
                append("imgType", imgType)
            }
        }
        return networkManager.postMultipart<String?>("api/user/app/kyc/save/v2", list)
    }

    suspend fun submitKycSelf(
        faceFile: ByteArray,
        livenessDataFile: ByteArray?
    ): ApiResponse<String?> {
        val list = withContext(Dispatchers.IO) {
            formData {
                // 添加图片文件
                append(
                    key = "faceFile",
                    value = faceFile,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "image/*")
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${currentTimeMillis()}\""
                        )
                    }
                )
                livenessDataFile?.let {
                    append(
                        key = "livenessDataFile",
                        value = livenessDataFile,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/*")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${currentTimeMillis()}\""
                            )
                        }
                    )
                }
                // 添加其他参数
                append("mobileType", mobileType())
                append("appCode", CacheManager.APPCODE)
                append("version", version_Name)
                append("imageId", randomUUID())
            }
        }
        return networkManager.postMultipart("api/user/app/kyc/liveness/anti/hack", list)
    }

    suspend fun faceCompare(): ApiResponse<String?> {
        return networkManager.post("api/user/app/kyc/face/compare")
    }

    suspend fun getPersonalInfoEnum(): ApiResponse<PersonalInfoEnumBean?> {
        return networkManager.post("api/user/app/userBaseExt/getEnum")
    }

    suspend fun getPersonalInfo(): ApiResponse<PersonalInfoBean?> {
        return networkManager.post("api/user/app/userBaseExt/info")
    }

    suspend fun submitPersonalInfo(paramBean: ParamBean): ApiResponse<String?> {
        return networkManager.post("api/user/app/userBaseExt/save/v2", paramBean)
    }

    suspend fun getAddressList(id: String?): ApiResponse<List<AddressBean>?> {
        return networkManager.post("api/user/app/address/list", ParamBean(parentId = id))
    }

    suspend fun getPayChannel(): ApiResponse<List<PayChannelBean>?> {
        return networkManager.post("api/user/app/bank/list")
    }

    suspend fun getContactInfo(): ApiResponse<ContactsInfoBean?> {
        return networkManager.post("api/user/app/userWork/info")
    }

    suspend fun getWorkInfoEnum(): ApiResponse<WorkInfoEnumBean?> {
        return networkManager.post("api/user/app/userWork/enum")
    }

    suspend fun submitBankAndContactInfo(paramBean: ParamBean): ApiResponse<String?> {
        return networkManager.post("api/user/app/bank/bind/v2", paramBean)
    }

    suspend fun addAccount(paramBean: ParamBean): ApiResponse<String?> {
        return networkManager.post("api/user/app/bank/addBank", paramBean)
    }

    suspend fun submitSuppleInfo(paramBean: ParamBean): ApiResponse<String?> {
        return networkManager.post("api/user/app/userBaseExt/save/work/v2", paramBean)
    }

    suspend fun getProductDetail(paramBean: ParamBean): ApiResponse<ProductDetailBean?> {
        return networkManager.post("api/loan/app/productInfo/detail", paramBean)
    }

    suspend fun togetherLoan(
        bankId: String,
        productList: List<ProductBean>,
        signPath: String?,
        installmentMap: String?,
        termIdMap: String?
    ): ApiResponse<List<ProductBean>?> {
        val list = withContext(Dispatchers.IO) {
            formData {
                append("mobileType", mobileType())
                append("appCode", CacheManager.APPCODE)
                append("version", version_Name)
                append("bankInfoId", bankId)
                append("userId", getLoginInfo()?.id.toString())
                append("payWay", "CARD")
                append("ip", getLocalIpAddress().orEmpty())
                append("imei", getDeviceId())
                append("coordinate", "${getLocation().first},${getLocation().second}")
                append("auditKey", "auditKey")
                if (installmentMap != null) {
                    append("productInstallmentMap", installmentMap)
                }
                if (termIdMap != null) {
                    append("productLoanTermIdMap", termIdMap)
                }
                append(
                    "productIds",
                    productList.joinToString(",") { it1 -> it1.productId.toString() })
                val planNums = installmentMap.parseLongIntMap().values.firstOrNull()
                if (planNums != null) {
                    append("planNums", planNums.toString())
                }
                val termId = termIdMap.parseLongLongMap().values.firstOrNull()
                if (termId != null) {
                    append("loanTermId", termId.toString())
                }
                append(
                    key = "signPic",
                    value = readImageBytes(signPath.orEmpty()),
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "image/*")
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${currentTimeMillis()}\""
                        )
                    }
                )
            }
        }
        return networkManager.postMultipart("api/loan/app/order/commit/all/with/event", list)
    }

    suspend fun singleLoan(
        productId: String,
        amount: String,
        bankId: String,
        signPath: String?,
        installmentMap: String?,
        termIdMap: String?
    ): ApiResponse<ProductBean?> {
        val list = withContext(Dispatchers.IO) {
            formData {
                append("mobileType", mobileType())
                append("appCode", CacheManager.APPCODE)
                append("version", version_Name)
                append("bankInfoId", bankId)
                append("userId", getLoginInfo()?.id.toString())
                append("payWay", "CARD")
                append("ip", getLocalIpAddress().orEmpty())
                append("imei", getDeviceId())
                append("coordinate", "${getLocation().first},${getLocation().second}")
                append("auditKey", "auditKey")
                append("productId", productId)
                append("amount", amount)
                val planNums = installmentMap.parseLongIntMap().values.firstOrNull()
                if (planNums != null) {
                    append("planNums", planNums.toString())
                }
                val termId = termIdMap.parseLongLongMap().values.firstOrNull()
                if (termId != null) {
                    append("loanTermId", termId.toString())
                }
                append(
                    key = "signPic",
                    value = readImageBytes(signPath.orEmpty()),
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "image/*")
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${currentTimeMillis()}\""
                        )
                    }
                )
            }
        }
        return networkManager.postMultipart("api/loan/app/order/commit/with/event", list)
    }

    suspend fun getOrderDetail(orderId: Long?): ApiResponse<OrderDetailBean?> {
        return networkManager.post("api/loan/app/order/detail", ParamBean(orderId = orderId))
    }

    suspend fun showRepaymentBorrow(): ApiResponse<TogetherRepaymentBean?> {
        return networkManager.get("api/user/app/common/reloan/button/sign")
    }

    suspend fun installmentRepay(paramBean: ParamBean): ApiResponse<TogetherRepaymentBean?> {
        return networkManager.post("api/finance/app/order/repay/url", paramBean)
    }

    suspend fun repayAndBorrow(id: Long?): ApiResponse<TogetherRepaymentBean?>{
        return networkManager.post("api/loan/app/apply/again", ParamBean(orderId = id))
    }
}
