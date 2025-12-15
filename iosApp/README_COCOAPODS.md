# 使用 CocoaPods 安装 AppsFlyer SDK

## 快速开始

### 1. 安装 CocoaPods（如果还没有安装）

```bash
sudo gem install cocoapods
```

### 2. 安装依赖

在项目根目录运行：

```bash
cd iosApp
pod install
```

### 3. 打开项目

**重要：** 使用 CocoaPods 后，必须使用 `.xcworkspace` 文件打开项目：

```bash
open iosApp.xcworkspace
```

或者在 Finder 中双击 `iosApp.xcworkspace` 文件（不是 `.xcodeproj`）

## 完成！

现在 `import AppsFlyerLib` 应该可以正常工作了。

## 常见命令

```bash
# 安装依赖
pod install

# 更新依赖
pod update

# 更新特定 pod
pod update AppsFlyerFramework

# 查看已安装的 pods
pod list
```

## 注意事项

- ✅ 永远使用 `.xcworkspace` 打开项目
- ✅ 团队成员拉取代码后需要运行 `pod install`
- ✅ `Pods/` 目录已添加到 `.gitignore`，不需要提交到 Git





