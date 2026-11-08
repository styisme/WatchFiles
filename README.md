**简体中文** | [English](README_EN.md)

# WatchFiles

> 基于 [wear-uikit](https://github.com/styisme/wear-uikit) 的手表文件管理器。
> 纯 Java 动态渲染 + XML 资源，无 AndroidX。最低支持 Android 2.3（API 10）。

[![Build WatchFiles](https://github.com/styisme/watchfiles/actions/workflows/build.yml/badge.svg)](https://github.com/styisme/watchfiles/actions/workflows/build.yml)
[![Version](https://img.shields.io/badge/version-1.1.0-1976D2)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%202.3%2B-brightgreen)]()

---

## 功能

| 类别 | 内容 |
|---|---|
| 文件管理 | 浏览、排序（名称/大小/时间/类型）、隐藏文件、新建、重命名、复制、剪切、粘贴、删除、复制路径、分享 |
| 预览 | 文本（UTF-8/GBK 自动探测、二进制检测、256KB 截断）、图片（采样防 OOM、双指缩放、单指拖动） |
| 解压 | ZIP 内置；RAR / 7z 为可选依赖，见下文 |
| 系统集成 | 系统文件选择器（API 19+ SAF，低版本回退 GET_CONTENT）、自写 ContentProvider 分享 |
| 设置 | 主题三态、主题色（蓝/靛/紫/橙/青/蓝灰）、动态取色、隐藏文件、排序、预览字号、解压位置、界面缩放 |
| 多语言 | 默认简体中文，`values-*` 扩展 |

---

## 快速开始

```bash
git clone https://github.com/styisme/watchfiles.git
cd WatchFiles
gradle :uikit:assembleRelease --no-daemon
gradle :app:assembleDebug --no-daemon
```

### RAR / 7z（可选）

`uikit` 本身零依赖。若需要 RAR/7z，在 `app/build.gradle` 中取消对应注释：

```groovy
implementation 'io.github.realburst:unrar5j:2.0.3'
implementation 'org.glavo.kala:kala-compress-archivers-sevenz:1.27.1-3'
```

不引入时，`ZipExtractor` 仅支持 ZIP，其余格式提示"当前构建不支持"。

---

## 兼容性

- `minSdk 10 / compileSdk 30 / targetSdk 30`
- 无 AndroidX、无强制第三方依赖
- 动态取色仅 API 31+ 可选开启，默认关闭
- Wear OS 4+ 对系统文件访问有限制，文件选择器入口做了 `ActivityNotFoundException` 兜底

## 关联文档

- [API_ZH.md](API_ZH.md) / [API_EN.md](API_EN.md)
- [CHANGELOG.md](CHANGELOG.md)
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [docs/AI_DISCLOSURE.md](docs/AI_DISCLOSURE.md)
- [docs/DEVELOPMENT_CONSTRAINTS.md](docs/DEVELOPMENT_CONSTRAINTS.md)

## 协议

[MIT License](LICENSE)
