# WatchFiles API Guide

## File operations

- `FileOps.list(File dir, boolean showHidden)`
- `FileOps.copyRecursive(File src, File dst, CopyProgress cb)` — byte-level progress
- `FileOps.deleteRecursive(File f)`
- `FileOps.move(File src, File dst)`
- `FileOps.rename(File src, String newName)`
- `FileOps.uniqueTarget(File dir, String name)` — auto `(1)` suffix

## Sorting

- `FileSorter.sort(FileItem[] items, int mode, int order)`

## Extraction

- `ZipExtractor.extract(File archive, File destDir, Progress cb)` — dispatch by extension
- ZIP built-in with Zip Slip protection and GBK filename fix
- RAR/7z are optional; missing deps throw `UnsupportedOperationException`

## Preview

- `TextReader.read(File f)` — returns `status` and `text`

## System integration

- `UriReader.materialize(Context, Uri)` — materialize SAF Uri to temp file
- `ShareProvider.uriFor(File)` — `content://org.watchfiles.share/file?path=...`

## Settings

- `Prefs` / `UikitConfig`
