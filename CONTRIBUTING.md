# 贡献指南

**简体中文** | [English](CONTRIBUTING_EN.md)

感谢参与 **WatchFiles** 的开发！本项目采用 **Conventional Commits + SemVer 2.0.0 + CHANGELOG** 规范。

> 提交 PR 时请注明是否由 AI 生成，并确保已在真机验证。

---

## 本地开发

```bash
git clone https://github.com/styisme/watchfiles.git
cd WatchFiles
gradle :uikit:assembleRelease
gradle :app:assembleDebug
```

## 提交规范

```
<type>(<scope>): <subject>
```

常用 type：`feat` / `fix` / `docs` / `style` / `refactor` / `ci`。
常用 scope：`fs` / `ui` / `preview` / `zip` / `wear`。

**重要**：版本号以 `CHANGELOG.md` 为准，打 tag 时与 CHANGELOG 一致。

## 注意事项

- 提交前确认 `uikit/` 的 3 处改动（蓝色兜底、主题色键、API<11 保护）未被覆盖。
- 若新增解压格式，优先以反射方式接入可选依赖，保持 `uikit` 零依赖。
- 新增文案请同时写入 `res/values/strings.xml`，保持默认简中。

## 常见问题

**Q: 动态取色在 Android 12+ 取不到？**
A: 宿主 `<application>` 主题必须是 `Theme.DeviceDefault.NoActionBar`。

**Q: 底部弹窗无法关闭？**
A: `UikitBottomSheet` 已支持点击外部、点击 `Close`、下拉拖拽、返回键。

**Q: CI Release job 报 403？**
A: 仓库 Settings → Actions → General → Workflow permissions 选 **Read and write permissions**。
