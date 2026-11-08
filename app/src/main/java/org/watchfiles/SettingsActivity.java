package org.watchfiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.uikit.*;
import org.watchfiles.core.FileSorter;
import org.watchfiles.core.Prefs;

public class SettingsActivity extends Activity {

    private static final int[] ACCENTS = new int[]{
        0xFF1976D2, 0xFF3F51B5, 0xFF7B1FA2, 0xFFE64A19, 0xFF00838F, 0xFF455A64
    };

    private LinearLayout listBox;
    private ScrollView scroll;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(UikitUi.wrapScale(base));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        UikitTheme.applyTheme(this);
        UikitUi.applyEntryAnimation(this);

        boolean dark = UikitTheme.isDarkMode(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(dark ? 0xFF000000 : 0xFFF4F6F8);
        setContentView(root);
        UikitUi.applyRoundPadding(this, root);

        TextView title = new TextView(this);
        title.setText(getString(R.string.settings));
        title.setTextSize(16);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(UikitColor.getAccentColor(this, dark));
        title.setPadding(0, dp(12), 0, dp(8));
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipToPadding(false);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        listBox.setPadding(dp(8), dp(4), dp(8), dp(24));
        scroll.addView(listBox, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        buildItems();
    }

    private void buildItems() {
        addTextRow(getString(R.string.settings_theme), themeLabel(), new View.OnClickListener() {
            public void onClick(View v) { chooseTheme(); }
        });
        addTextRow(getString(R.string.settings_accent), accentLabel(), new View.OnClickListener() {
            public void onClick(View v) { chooseAccent(); }
        });
        if (Build.VERSION.SDK_INT >= 31) {
            addSwitchRow(getString(R.string.settings_dynamic),
                UikitConfig.isDynamicColorEnabled(this),
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton b, boolean c) {
                        UikitConfig.setDynamicColorEnabled(SettingsActivity.this, c);
                        restart();
                    }
                });
        }
        addSwitchRow(getString(R.string.settings_hidden), Prefs.isHidden(this),
            new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton b, boolean c) {
                    Prefs.setHidden(SettingsActivity.this, c);
                }
            });
        addTextRow(getString(R.string.sort), sortLabel(), new View.OnClickListener() {
            public void onClick(View v) { chooseSort(); }
        });
        addTextRow(getString(R.string.sort_order), orderLabel(), new View.OnClickListener() {
            public void onClick(View v) { chooseOrder(); }
        });
        addTextRow(getString(R.string.settings_text_size),
            Prefs.getTextSize(this) + " sp", new View.OnClickListener() {
                public void onClick(View v) { chooseTextSize(); }
            });
        addTextRow(getString(R.string.settings_unzip), unzipLabel(), new View.OnClickListener() {
            public void onClick(View v) { chooseUnzip(); }
        });
        addTextRow(getString(R.string.settings_scale),
            UikitConfig.getUiScale(this) + "x", new View.OnClickListener() {
                public void onClick(View v) { chooseScale(); }
            });
        addTextRow(getString(R.string.about), "v1.1.0", new View.OnClickListener() {
            public void onClick(View v) {
                UikitUi.showConfirm(SettingsActivity.this, getString(R.string.about),
                    getString(R.string.about_text), null).show();
            }
        });
    }

    private void addTextRow(String title, String value, View.OnClickListener l) {
        LinearLayout row = makeRow();
        TextView tv = makeTitle(title);
        row.addView(tv, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView val = new TextView(this);
        val.setText(value);
        val.setTextSize(13);
        val.setTextColor(UikitColor.getPrimaryColor(this));
        row.addView(val);
        row.setOnClickListener(l);
        listBox.addView(row);
    }

    private void addSwitchRow(String title, boolean checked,
                              CompoundButton.OnCheckedChangeListener l) {
        LinearLayout row = makeRow();
        TextView tv = makeTitle(title);
        row.addView(tv, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        UikitSwitch sw = new UikitSwitch(this);
        sw.setChecked(checked);
        row.addView(sw, new LinearLayout.LayoutParams(dp(44), dp(26)));
        sw.setOnCheckedChangeListener(l);
        listBox.addView(row);
    }

    private LinearLayout makeRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(12), dp(16), dp(12));
        row.setBackground(UikitUi.getPillBackground(this));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(8);
        row.setLayoutParams(lp);
        return row;
    }

    private TextView makeTitle(String s) {
        TextView tv = new TextView(this);
        tv.setText(s);
        tv.setTextSize(14);
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTextColor(UikitTheme.isDarkMode(this) ? 0xFFE0E0E0 : 0xFF1C1B1F);
        return tv;
    }

    private String themeLabel() {
        String m = UikitConfig.getThemeMode(this);
        if (UikitConfig.THEME_DARK.equals(m))  return getString(R.string.theme_dark);
        if (UikitConfig.THEME_LIGHT.equals(m)) return getString(R.string.theme_light);
        return getString(R.string.theme_auto);
    }

    private String accentLabel() {
        int c = UikitConfig.getThemeColor(this);
        if (c == ACCENTS[1]) return getString(R.string.accent_indigo);
        if (c == ACCENTS[2]) return getString(R.string.accent_purple);
        if (c == ACCENTS[3]) return getString(R.string.accent_orange);
        if (c == ACCENTS[4]) return getString(R.string.accent_cyan);
        if (c == ACCENTS[5]) return getString(R.string.accent_bluegrey);
        return getString(R.string.accent_blue);
    }

    private String sortLabel() {
        switch (Prefs.getSort(this)) {
            case FileSorter.SIZE: return getString(R.string.sort_size);
            case FileSorter.TIME: return getString(R.string.sort_time);
            case FileSorter.TYPE: return getString(R.string.sort_type);
            default: return getString(R.string.sort_name);
        }
    }

    private String orderLabel() {
        return Prefs.getOrder(this) == FileSorter.DESC
            ? getString(R.string.sort_desc) : getString(R.string.sort_asc);
    }

    private String unzipLabel() {
        return Prefs.getUnzipMode(this) == Prefs.UNZIP_HERE
            ? getString(R.string.unzip_here) : getString(R.string.unzip_subdir);
    }

    private void chooseTheme() {
        final String[] items = new String[]{
            getString(R.string.theme_auto),
            getString(R.string.theme_light),
            getString(R.string.theme_dark)
        };
        UikitUi.showList(this, getString(R.string.settings_theme), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    String mode = p == 1 ? UikitConfig.THEME_LIGHT
                                : p == 2 ? UikitConfig.THEME_DARK
                                : UikitConfig.THEME_AUTO;
                    UikitConfig.setThemeMode(SettingsActivity.this, mode);
                    restart();
                }
            }).show();
    }

    private void chooseAccent() {
        final String[] items = new String[]{
            getString(R.string.accent_blue), getString(R.string.accent_indigo),
            getString(R.string.accent_purple), getString(R.string.accent_orange),
            getString(R.string.accent_cyan), getString(R.string.accent_bluegrey)
        };
        UikitUi.showList(this, getString(R.string.settings_accent), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    UikitConfig.setThemeColor(SettingsActivity.this, ACCENTS[p]);
                    restart();
                }
            }).show();
    }

    private void chooseSort() {
        final String[] items = new String[]{
            getString(R.string.sort_name), getString(R.string.sort_size),
            getString(R.string.sort_time), getString(R.string.sort_type)
        };
        UikitUi.showList(this, getString(R.string.sort), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    Prefs.setSort(SettingsActivity.this, p);
                    restart();
                }
            }).show();
    }

    private void chooseOrder() {
        final String[] items = new String[]{
            getString(R.string.sort_asc), getString(R.string.sort_desc)
        };
        UikitUi.showList(this, getString(R.string.sort_order), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    Prefs.setOrder(SettingsActivity.this, p == 1 ? FileSorter.DESC : FileSorter.ASC);
                    restart();
                }
            }).show();
    }

    private void chooseTextSize() {
        final String[] items = new String[]{"12 sp", "14 sp", "16 sp", "18 sp", "20 sp"};
        final int[] values = new int[]{12, 14, 16, 18, 20};
        UikitUi.showList(this, getString(R.string.settings_text_size), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    Prefs.setTextSize(SettingsActivity.this, values[p]);
                    restart();
                }
            }).show();
    }

    private void chooseUnzip() {
        final String[] items = new String[]{
            getString(R.string.unzip_here), getString(R.string.unzip_subdir)
        };
        UikitUi.showList(this, getString(R.string.settings_unzip), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    Prefs.setUnzipMode(SettingsActivity.this,
                        p == 1 ? Prefs.UNZIP_SUBDIR : Prefs.UNZIP_HERE);
                    restart();
                }
            }).show();
    }

    private void chooseScale() {
        final String[] items = new String[]{"0.8x", "1.0x", "1.2x", "1.5x", "2.0x"};
        final float[] values = new float[]{0.8f, 1.0f, 1.2f, 1.5f, 2.0f};
        UikitUi.showList(this, getString(R.string.settings_scale), items,
            new UikitUi.OnItemSelectListener() {
                public void onItemSelect(int p) {
                    UikitConfig.setUiScale(SettingsActivity.this, values[p]);
                    UikitToast.show(SettingsActivity.this, getString(R.string.restart_hint));
                    restart();
                }
            }).show();
    }

    private void restart() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent i = getIntent();
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);
        }
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
