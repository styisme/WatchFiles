package org.watchfiles.core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class UriReader {

    private UriReader() {}

    public static File materialize(Context ctx, Uri uri) throws Exception {
        if ("file".equals(uri.getScheme())) {
            return new File(uri.getPath());
        }
        String name = queryName(ctx, uri);
        if (name == null) name = "picked_" + System.currentTimeMillis();
        File dir = new File(ctx.getCacheDir(), "picked");
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, name);
        InputStream in = null;
        FileOutputStream os = null;
        try {
            in = ctx.getContentResolver().openInputStream(uri);
            if (in == null) throw new Exception("无法打开输入流");
            os = new FileOutputStream(out);
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
            os.flush();
        } finally {
            FileOps.closeQuietly(in);
            FileOps.closeQuietly(os);
        }
        return out;
    }

    private static String queryName(Context ctx, Uri uri) {
        Cursor c = null;
        try {
            c = ctx.getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return c.getString(idx);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }
}
