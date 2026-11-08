# WatchFiles API 指南

## 文件操作

- `FileOps.list(File dir, boolean showHidden)` — 列出目录内容
- `FileOps.copyRecursive(File src, File dst, CopyProgress cb)` — 带字节级进度的递归复制
- `FileOps.deleteRecursive(File f)` — 递归删除
- `FileOps.move(File src, File dst)` — 移动（rename 失败时回退复制+删除）
- `FileOps.rename(File src, String newName)` — 重命名
- `FileOps.uniqueTarget(File dir, String name)` — 自动加 `(1)` 防重名
- `FileOps.humanSize(long b)` — 人类可读大小

## 排序

- `FileSorter.sort(FileItem[] items, int mode, int order)`
- `mode`: `NAME` / `SIZE` / `TIME` / `TYPE`
- `order`: `ASC` / `DESC`

## 解压

- `ZipExtractor.extract(File archive, File destDir, Progress cb)` — 按扩展名分派 zip/rar/7z
- ZIP 内置 Zip Slip 防护与 GBK 文件名修复
- RAR/7z 依赖可选，未引入时抛 `UnsupportedOperationException`

## 预览

- `TextReader.read(File f)` — 返回 `status`（`OK` / `BINARY` / `TOO_LARGE`）与 `text`

## 系统集成

- `UriReader.materialize(Context, Uri)` — 把 SAF `content://` 落盘为临时文件
- `ShareProvider.uriFor(File)` — 生成 `content://org.watchfiles.share/file?path=...`
- `MainActivity.pickFromSystem()` — API 19+ `ACTION_OPEN_DOCUMENT`，低版本 `ACTION_GET_CONTENT`

## 设置

- `Prefs`：排序、隐藏文件、预览字号、解压位置
- `UikitConfig`：主题模式、主题色、动态取色、界面缩放（来自内嵌 uikit）
