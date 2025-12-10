import Foundation
import UIKit
import AppsFlyerLib

// 在 Swift 中创建简单的缓存存储类
class SimpleCache {
    static let shared = SimpleCache()

    private let defaults = UserDefaults.standard

    func saveAppsFlyerUID(uid: String) {
        defaults.set(uid, forKey: "appsFlyerUID")
        print("Saved AppsFlyer UID: \(uid)")
    }

    func saveAppFlyer(json: String) {
        defaults.set(json, forKey: "appFlyerData")
        print("Saved AppsFlyer JSON data")
    }

    func saveReferrer(referrer: String) {
        defaults.set(referrer, forKey: "referrer")
        print("Saved Referrer: \(referrer)")
    }

    func getAppsFlyerUID() -> String {
        return defaults.string(forKey: "appsFlyerUID") ?? ""
    }

    func getAppFlyer() -> String {
        return defaults.string(forKey: "appFlyerData") ?? ""
    }

    func getReferrer() -> String {
        return defaults.string(forKey: "referrer") ?? ""
    }
}

// AppsFlyer Conversion Delegate
class AppsFlyerConversionDelegate: NSObject, AppsFlyerLibDelegate {
    static let shared = AppsFlyerConversionDelegate()

    private override init() {
        super.init()
        print("AppsFlyerConversionDelegate initialized")
    }

    func onConversionDataSuccess(_ conversionInfo: [AnyHashable : Any]) {
        print("=== AppsFlyer Conversion Data Success ===")

        var jsonDict: [String: String] = [:]

        // 获取 UID
        let uid = AppsFlyerLib.shared().getAppsFlyerUID() as String? ?? ""
        print("AppsFlyer UID: \(uid)")

        // 保存到缓存
        SimpleCache.shared.saveAppsFlyerUID(uid: uid)
        jsonDict["apps_flyer_uid"] = uid

        // 解析所有 conversion 数据
        for (key, value) in conversionInfo {
            if let keyStr = key as? String {
                jsonDict[keyStr] = String(describing: value)
                print("\(keyStr) = \(value)")
            }
        }

        // 转换为 JSON 字符串
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonDict, options: .prettyPrinted)
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                SimpleCache.shared.saveAppFlyer(json: jsonString)
                print("Saved conversion data as JSON")
            }
        } catch {
            print("Failed to serialize conversion data: \(error)")
        }

        // 处理安装类型
        if let status = conversionInfo["af_status"] as? String {
            print("Install Status: \(status)")
            if status == "Non-organic" {
                if let sourceID = conversionInfo["media_source"],
                   let campaign = conversionInfo["campaign"] {
                    print("Non-organic install. Source: \(sourceID), Campaign: \(campaign)")
                }
            } else {
                print("Organic install")
            }
        }

        print("========================================")
    }

    func onConversionDataFail(_ error: Error) {
        print("❌ AppsFlyer Conversion Data Failed: \(error.localizedDescription)")
    }

    func onAppOpenAttribution(_ attributionInfo: [AnyHashable : Any]) {
        print("=== AppsFlyer Attribution Data ===")

        for (key, value) in attributionInfo {
            print("\(key) = \(value)")

            // 保存 referrer
            if key as? String == "referrer" {
                let referrer = String(describing: value)
                SimpleCache.shared.saveReferrer(referrer: referrer)
            }
        }

        print("==================================")
    }

    func onAppOpenAttributionFailure(_ error: Error) {
        print("❌ AppsFlyer Attribution Failure: \(error.localizedDescription)")
    }
}