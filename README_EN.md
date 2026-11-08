[简体中文](README.md) | **English**

# WatchFiles

> A watch file manager built on [wear-uikit](https://github.com/styisme/wear-uikit).
> Pure Java dynamic rendering + XML resources. No AndroidX. Min Android 2.3 (API 10).

[![Build WatchFiles](https://github.com/styisme/watchfiles/actions/workflows/build.yml/badge.svg)](https://github.com/styisme/watchfiles/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Features

| Category | Details |
|---|---|
| File management | Browse, sort (name/size/time/type), hidden files, new folder, rename, copy, cut, paste, delete, copy path, share |
| Preview | Text (UTF-8/GBK auto-detect, binary detection, 256KB truncation), image (sampled decode, pinch zoom, drag) |
| Extraction | ZIP built-in; RAR / 7z as optional dependencies |
| System integration | System file picker (API 19+ SAF, fallback GET_CONTENT), custom ContentProvider for sharing |
| Settings | Theme tri-state, accent color, dynamic color, hidden files, sort, text size, unzip location, UI scale |
| i18n | Simplified Chinese by default, extend with `values-*` |

---

## Quick Start

```bash
gradle :uikit:assembleRelease --no-daemon
gradle :app:assembleDebug --no-daemon
```

### RAR / 7z (optional)

```groovy
implementation 'io.github.realburst:unrar5j:2.0.3'
implementation 'org.glavo.kala:kala-compress-archivers-sevenz:1.27.1-3'
```

Without these, `ZipExtractor` only handles ZIP.

---

## License

[MIT License](LICENSE)
