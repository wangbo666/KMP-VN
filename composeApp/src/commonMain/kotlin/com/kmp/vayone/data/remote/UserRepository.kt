package com.kmp.vayone.data.remote

import com.kmp.vayone.data.AuthBean
import com.kmp.vayone.data.BankCardBean
import com.kmp.vayone.data.BannerBean
import com.kmp.vayone.data.CacheManager
import com.kmp.vayone.data.HomeBean
import com.kmp.vayone.data.HomeLoanBean
import com.kmp.vayone.data.KycConfigBean
import com.kmp.vayone.data.KycInfoBean
import com.kmp.vayone.data.LoginBean
import com.kmp.vayone.data.MessagePageBean
import com.kmp.vayone.data.OrderBean
import com.kmp.vayone.data.ParamBean
import com.kmp.vayone.data.ProductBean
import com.kmp.vayone.data.SignBean
import com.kmp.vayone.data.TogetherRepaymentBean
import com.kmp.vayone.data.UserAuthBean

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
}