package org.watchfiles.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class TextReader {
    public static final int MAX_BYTES = 256 * 1024;
    public static final int BINARY = 0, OK = 1, TOO_LARGE = 2;

    public static class Result {
        public int status;
        public String text;
        public long total;
    }

    private TextReader() {}

    public static Result read(File f) {
        Result r = new Result();
        r.total = f.length();
        int cap = (int) Math.min(f.length(), MAX_BYTES);
        if (cap <= 0) { r.status = OK; r.text = ""; return r; }
        byte[] buf = new byte[cap];
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int off = 0;
            while (off < cap) {
                int n = in.read(buf, off, cap - off);
                if (n <= 0) break;
                off += n;
            }
            int probe = Math.min(off, 8192);
            for (int i = 0; i < probe; i++) {
                if (buf[i] == 0) { r.status = BINARY; return r; }
            }
            String s = new String(buf, 0, off, "UTF-8");
            if (s.indexOf('\uFFFD') >= 0) {
                try { s = new String(buf, 0, off, "GBK"); } catch (Exception ignored) {}
            }
            r.text = s;
            r.status = f.length() > MAX_BYTES ? TOO_LARGE : OK;
        } catch (IOException e) {
            r.status = BINARY;
        } finally {
            FileOps.closeQuietly(in);
        }
        return r;
    }
}
