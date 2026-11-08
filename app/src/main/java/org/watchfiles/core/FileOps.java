package org.watchfiles.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FileOps {

    public interface CopyProgress {
        void onProgress(long copied, long total, String currentFile);
    }

    private FileOps() {}

    public static List<FileItem> list(File dir, boolean showHidden) {
        List<FileItem> out = new ArrayList<FileItem>();
        File[] fs = dir == null ? null : dir.listFiles();
        if (fs == null) return out;
        for (File f : fs) {
            if (!showHidden && f.getName().startsWith(".")) continue;
            out.add(new FileItem(f));
        }
        return out;
    }

    public static boolean deleteRecursive(File f) {
        if (f == null || !f.exists()) return false;
        if (f.isDirectory()) {
            File[] cs = f.listFiles();
            if (cs != null) for (File c : cs) deleteRecursive(c);
        }
        return f.delete();
    }

    public static boolean rename(File src, String newName) {
        if (src == null || newName == null) return false;
        newName = newName.trim();
        if (newName.length() == 0 || newName.contains("/")) return false;
        File dst = new File(src.getParentFile(), newName);
        return !dst.exists() && src.renameTo(dst);
    }

    public static File uniqueTarget(File dir, String name) {
        File f = new File(dir, name);
        if (!f.exists()) return f;
        String base = name, ext = "";
        int i = name.lastIndexOf('.');
        if (i > 0) { base = name.substring(0, i); ext = name.substring(i); }
        for (int n = 1; n < 10000; n++) {
            f = new File(dir, base + " (" + n + ")" + ext);
            if (!f.exists()) return f;
        }
        return new File(dir, base + "-" + System.currentTimeMillis() + ext);
    }

    public static boolean copyRecursive(File src, File dst) throws IOException {
        return copyRecursive(src, dst, null);
    }

    public static boolean copyRecursive(File src, File dst, CopyProgress cb) throws IOException {
        long total = sizeOf(src);
        long[] done = new long[]{0};
        return copyInternal(src, dst, cb, done, total);
    }

    private static boolean copyInternal(File src, File dst, CopyProgress cb,
                                        long[] done, long total) throws IOException {
        if (src.isDirectory()) {
            if (!dst.exists() && !dst.mkdirs()) return false;
            File[] cs = src.listFiles();
            if (cs != null) for (File c : cs) {
                if (!copyInternal(c, new File(dst, c.getName()), cb, done, total)) return false;
            }
            return true;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(src));
            out = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
                done[0] += n;
                if (cb != null) cb.onProgress(done[0], total, src.getName());
            }
            out.flush();
            return true;
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    public static long sizeOf(File f) {
        if (f == null || !f.exists()) return 0;
        if (f.isFile()) return f.length();
        long s = 0;
        File[] cs = f.listFiles();
        if (cs != null) for (File c : cs) s += sizeOf(c);
        return s;
    }

    public static boolean move(File src, File dst) {
        if (src == null || dst == null) return false;
        if (src.renameTo(dst)) return true;
        try {
            if (copyRecursive(src, dst)) { deleteRecursive(src); return true; }
        } catch (IOException ignored) {}
        return false;
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) try { c.close(); } catch (IOException ignored) {}
    }

    public static String humanSize(long b) {
        if (b < 1024) return b + " B";
        double k = b / 1024.0;
        if (k < 1024) return String.format(Locale.US, "%.1f KB", k);
        double m = k / 1024.0;
        if (m < 1024) return String.format(Locale.US, "%.1f MB", m);
        return String.format(Locale.US, "%.2f GB", m / 1024.0);
    }
}
