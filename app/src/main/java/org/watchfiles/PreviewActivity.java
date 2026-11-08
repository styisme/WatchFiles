package org.watchfiles;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.uikit.*;
import org.watchfiles.core.Prefs;
import org.watchfiles.core.TextReader;

import java.io.File;

public class PreviewActivity extends Activity {

    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_TYPE = "type";

    private Bitmap bitmap;
    private final Matrix matrix = new Matrix();
    private float lastX, lastY;
    private ScaleGestureDetector scaleDetector;
    private ImageView imageView;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(UikitUi.wrapScale(base));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        UikitTheme.applyTheme(this);
        UikitUi.applyEntryAnimation(this);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        if (path == null) { finish(); return; }
        File f = new File(path);
        if (!f.exists()) { finish(); return; }

        boolean dark = UikitTheme.isDarkMode(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(dark ? 0xFF000000 : 0xFFF4F6F8);
        setContentView(root);
        UikitUi.applyRoundPadding(this, root);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8), dp(8), dp(8), dp(4));

        TextView title = new TextView(this);
        title.setText(f.getName());
        title.setTextSize(13);
        title.setSingleLine(true);
        title.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);
        title.setTextColor(UikitColor.getAccentColor(this, dark));
        title.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(title);

        UikitButton btnClose = new UikitButton(this);
        btnClose.setText(getString(R.string.close));
        btnClose.setTextSize(12);
        header.addView(btnClose, new LinearLayout.LayoutParams(dp(72), dp(36)));
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { finish(); }
        });

        root.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if ("image".equals(type)) {
            buildImage(root, f, dark);
        } else {
            buildText(root, f, dark);
        }
    }

    private void buildText(LinearLayout root, File f, boolean dark) {
        TextReader.Result r = TextReader.read(f);

        ScrollView sv = new ScrollView(this);
        sv.setVerticalScrollBarEnabled(false);
        sv.setClipToPadding(false);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(dp(16), dp(8), dp(16), dp(24));

        if (r.status == TextReader.BINARY) {
            TextView tv = new TextView(this);
            tv.setText(getString(R.string.preview_binary));
            tv.setTextSize(14);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(dark ? 0xFFAAAAAA : 0xFF666666);
            tv.setPadding(0, dp(32), 0, 0);
            col.addView(tv);
        } else {
            if (r.status == TextReader.TOO_LARGE) {
                TextView hint = new TextView(this);
                hint.setText(getString(R.string.preview_truncated));
                hint.setTextSize(11);
                hint.setTextColor(dark ? 0xFFAAAAAA : 0xFF666666);
                hint.setPadding(0, 0, 0, dp(8));
                col.addView(hint);
            }
            TextView tv = new TextView(this);
            tv.setTypeface(Typeface.MONOSPACE);
            tv.setTextSize(Prefs.getTextSize(this));
            tv.setTextColor(dark ? 0xFFE0E0E0 : 0xFF1C1B1F);
            tv.setText(r.text == null ? "" : r.text);
            col.addView(tv);
        }

        sv.addView(col, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(sv, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
    }

    private void buildImage(LinearLayout root, File f, boolean dark) {
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setBackgroundColor(dark ? 0xFF000000 : 0xFFFFFFFF);
        root.addView(imageView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        int vw = getResources().getDisplayMetrics().widthPixels;
        int vh = getResources().getDisplayMetrics().heightPixels;
        bitmap = decode(f, Math.max(vw, 1), Math.max(vh, 1));
        if (bitmap == null) {
            UikitToast.show(this, getString(R.string.preview_error));
            finish();
            return;
        }
        imageView.setImageBitmap(bitmap);

        scaleDetector = new ScaleGestureDetector(this,
            new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector d) {
                    float factor = d.getScaleFactor();
                    matrix.postScale(factor, factor, d.getFocusX(), d.getFocusY());
                    imageView.setImageMatrix(matrix);
                    return true;
                }
            });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                scaleDetector.onTouchEvent(e);
                if (e.getPointerCount() == 1) {
                    int action = e.getActionMasked();
                    if (action == MotionEvent.ACTION_DOWN) {
                        lastX = e.getX();
                        lastY = e.getY();
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        matrix.postTranslate(e.getX() - lastX, e.getY() - lastY);
                        imageView.setImageMatrix(matrix);
                        lastX = e.getX();
                        lastY = e.getY();
                    }
                }
                return true;
            }
        });

        imageView.post(new Runnable() {
            public void run() {
                int w = imageView.getWidth(), h = imageView.getHeight();
                if (w == 0 || h == 0 || bitmap == null) return;
                float s = Math.min((float) w / bitmap.getWidth(), (float) h / bitmap.getHeight());
                matrix.setScale(s, s);
                matrix.postTranslate((w - bitmap.getWidth() * s) / 2f,
                                     (h - bitmap.getHeight() * s) / 2f);
                imageView.setImageMatrix(matrix);
            }
        });
    }

    private Bitmap decode(File f, int reqW, int reqH) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), o);
            if (o.outWidth <= 0 || o.outHeight <= 0) return null;

            int sample = 1;
            while (o.outWidth / (sample * 2) >= reqW && o.outHeight / (sample * 2) >= reqH) {
                sample *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = sample;
            return BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            try { bitmap.recycle(); } catch (Exception ignored) {}
            bitmap = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
