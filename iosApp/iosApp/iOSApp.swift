import SwiftUI
import AppsFlyerLib

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// 添加 AppDelegate 来处理应用生命周期
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        // 初始化 AppsFlyer
        initAppsFlyer()

        return true
    }

    private func initAppsFlyer() {
        let devKey = "gW2odiT5txpMruKrCQkrTb"
        let appleAppID = Bundle.main.bundleIdentifier ?? "com.kmp.vayone2.VayOne2" // 替换为你的 App Store ID

        print("Initializing AppsFlyer with devKey: \(devKey)")
        print("Bundle Identifier: \(Bundle.main.bundleIdentifier ?? "unknown")")

        // Configure AppsFlyer
        AppsFlyerLib.shared().appsFlyerDevKey = devKey
        AppsFlyerLib.shared().appleAppID = appleAppID
        AppsFlyerLib.shared().isDebug = true // 调试模式

        // Set conversion delegate
        AppsFlyerLib.shared().delegate = AppsFlyerConversionDelegate.shared

        // Start AppsFlyer when app becomes active
        NotificationCenter.default.addObserver(
            forName: UIApplication.didBecomeActiveNotification,
            object: nil,
            queue: .main
        ) { _ in
            print("App became active, starting AppsFlyer...")
            AppsFlyerLib.shared().start()
        }

        // Start immediately if app is already active
        if UIApplication.shared.applicationState == .active {
            print("App already active, starting AppsFlyer immediately...")
            AppsFlyerLib.shared().start()
        }

        // 打印当前配置
        print("AppsFlyer Configuration:")
        print("- Dev Key: \(AppsFlyerLib.shared().appsFlyerDevKey ?? "nil")")
        print("- Apple App ID: \(AppsFlyerLib.shared().appleAppID ?? "nil")")
        print("- AppsFlyer UID: \(AppsFlyerLib.shared().getAppsFlyerUID() as String? ?? "nil")")
    }
}