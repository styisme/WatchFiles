package org.uikit;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;
public class UikitButton extends TextView {
    public UikitButton(Context context) { super(context); init(); }
    public UikitButton(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    private void init() {
        setGravity(android.view.Gravity.CENTER);
        setTextSize(16);
        setClickable(true);
        setFocusable(true);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(UikitScreenUtils.dp2px(getContext(), 32));
        bg.setColor(UikitColor.getPrimaryColor(getContext()));
        setBackground(bg);
        setTextColor(UikitColor.getOnPrimaryColor(getContext()));
    }
}
