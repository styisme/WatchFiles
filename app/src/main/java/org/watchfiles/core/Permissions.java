package org.watchfiles.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public final class Permissions {
    public static final int REQ_STORAGE = 1001;

    private Permissions() {}

    public static boolean hasStorage(Context c) {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (Build.VERSION.SDK_INT >= 30) return Environment.isExternalStorageManager();
        return c.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == 0
            && c.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0;
    }

    public static void request(Activity a) {
        if (Build.VERSION.SDK_INT < 23) return;
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                i.setData(Uri.parse("package:" + a.getPackageName()));
                a.startActivity(i);
            } catch (Exception e) {
                try {
                    a.startActivity(new Intent(
                        android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                } catch (Exception ignored) {}
            }
            return;
        }
        a.requestPermissions(new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQ_STORAGE);
    }

    public static File root(Context c) {
        File ext = null;
        try { ext = Environment.getExternalStorageDirectory(); } catch (Exception ignored) {}
        if (ext != null && ext.exists() && hasStorage(c)) return ext;
        File fb = c.getExternalFilesDir(null);
        if (fb == null) fb = c.getFilesDir();
        return fb;
    }
}
