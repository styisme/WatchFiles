# Contributing Guidelines

[简体中文](CONTRIBUTING.md) | **English**

Thank you for contributing to **WatchFiles**! Follow **Conventional Commits + SemVer 2.0.0 + CHANGELOG**.

---

## Local Development

```bash
gradle :uikit:assembleRelease
gradle :app:assembleDebug
```

## Commit Convention

```
<type>(<scope>): <subject>
```

Allowed types: `feat`, `fix`, `docs`, `style`, `refactor`, `ci`.
Common scopes: `fs`, `ui`, `preview`, `zip`, `wear`.

**Important**: Versioning relies on `CHANGELOG.md`. Tag with the same version.

## Notes

- Do not overwrite the three `uikit/` changes (blue fallback, theme color key, API<11 guard).
- New archive formats should be wired via reflection to keep `uikit` dependency-free.

## Troubleshooting

**Q: Dynamic color not working on Android 12+?**
A: The host `<application>` theme must be `Theme.DeviceDefault.NoActionBar`.

**Q: Bottom sheet cannot be dismissed?**
A: It supports clicking outside, tapping `Close`, dragging down, and the back button.

**Q: CI release job returns 403?**
A: Enable **Read and write permissions** under Settings → Actions → General.
