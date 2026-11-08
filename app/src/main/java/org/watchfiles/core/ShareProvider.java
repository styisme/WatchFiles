package org.watchfiles.core;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

public class ShareProvider extends ContentProvider {

    public static final String AUTHORITY = "org.watchfiles.share";

    public static Uri uriFor(File f) {
        return Uri.parse("content://" + AUTHORITY + "/file?path="
            + Uri.encode(f.getAbsolutePath()));
    }

    @Override
    public boolean onCreate() { return true; }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        if (!"r".equals(mode)) throw new FileNotFoundException("只读");
        String path = uri.getQueryParameter("path");
        if (path == null) throw new FileNotFoundException("缺少 path");
        File f = new File(path);
        if (!f.exists() || !f.isFile()) throw new FileNotFoundException(path);
        return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override public Cursor query(Uri u, String[] p, String s, String[] a, String o) { return null; }
    @Override public String getType(Uri uri) { return "application/octet-stream"; }
    @Override public Uri insert(Uri uri, ContentValues v) { return null; }
    @Override public int delete(Uri uri, String s, String[] a) { return 0; }
    @Override public int update(Uri uri, ContentValues v, String s, String[] a) { return 0; }
}
