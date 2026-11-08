package org.uikit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class UikitUi {
    public interface OnItemSelectListener { void onItemSelect(int position); }
    public interface OnInputListener { void onInput(String text); }
    private UikitUi() {}

    public static Context wrapScale(Context base) {
        float scale = UikitConfig.getUiScale(base);
        if (scale == 1.0f) return base;
        if (Build.VERSION.SDK_INT < 17) return base;
        try {
            Configuration config = new Configuration();
            config.updateFrom(base.getResources().getConfiguration());
            int sysDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
            int targetDpi = (int)(sysDpi * scale);
            if (targetDpi < 80) targetDpi = 80;
            if (targetDpi > 960) targetDpi = 960;
            config.densityDpi = targetDpi;
            return base.createConfigurationContext(config);
        } catch (Exception e) { return base; }
    }
    public static GradientDrawable getPillBackground(Context ctx) {
        boolean isDark = UikitTheme.isDarkMode(ctx);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(32 * ctx.getResources().getDisplayMetrics().density);
        bg.setColor(isDark ? 0xFF1E1E1E : 0xFFFFFFFF);
        if (!isDark) bg.setStroke((int)(1 * ctx.getResources().getDisplayMetrics().density), 0xFFE0E0E0);
        return bg;
    }
    public static void applyRoundPadding(Context ctx, View root) {
        if (root == null) return;
        boolean isRound = WearHardwareUtils.isRoundScreen(root);
        boolean isChin  = WearHardwareUtils.needChinPadding(ctx);
        float d = ctx.getResources().getDisplayMetrics().density;
        int pL = isRound ? (int)(24*d) : (int)(12*d);
        int pR = isRound ? (int)(24*d) : (int)(12*d);
        int pB = isChin ? (int)(32*d) : 0;
        root.setPadding(pL, 0, pR, pB);
    }
    public static void setupWheelScroll(final ScrollView sv, final LinearLayout c) {
        if (sv == null || c == null) return;
        if (Build.VERSION.SDK_INT < 11) return;   // API 10 保护
        if (UikitConfig.isLowPerf(sv.getContext())) return;
        boolean isRound = WearHardwareUtils.isRoundScreen(sv);
        sv.setClipToPadding(false);
        sv.setVerticalScrollBarEnabled(false);
        if (!isRound) {
            for (int i = 0; i < c.getChildCount(); i++) {
                View ch = c.getChildAt(i);
                ch.setScaleX(1f); ch.setScaleY(1f); ch.setAlpha(1f);
            }
            return;
        }
        sv.post(new Runnable() {
            @Override public void run() {
                int h = sv.getHeight();
                if (h > 0) {
                    int pad = h/2 - dp(40, sv.getContext());
                    c.setPadding(c.getPaddingLeft(), pad, c.getPaddingRight(), pad);
                    sv.getViewTreeObserver().addOnScrollChangedListener(
                        new ViewTreeObserver.OnScrollChangedListener() {
                            @Override public void onScrollChanged() { applyWheelEffect(sv, c); }
                        });
                    applyWheelEffect(sv, c);
                }
            }
        });
    }
    private static void applyWheelEffect(ScrollView sv, LinearLayout c) {
        int centerY = sv.getHeight()/2;
        int scrollY = sv.getScrollY();
        for (int i = 0; i < c.getChildCount(); i++) {
            View ch = c.getChildAt(i);
            if (ch.getVisibility() != View.VISIBLE) continue;
            int cc = ch.getTop() + ch.getHeight()/2 - scrollY;
            float frac = Math.abs(centerY - cc) / (float) centerY;
            if (frac > 1f) frac = 1f;
            ch.setScaleX(1f - 0.25f*frac);
            ch.setScaleY(1f - 0.25f*frac);
            ch.setAlpha(1f - 0.7f*frac);
        }
    }
    public static void applyEntryAnimation(Activity a) {
        if (a == null || Build.VERSION.SDK_INT < 12) return;
        if (UikitConfig.isLowPerf(a)) return;
        a.overridePendingTransition(0, 0);
        final View root = a.findViewById(android.R.id.content);
        if (root == null) return;
        root.setScaleX(0.6f); root.setScaleY(0.6f); root.setAlpha(0f);
        root.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(250)
            .setInterpolator(new android.view.animation.DecelerateInterpolator()).start();
    }
    private static void setupWindow(Context ctx, Dialog d) {
        Window w = d.getWindow();
        if (w != null) {
            w.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(w.getAttributes());
            lp.width = (int)(ctx.getResources().getDisplayMetrics().widthPixels * 0.9);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            w.setAttributes(lp);
        }
    }
    private static void applyDialogStyle(Context ctx, Dialog d) {
        boolean dark = UikitTheme.isDarkMode(ctx);
        View root = d.findViewById(R.id.dlg_root);
        if (root != null) {
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(dark ? 0xFF1A1A1A : 0xFFFFFFFF);
            bg.setCornerRadius(36 * ctx.getResources().getDisplayMetrics().density);
            root.setBackground(bg);
        }
        TextView t = (TextView) d.findViewById(R.id.dlg_title);
        if (t != null) t.setTextColor(dark ? 0xFFFFFFFF : 0xFF1C1B1F);
    }
    public static Dialog createBaseDialog(Context ctx, String title, View content,
                                          String positive, final DialogInterface.OnClickListener posL,
                                          String negative, final DialogInterface.OnClickListener negL) {
        final Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_uikit_base);
        setupWindow(ctx, d);
        applyDialogStyle(ctx, d);
        ((TextView) d.findViewById(R.id.dlg_title)).setText(title);
        LinearLayout c = (LinearLayout) d.findViewById(R.id.dlg_content_container);
        if (content != null) c.addView(content);
        View bp = d.findViewById(R.id.dlg_btn_positive);
        View bn = d.findViewById(R.id.dlg_btn_negative);
        if (positive != null) {
            bp.setVisibility(View.VISIBLE);
            bp.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (posL != null) posL.onClick(d, 0); else d.dismiss();
                }
            });
        } else bp.setVisibility(View.GONE);
        if (negative != null) {
            bn.setVisibility(View.VISIBLE);
            bn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (negL != null) negL.onClick(d, 0); else d.dismiss();
                }
            });
        } else bn.setVisibility(View.GONE);
        return d;
    }
    public static Dialog showList(Context ctx, String title, String[] items, final OnItemSelectListener l) {
        final Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_uikit_list);
        setupWindow(ctx, d);
        applyDialogStyle(ctx, d);
        boolean isDark = UikitTheme.isDarkMode(ctx);
        ((TextView) d.findViewById(R.id.dlg_title)).setText(title);
        LinearLayout c = (LinearLayout) d.findViewById(R.id.dlg_list_container);
        c.removeAllViews();
        for (int i = 0; i < items.length; i++) {
            final int idx = i;
            TextView tv = new TextView(ctx);
            tv.setText(items[i]);
            tv.setTextSize(15);
            tv.setPadding(16, 16, 16, 16);
            tv.setTextColor(isDark ? 0xFFE0E0E0 : 0xFF333333);
            tv.setGravity(Gravity.CENTER);
            android.util.TypedValue t = new android.util.TypedValue();
            ctx.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, t, true);
            if (t.resourceId != 0) tv.setBackgroundResource(t.resourceId);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    d.dismiss();
                    if (l != null) l.onItemSelect(idx);
                }
            });
            c.addView(tv);
        }
        d.findViewById(R.id.dlg_btn_negative).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { d.dismiss(); }
        });
        return d;
    }
    public static Dialog showInput(Context ctx, String title, String def, final OnInputListener l) {
        final Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_uikit_input);
        setupWindow(ctx, d);
        applyDialogStyle(ctx, d);
        ((TextView) d.findViewById(R.id.dlg_title)).setText(title);
        final EditText et = (EditText) d.findViewById(R.id.dlg_input);
        et.setText(def);
        d.findViewById(R.id.dlg_btn_negative).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { d.dismiss(); }
        });
        d.findViewById(R.id.dlg_btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String s = et.getText().toString();
                d.dismiss();
                if (l != null) l.onInput(s);
            }
        });
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface x) {
                et.requestFocus();
                if (d.getWindow() != null)
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        return d;
    }
    public static Dialog showConfirm(Context ctx, String title, String msg, final DialogInterface.OnClickListener pos) {
        TextView tv = new TextView(ctx);
        tv.setText(msg);
        tv.setTextSize(14);
        tv.setTextColor(UikitTheme.isDarkMode(ctx) ? 0xFFE0E0E0 : 0xFF49454F);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 8, 16, 8);
        return createBaseDialog(ctx, title, tv,
                ctx.getString(R.string.uikit_ok), pos,
                ctx.getString(R.string.uikit_cancel), null);
    }
    private static float sStartX = 0, sStartY = 0;
    private static boolean sIsSwiping = false;
    public static boolean handleSwipeBack(final Activity a, android.view.MotionEvent ev) {
        if (a == null) return false;
        final View root = a.findViewById(android.R.id.content);
        final int w = a.getResources().getDisplayMetrics().widthPixels;
        if (root == null) return false;
        switch (ev.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN:
                sStartX = ev.getX(); sStartY = ev.getY(); sIsSwiping = false; break;
            case android.view.MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - sStartX;
                float dy = ev.getY() - sStartY;
                if (UikitConfig.isLowPerf(a)) {
                    if (Math.abs(dx) > w/4 && Math.abs(dx) > Math.abs(dy)*1.5) { a.finish(); return true; }
                    break;
                }
                if (!sIsSwiping && Math.abs(dx) > 40 && Math.abs(dx) > Math.abs(dy)*1.5) sIsSwiping = true;
                if (sIsSwiping) {
                    root.setTranslationX(dx);
                    float s = 1f - (Math.abs(dx)/(float)w)*0.25f;
                    root.setScaleX(s); root.setScaleY(s);
                    root.setAlpha(1f - (Math.abs(dx)/(float)w)*0.5f);
                    return true;
                }
                break;
            case android.view.MotionEvent.ACTION_UP:
            case android.view.MotionEvent.ACTION_CANCEL:
                if (sIsSwiping) {
                    sIsSwiping = false;
                    float fx = ev.getX() - sStartX;
                    if (Math.abs(fx) > w/4) {
                        if (Build.VERSION.SDK_INT >= 16) {
                            float tx = fx > 0 ? w : -w;
                            root.animate().translationX(tx).scaleX(0.5f).scaleY(0.5f).alpha(0f)
                                .setDuration(200).withEndAction(new Runnable() {
                                    @Override public void run() {
                                        a.finish();
                                        a.overridePendingTransition(0, 0);
                                    }
                                }).start();
                        } else { a.finish(); }
                    } else {
                        root.animate().translationX(0).scaleX(1f).scaleY(1f).alpha(1f).setDuration(200).start();
                    }
                    return true;
                }
                break;
        }
        return false;
    }
    public static boolean isSystemBackBlocked(Context ctx) {
        return UikitConfig.isSystemBackBlocked(ctx);
    }
    private static int dp(int v, Context ctx) {
        return (int)(v * ctx.getResources().getDisplayMetrics().density);
    }
}
