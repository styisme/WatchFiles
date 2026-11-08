package org.watchfiles.core;

import java.io.File;
import java.util.Locale;

public class FileItem {
    public static final int TYPE_DIR = 0, TYPE_TEXT = 1, TYPE_IMAGE = 2, TYPE_ZIP = 3, TYPE_OTHER = 4;

    public final File file;
    public final String name;
    public final boolean dir;
    public final long size;
    public final long modified;
    public final String lower;

    public FileItem(File f) {
        file = f;
        name = f.getName();
        dir = f.isDirectory();
        size = dir ? 0 : f.length();
        modified = f.lastModified();
        lower = name.toLowerCase(Locale.US);
    }

    public String ext() {
        int i = lower.lastIndexOf('.');
        return i >= 0 ? lower.substring(i + 1) : "";
    }

    public int type() {
        if (dir) return TYPE_DIR;
        String e = ext();
        if ("zip".equals(e) || "rar".equals(e) || "7z".equals(e)) return TYPE_ZIP;
        if (isText(e)) return TYPE_TEXT;
        if (isImage(e)) return TYPE_IMAGE;
        return TYPE_OTHER;
    }

    public static boolean isText(String e) {
        return "txt".equals(e) || "log".equals(e) || "md".equals(e) || "json".equals(e)
            || "xml".equals(e) || "ini".equals(e) || "cfg".equals(e) || "csv".equals(e)
            || "java".equals(e) || "js".equals(e) || "css".equals(e) || "html".equals(e)
            || "htm".equals(e) || "properties".equals(e) || "yml".equals(e) || "yaml".equals(e)
            || "sh".equals(e) || "bat".equals(e) || "conf".equals(e) || "gradle".equals(e)
            || "pro".equals(e) || "kt".equals(e) || "c".equals(e) || "h".equals(e) || "cpp".equals(e);
    }

    public static boolean isImage(String e) {
        return "png".equals(e) || "jpg".equals(e) || "jpeg".equals(e)
            || "gif".equals(e) || "bmp".equals(e) || "webp".equals(e);
    }
}
