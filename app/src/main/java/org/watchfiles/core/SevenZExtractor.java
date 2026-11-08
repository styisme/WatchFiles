package org.watchfiles.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 7z 解压，依赖 kala-compress。通过反射调用，保证未引入依赖时仍可编译。
 */
public final class SevenZExtractor {

    private SevenZExtractor() {}

    public static int extract(File sz, File destDir, ZipExtractor.Progress cb)
            throws IOException {
        try {
            Class<?> archive = Class.forName("kala.compress.archivers.sevenz.SevenZFile");
            Object sevenZ = archive.getConstructor(File.class).newInstance(sz);
            Class<?> entryCls = Class.forName("kala.compress.archivers.sevenz.SevenZArchiveEntry");
            java.lang.reflect.Method getNext = archive.getMethod("getNextEntry");
            java.lang.reflect.Method getStream = archive.getMethod("getInputStream", entryCls);
            java.lang.reflect.Method isDir = entryCls.getMethod("isDirectory");
            java.lang.reflect.Method getName = entryCls.getMethod("getName");
            java.lang.reflect.Method close = archive.getMethod("close");

            int count = 0;
            Object entry;
            while ((entry = getNext.invoke(sevenZ)) != null) {
                String name = (String) getName.invoke(entry);
                File out = new File(destDir, name);
                if ((Boolean) isDir.invoke(entry)) {
                    out.mkdirs();
                } else {
                    File parent = out.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();
                    InputStream in = (InputStream) getStream.invoke(sevenZ, entry);
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
                    byte[] buf = new byte[8192];
                    int n;
                    try {
                        while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
                        os.flush();
                    } finally {
                        FileOps.closeQuietly(os);
                        FileOps.closeQuietly(in);
                    }
                }
                count++;
                if (cb != null) cb.onProgress(count, name);
            }
            try { close.invoke(sevenZ); } catch (Exception ignored) {}
            return count;
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                "当前构建未包含 7z 支持，请在 build.gradle 中引入 kala-compress");
        } catch (Exception e) {
            throw new IOException("7z 解压失败: " + e.getMessage());
        }
    }
}
