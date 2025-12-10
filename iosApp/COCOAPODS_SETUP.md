# CocoaPods 集成 AppsFlyer SDK 步骤

## 前置条件

确保已安装 CocoaPods：
```bash
sudo gem install cocoapods
```

如果安装失败，可能需要使用：
```bash
sudo gem install cocoapods -v 1.15.2
```

## 步骤

### 1. 进入 iosApp 目录
```bash
cd iosApp
```

### 2. 安装依赖
```bash
pod install
```

### 3. 使用 .xcworkspace 打开项目
**重要：** 使用 CocoaPods 后，必须使用 `.xcworkspace` 文件打开项目，而不是 `.xcodeproj`

```bash
open iosApp.xcworkspace
```

或者在 Finder 中双击 `iosApp.xcworkspace` 文件

### 4. 验证

在 Xcode 中：
- 编译项目（⌘+B），应该不再有 `AppsFlyerLib` 相关的错误
- 检查左侧项目导航器，应该能看到 "Pods" 项目

## 常见问题

### 问题 1: pod install 失败
```bash
# 更新 CocoaPods repo
pod repo update

# 清理并重新安装
rm -rf Pods Podfile.lock
pod install
```

### 问题 2: 版本冲突
如果遇到版本冲突，可以指定具体版本：
```ruby
pod 'AppsFlyerFramework', '6.15.0'
```

### 问题 3: 需要更新依赖
```bash
pod update AppsFlyerFramework
```

## 后续操作

- 每次拉取代码后，如果 Podfile 有变化，运行 `pod install`
- 团队成员也需要运行 `pod install`
- 永远使用 `.xcworkspace` 打开项目

