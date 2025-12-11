# 快速添加 AppsFlyer SDK 到 Xcode 项目

## 问题
如果遇到错误：`Unable to find module dependency: 'AppsFlyerLib'`

## 解决方案

### 方法 1: 使用 Swift Package Manager（推荐，最简单）

1. **打开项目**
   ```
   在 Finder 中打开：iosApp/iosApp.xcodeproj
   ```

2. **添加 Package**
   - 在 Xcode 中，点击左侧项目名称（蓝色图标）
   - 选择 `iosApp` target
   - 点击 "Package Dependencies" 标签
   - 点击左下角 "+" 按钮
   - 输入 URL：`https://github.com/AppsFlyerSDK/appsflyer-apple-sdk`
   - 点击 "Add Package"
   - 选择 "AppsFlyerLib" 产品，勾选添加到 target
   - 点击 "Add Package"

3. **完成**
   - 等待 Xcode 下载和集成
   - 重新编译项目（⌘+B）

### 方法 2: 使用 CocoaPods

如果项目已经使用 CocoaPods：

1. **创建 Podfile**（在 `iosApp` 目录下）
   ```ruby
   platform :ios, '15.1'
   use_frameworks!

   target 'iosApp' do
     pod 'AppsFlyerFramework', '~> 6.15'
   end
   ```

2. **安装依赖**
   ```bash
   cd iosApp
   pod install
   ```

3. **重新打开项目**
   - 关闭 Xcode
   - 打开 `iosApp.xcworkspace`（不是 .xcodeproj）

## 验证

编译项目应该不再有 `AppsFlyerLib` 相关的错误。



