# 开发环境约束

- 仅云端 CI 编译（GitHub Actions + JDK 8 + Gradle 5.6.4）
- 仅移动端开发（Pydroid + MGit），无本地 IDE
- `minSdk 10` 下无法使用 AndroidX，文件分享必须自写 ContentProvider
- RAR/7z 通过 app 层可选依赖实现；若宿主要求零依赖，注释依赖后仅保留 ZIP
