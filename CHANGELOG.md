# Changelog

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，版本号遵循 [Semantic Versioning](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### Planned
- 文件压缩（zip 打包）。
- 收藏夹与最近访问。

---

## [1.1.0] - 2026-11-08

### Added
- RAR / 7z 可选解压（`unrar5j` + `kala-compress`，app 层可选依赖，反射接入）。
- 带字节级进度的复制/移动。
- 系统文件选择器（API 19+ SAF，低版本回退 GET_CONTENT）。
- 自写 `ShareProvider`，支持分享文件而不依赖 AndroidX FileProvider。
- 主题色预设（蓝/靛/紫/橙/青/蓝灰），默认蓝色。
- 多语言资源结构（默认简中，`values-*` 可扩展）。

### Changed
- 动态取色默认关闭，避免 API 31+ 取到系统绿色。
- `ZipExtractor` 改为按扩展名分派，ZIP 保持零依赖。

### Fixed
- API 10 下 3D 滚轮 `setScaleX` 兼容保护。

---

## [1.0.0] - 2026-11-08

首个版本。基础文件浏览、ZIP 解压、文本/图片预览、设置。

---

## 版本对比链接

[Unreleased]: https://github.com/styisme/watchfiles/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/styisme/watchfiles/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/styisme/watchfiles/releases/tag/v1.0.0
