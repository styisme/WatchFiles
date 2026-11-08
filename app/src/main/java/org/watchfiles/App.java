package org.watchfiles;

import android.app.Application;
import org.uikit.UikitConfig;

import java.io.File;

public class App extends Application {

    public static class PendingOp {
        public final File src;
        public final boolean move;
        public PendingOp(File src, boolean move) { this.src = src; this.move = move; }
    }

    private static App sInstance;
    private PendingOp pending;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        UikitConfig.init("watchfiles_prefs");
        if (UikitConfig.getThemeColor(this) == 0) {
            UikitConfig.setThemeColor(this, 0xFF1976D2);
        }
    }

    public static App get() { return sInstance; }
    public PendingOp getPending() { return pending; }
    public void setPending(PendingOp p) { pending = p; }
}
