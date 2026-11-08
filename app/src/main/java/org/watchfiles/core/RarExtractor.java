package org.watchfiles.core;

import java.io.File;
import java.io.IOException;

/**
 * RAR 解压，依赖 unrar5j。通过反射调用，保证未引入依赖时仍可编译。
 */
public final class RarExtractor {

    private RarExtractor() {}

    public static int extract(File rar, File destDir, ZipExtractor.Progress cb)
            throws IOException {
        try {
            Class<?> unrar = Class.forName("be.stef.rar.Unrar5j");
            java.lang.reflect.Method m = unrar.getMethod(
                "extract", String.class, String.class, String.class);
            Object result = m.invoke(null, rar.getAbsolutePath(), destDir.getAbsolutePath(), null);
            int n = 0;
            if (result != null) {
                try {
                    java.lang.reflect.Field f = result.getClass().getField("successCount");
                    n = f.getInt(result);
                } catch (Exception ignored) {}
            }
            if (cb != null) cb.onProgress(n, rar.getName());
            return n;
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                "当前构建未包含 RAR 支持，请在 build.gradle 中引入 unrar5j");
        } catch (Exception e) {
            throw new IOException("RAR 解压失败: " + e.getMessage());
        }
    }
}
