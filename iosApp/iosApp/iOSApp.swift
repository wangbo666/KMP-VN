import SwiftUI
import AppsFlyerLib

@main
struct iOSApp: App {
    init() {
        // Initialize AppsFlyer directly in Swift
        initAppsFlyer()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
    
    private func initAppsFlyer() {
        let devKey = "gW2odiT5txpMruKrCQkrTb"
        
        // Configure AppsFlyer
        AppsFlyerLib.shared().appsFlyerDevKey = devKey
        AppsFlyerLib.shared().appleAppID = Bundle.main.bundleIdentifier ?? ""
        
        // Set conversion delegate
        AppsFlyerLib.shared().delegate = AppsFlyerConversionDelegate.shared
        
        // Start AppsFlyer when app becomes active
        NotificationCenter.default.addObserver(
            forName: UIApplication.didBecomeActiveNotification,
            object: nil,
            queue: .main
        ) { _ in
            AppsFlyerLib.shared().start()
        }
        
        // Start immediately if app is already active
        if UIApplication.shared.applicationState == .active {
            AppsFlyerLib.shared().start()
        }
    }
}