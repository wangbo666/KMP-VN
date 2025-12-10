import Foundation
import Security
import UIKit

@objc public class DeviceIdHelper: NSObject {

    @objc public static let shared = DeviceIdHelper()
    private let key = "persistent_device_id_v1"

    private override init() { super.init() }

    @objc public func getDeviceId() -> String {
        if let saved = getFromKeychain() { return saved }

        let idfv = UIDevice.current.identifierForVendor?.uuidString ?? UUID().uuidString
        saveToKeychain(value: idfv)
        return idfv
    }

    private func saveToKeychain(value: String) {
        guard let data = value.data(using: .utf8) else { return }
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecValueData as String: data
        ]
        SecItemDelete(query as CFDictionary)
        SecItemAdd(query as CFDictionary, nil)
    }

    private func getFromKeychain() -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        guard status == errSecSuccess else { return nil }
        if let data = result as? Data {
            return String(data: data, encoding: .utf8)
        }
        return nil
    }
}
