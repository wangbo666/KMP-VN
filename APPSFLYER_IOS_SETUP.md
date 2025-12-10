# AppsFlyer iOS 集成说明

## ⚠️ 重要：必须先添加 AppsFlyer SDK 依赖才能编译

## 步骤 1: 添加 AppsFlyer SDK 依赖

### 方式一：使用 Swift Package Manager (推荐)

**详细步骤：**

1. **打开 Xcode 项目**
   - 在 Finder 中导航到 `iosApp` 目录
   - 双击 `iosApp.xcodeproj` 文件打开项目

2. **添加 Package Dependency**
   - 在 Xcode 左侧项目导航器中，点击最顶部的项目名称（蓝色图标）
   - 在中间面板中，选择 `iosApp` target（不是项目）
   - 点击顶部的 **"Package Dependencies"** 标签页

3. **添加 AppsFlyer Package**
   - 点击左下角的 **"+"** 按钮
   - 在弹出的搜索框中输入以下 URL：
     ```
   https://github.com/AppsFlyerSDK/AppsFlyerFramework-Static
     ```
   - 按回车或点击搜索
   - 等待 Xcode 加载 package 信息

4. **选择版本和产品**
   - 在 "Dependency Rule" 中选择版本规则（建议选择 "Up to Next Major Version" 并输入 `6.15.0`）
   - 点击 **"Add Package"** 按钮
   - 在下一个对话框中，确保 **"AppsFlyerLib"** 产品被选中（在右侧的 "Add to Target" 列中勾选）
   - 点击 **"Add Package"** 完成添加

5. **验证添加成功**
   - 在 "Package Dependencies" 标签页中应该能看到 `appsflyer-apple-sdk`
   - 尝试编译项目（⌘+B），应该不再有 "Unable to find module dependency: 'AppsFlyerLib'" 错误

### 方式二：使用 CocoaPods

1. 在 `iosApp` 目录下创建 `Podfile`（如果不存在）：
   ```ruby
   platform :ios, '15.1'
   use_frameworks!

   target 'iosApp' do
     pod 'AppsFlyerFramework', '~> 6.15'
   end
   ```

2. 在终端中运行：
   ```bash
   cd iosApp
   pod install
   ```

3. 使用生成的 `.xcworkspace` 文件打开项目（而不是 `.xcodeproj`）

## 步骤 2: 配置 Info.plist

确保 `Info.plist` 中已配置必要的权限和设置（通常已经配置好了）。

## 步骤 3: 验证集成

1. 编译项目，确保没有编译错误
2. 运行应用，检查控制台是否有 AppsFlyer 相关的日志
3. 登录时，AppsFlyer 数据会自动收集并发送到服务器

## 注意事项

- AppsFlyer SDK 会在应用启动时自动初始化（在 `iOSApp.swift` 的 `init()` 方法中）
- 转化数据会通过 `AppsFlyerConversionDelegate` 回调保存到本地
- `media_source` 会单独保存，完整的转化数据会以 JSON 格式保存
- 登录时会自动获取 AppsFlyer UID 和转化数据并发送到服务器

## 测试

1. 安装应用后，AppsFlyer 会自动开始收集数据
2. 可以通过 AppsFlyer 控制台查看数据
3. 登录时检查网络请求，确认 `appsflyerId` 和 `content` 字段已正确发送

