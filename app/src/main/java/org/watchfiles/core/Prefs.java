package org.watchfiles.core;

import android.content.Context;
import android.content.SharedPreferences;
import org.uikit.UikitConfig;

public final class Prefs {
    public static final String K_SORT      = "wf_sort_mode";
    public static final String K_ORDER     = "wf_sort_order";
    public static final String K_HIDDEN    = "wf_show_hidden";
    public static final String K_TEXT_SIZE = "wf_text_size";
    public static final String K_UNZIP     = "wf_unzip_mode";

    public static final int UNZIP_HERE = 0, UNZIP_SUBDIR = 1;

    private Prefs() {}

    public static SharedPreferences p(Context c) { return UikitConfig.prefs(c); }

    public static int getSort(Context c)  { return p(c).getInt(K_SORT, FileSorter.NAME); }
    public static void setSort(Context c, int v) { p(c).edit().putInt(K_SORT, v).commit(); }
    public static int getOrder(Context c) { return p(c).getInt(K_ORDER, FileSorter.ASC); }
    public static void setOrder(Context c, int v) { p(c).edit().putInt(K_ORDER, v).commit(); }
    public static boolean isHidden(Context c) { return p(c).getBoolean(K_HIDDEN, false); }
    public static void setHidden(Context c, boolean v) { p(c).edit().putBoolean(K_HIDDEN, v).commit(); }
    public static int getTextSize(Context c) { return p(c).getInt(K_TEXT_SIZE, 14); }
    public static void setTextSize(Context c, int v) { p(c).edit().putInt(K_TEXT_SIZE, v).commit(); }
    public static int getUnzipMode(Context c) { return p(c).getInt(K_UNZIP, UNZIP_SUBDIR); }
    public static void setUnzipMode(Context c, int v) { p(c).edit().putInt(K_UNZIP, v).commit(); }
}
