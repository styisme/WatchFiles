package org.uikit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UikitBottomSheet extends Dialog {

    private LinearLayout root;
    private float startY;
    private boolean isDragging = false;

    public UikitBottomSheet(Context ctx, String title, View content) {
        super(ctx);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);

        boolean dark = UikitTheme.isDarkMode(ctx);

        FrameLayout wrapper = new FrameLayout(ctx);
        wrapper.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);

        int padH = UikitScreenUtils.dp2px(ctx, 24);
        int padTop = UikitScreenUtils.dp2px(ctx, 16);
        int padBottom = UikitScreenUtils.dp2px(ctx, 64);
        root.setPadding(padH, padTop, padH, padBottom);

        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(dark ? 0xFF1E1E1E : 0xFFFFFFFF);
        bg.setCornerRadii(new float[]{48, 48, 48, 48, 0, 0, 0, 0});
        root.setBackground(bg);

        View handle = new View(ctx);
        LinearLayout.LayoutParams handleLp = new LinearLayout.LayoutParams(
                UikitScreenUtils.dp2px(ctx, 32), UikitScreenUtils.dp2px(ctx, 4));
        handleLp.gravity = Gravity.CENTER_HORIZONTAL;
        handleLp.bottomMargin = UikitScreenUtils.dp2px(ctx, 16);
        handle.setLayoutParams(handleLp);

        android.graphics.drawable.GradientDrawable hBg = new android.graphics.drawable.GradientDrawable();
        hBg.setCornerRadius(UikitScreenUtils.dp2px(ctx, 2));
        hBg.setColor(dark ? 0xFF555555 : 0xFFDDDDDD);
        handle.setBackground(hBg);
        root.addView(handle);

        if (title != null) {
            TextView tvTitle = new TextView(ctx);
            tvTitle.setText(title);
            tvTitle.setTextSize(18);
            tvTitle.setTextColor(dark ? 0xFFE0E0E0 : 0xFF1C1B1F);
            tvTitle.setGravity(Gravity.CENTER);
            tvTitle.setPadding(0, 0, 0, 16);
            root.addView(tvTitle);
        }

        if (content != null) {
            root.addView(content);
        }

        UikitButton btnClose = new UikitButton(ctx);
        btnClose.setText("Close");
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, UikitScreenUtils.dp2px(ctx, 48));
        btnLp.topMargin = UikitScreenUtils.dp2px(ctx, 24);
        btnClose.setLayoutParams(btnLp);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { dismiss(); }
        });
        root.addView(btnClose);

        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        rootLp.gravity = Gravity.BOTTOM;
        root.setLayoutParams(rootLp);
        wrapper.addView(root);

        setContentView(wrapper);

        Window w = getWindow();
        if (w != null) {
            w.setBackgroundDrawableResource(android.R.color.transparent);
            w.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(w.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            w.setAttributes(lp);
        }

        View.OnTouchListener dragListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getRawY();
                        isDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getRawY() - startY;
                        if (deltaY > 0) {
                            isDragging = true;
                            root.setTranslationY(deltaY);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        float finalDeltaY = event.getRawY() - startY;
                        if (isDragging) {
                            if (finalDeltaY > UikitScreenUtils.dp2px(v.getContext(), 60)) {
                                dismiss();
                            } else {
                                root.animate().translationY(0).setDuration(200).start();
                            }
                        }
                        return true;
                }
                return false;
            }
        };

        handle.setOnTouchListener(dragListener);
        if (title != null) {
            for (int i = 0; i < root.getChildCount(); i++) {
                if (root.getChildAt(i) instanceof TextView) {
                    root.getChildAt(i).setOnTouchListener(dragListener);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
