package org.watchfiles.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipExtractor {

    public interface Progress { void onProgress(int count, String current); }

    private ZipExtractor() {}

    public static int extract(File archive, File destDir, Progress cb) throws IOException {
        String name = archive.getName().toLowerCase(Locale.US);
        if (name.endsWith(".zip")) return ZipOnly.extract(archive, destDir, cb);
        if (name.endsWith(".rar")) return RarExtractor.extract(archive, destDir, cb);
        if (name.endsWith(".7z"))  return SevenZExtractor.extract(archive, destDir, cb);
        throw new IOException("不支持的格式: " + name);
    }

    public static String baseName(File f) {
        String n = f.getName();
        int i = n.lastIndexOf('.');
        return i > 0 ? n.substring(0, i) : n;
    }

    static final class ZipOnly {
        private ZipOnly() {}

        static int extract(File zip, File destDir, Progress cb) throws IOException {
            if (destDir == null) throw new IOException("dest null");
            String destCanonical = destDir.getCanonicalPath();
            int count = 0;
            ZipInputStream zin = null;
            try {
                zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zip)));
                ZipEntry e;
                byte[] buf = new byte[8192];
                while ((e = zin.getNextEntry()) != null) {
                    String rawName = e.getName();
                    String name = fixName(rawName);
                    File out = new File(destDir, name);
                    String outCanonical = out.getCanonicalPath();
                    if (!outCanonical.equals(destCanonical)
                            && !outCanonical.startsWith(destCanonical + File.separator)) {
                        throw new IOException("非法路径: " + name);
                    }
                    if (e.isDirectory()) {
                        out.mkdirs();
                    } else {
                        File parent = out.getParentFile();
                        if (parent != null && !parent.exists()) parent.mkdirs();
                        OutputStream os = null;
                        try {
                            os = new BufferedOutputStream(new FileOutputStream(out));
                            int n;
                            while ((n = zin.read(buf)) > 0) os.write(buf, 0, n);
                            os.flush();
                        } finally {
                            FileOps.closeQuietly(os);
                        }
                    }
                    count++;
                    if (cb != null) cb.onProgress(count, name);
                    zin.closeEntry();
                }
            } finally {
                FileOps.closeQuietly(zin);
            }
            return count;
        }

        private static String fixName(String name) {
            if (name == null) return "unnamed";
            if (name.indexOf('\uFFFD') >= 0) {
                try { return new String(name.getBytes("UTF-8"), "GBK"); }
                catch (Exception ignored) {}
            }
            return name;
        }
    }
}
