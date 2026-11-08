package org.watchfiles;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.uikit.*;
import org.watchfiles.core.*;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQ_PICK = 2001;

    private File root, current;
    private ScrollView scroll;
    private LinearLayout listBox;
    private TextView pathView;

    private final Handler ui = new Handler(Looper.getMainLooper());
    private Dialog progressDialog;
    private TextView progressText;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(UikitUi.wrapScale(base));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        UikitTheme.applyTheme(this);
        UikitUi.applyEntryAnimation(this);

        root = Permissions.root(this);
        current = root;
        buildUi();

        if (!Permissions.hasStorage(this)) {
            UikitUi.showConfirm(this,
                getString(R.string.perm_title),
                getString(R.string.perm_msg),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int w) {
                        d.dismiss();
                        Permissions.request(MainActivity.this);
                    }
                }).show();
        }
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        File r = Permissions.root(this);
        if (!r.equals(root)) { root = r; current = r; }
        if (listBox != null) refresh();
    }

    @Override
    protected void onDestroy() {
        hideProgress();
        super.onDestroy();
    }

    private void buildUi() {
        boolean dark = UikitTheme.isDarkMode(this);

        LinearLayout rootL = new LinearLayout(this);
        rootL.setOrientation(LinearLayout.VERTICAL);
        rootL.setBackgroundColor(dark ? 0xFF000000 : 0xFFF4F6F8);
        setContentView(rootL);
        UikitUi.applyRoundPadding(this, rootL);

        pathView = new TextView(this);
        pathView.setTextSize(13);
        pathView.setSingleLine(true);
        pathView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        pathView.setGravity(Gravity.CENTER);
        pathView.setTextColor(UikitColor.getAccentColor(this, dark));
        pathView.setPadding(dp(12), dp(10), dp(12), dp(6));
        pathView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { goUp(); }
        });
        rootL.addView(pathView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipToPadding(false);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(listBox, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        rootL.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setPadding(dp(8), dp(6), dp(8), dp(8));
        rootL.addView(bar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        addBarBtn(bar, getString(R.string.new_folder_short), new View.OnClickListener() {
            public void onClick(View v) { doNewFolder(); }
        });
        addBarBtn(bar, getString(R.string.paste), new View.OnClickListener() {
            public void onClick(View v) { doPaste(); }
        });
        addBarBtn(bar, getString(R.string.more), new View.OnClickListener() {
            public void onClick(View v) { showMore(); }
        });

        UikitUi.setupWheelScroll(scroll, listBox);
    }

    private void addBarBtn(LinearLayout bar, String text, View.OnClickListener l) {
        UikitButton b = new UikitButton(this);
        b.setText(text);
        b.setTextSize(13);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(42), 1f);
        lp.leftMargin = dp(4);
        lp.rightMargin = dp(4);
        bar.addView(b, lp);
        b.setOnClickListener(l);
    }

    private void refresh() {
        boolean dark = UikitTheme.isDarkMode(this);
        pathView.setText(displayPath());

        listBox.removeAllViews();
        List<FileItem> items = FileOps.list(current, Prefs.isHidden(this));
        FileItem[] arr = items.toArray(new FileItem[0]);
        FileSorter.sort(arr, Prefs.getSort(this), Prefs.getOrder(this));

        if (arr.length == 0) {
            TextView empty = new TextView(this);
            empty.setText(getString(R.string.empty_dir));
            empty.setTextColor(dark ? 0xFFAAAAAA : 0xFF666666);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(32), 0, dp(32));
            listBox.addView(empty);
            return;
        }
        for (int i = 0; i < arr.length; i++) listBox.addView(buildRow(arr[i]));
    }

    private View buildRow(final FileItem it) {
        boolean dark = UikitTheme.isDarkMode(this);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(10), dp(2), dp(10));
        row.setBackground(UikitUi.getPillBackground(this));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(8);
        lp.leftMargin = dp(8);
        lp.rightMargin = dp(8);
        row.setLayoutParams(lp);

        TextView icon = new TextView(this);
        icon.setText(iconOf(it));
        icon.setTextSize(18);
        icon.setPadding(0, 0, dp(8), 0);
        row.addView(icon);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = new TextView(this);
        name.setText(it.dir ? it.name + "/" : it.name);
        name.setTextSize(15);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        name.setTextColor(dark ? 0xFFE0E0E0 : 0xFF1C1B1F);
        col.addView(name);

        TextView sub = new TextView(this);
        sub.setText(it.dir ? getString(R.string.folder) : FileOps.humanSize(it.size));
        sub.setTextSize(11);
        sub.setTextColor(dark ? 0xFFAAAAAA : 0xFF666666);
        col.addView(sub);

        row.addView(col);

        TextView more = new TextView(this);
        more.setText("\u22EE");
        more.setTextSize(20);
        more.setGravity(Gravity.CENTER);
        more.setPadding(dp(10), dp(2), dp(10), dp(2));
        more.setTextColor(dark ? 0xFFCCCCCC : 0xFF444444);
        row.addView(more);
        more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showItemMenu(it); }
        });

        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { onItemClick(it); }
        });
        return row;
    }

    private String iconOf(FileItem it) {
        switch (it.type()) {
            case FileItem.TYPE_DIR:   return "\uD83D\uDCC1";
            case FileItem.TYPE_TEXT:  return "\uD83D\uDCC4";
            case FileItem.TYPE_IMAGE: return "\uD83D\uDDBC";
            case FileItem.TYPE_ZIP:   return "\uD83D\uDCE6";
            default:                  return "\uD83D\uDCCE";
        }
    }

    private String displayPath() {
        if (current == null) return "/";
        if (current.equals(root)) return getString(R.string.storage_root);
        String n = current.getName();
        return "\u2191  " + (n == null || n.length() == 0 ? current.getAbsolutePath() : n);
    }

    private void onItemClick(FileItem it) {
        if (it.dir) {
            current = it.file;
            if (scroll != null) scroll.scrollTo(0, 0);
            refresh();
            return;
        }
        switch (it.type()) {
            case FileItem.TYPE_TEXT:
                openPreview(it.file, "text");
                break;
            case FileItem.TYPE_IMAGE:
                openPreview(it.file, "image");
                break;
            case FileItem.TYPE_ZIP:
                confirmExtract(it.file);
                break;
            default:
                showItemMenu(it);
                break;
        }
    }

    private void openPreview(File f, String type) {
        Intent i = new Intent(this, PreviewActivity.class);
        i.putExtra(PreviewActivity.EXTRA_PATH, f.getAbsolutePath());
        i.putExtra(PreviewActivity.EXTRA_TYPE, type);
        startActivity(i);
    }

    private void showItemMenu(final FileItem it) {
        String[] arr = new String[]{
            getString(R.string.rename),
            getString(R.string.copy),
            getString(R.string.cut),
            getString(R.string.delete),
            getString(R.string.copy_path),
            it.type() == FileItem.TYPE_ZIP ? getString(R.string.extract) : getString(R.string.info),
            getString(R.string.share)
        };
        UikitUi.showList(this, it.name, arr, new UikitUi.OnItemSelectListener() {
            public void onItemSelect(int p) {
                switch (p) {
                    case 0: doRename(it); break;
                    case 1: setPending(it.file, false, R.string.pending_copy); break;
                    case 2: setPending(it.file, true,  R.string.pending_cut); break;
                    case 3: doDelete(it); break;
                    case 4: copyPath(it.file); break;
                    case 5:
                        if (it.type() == FileItem.TYPE_ZIP) confirmExtract(it.file);
                        else showInfo(it);
                        break;
                    case 6: doShare(it.file); break;
                }
            }
        }).show();
    }

    private void showInfo(FileItem it) {
        String msg = it.file.getAbsolutePath() + "\n" + FileOps.humanSize(it.size);
        UikitUi.showConfirm(this, getString(R.string.info), msg, null).show();
    }

    private void doNewFolder() {
        UikitUi.showInput(this,
            getString(R.string.new_folder),
            getString(R.string.new_folder_default),
            new UikitUi.OnInputListener() {
                public void onInput(String s) {
                    if (s == null || s.trim().length() == 0) return;
                    File f = new File(current, s.trim());
                    if (f.mkdirs()) refresh();
                    else toast(R.string.op_failed);
                }
            }).show();
    }

    private void doRename(final FileItem it) {
        UikitUi.showInput(this, getString(R.string.rename), it.name,
            new UikitUi.OnInputListener() {
                public void onInput(String s) {
                    if (FileOps.rename(it.file, s)) refresh();
                    else toast(R.string.op_failed);
                }
            }).show();
    }

    private void doDelete(final FileItem it) {
        UikitUi.showConfirm(this, getString(R.string.delete),
            getString(R.string.confirm_delete, it.name),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int w) {
                    d.dismiss();
                    final File f = it.file;
                    bg(new Runnable() {
                        public void run() {
                            final boolean ok = FileOps.deleteRecursive(f);
                            ui.post(new Runnable() {
                                public void run() {
                                    toast(ok ? R.string.op_done : R.string.op_failed);
                                    refresh();
                                }
                            });
                        }
                    });
                }
            }).show();
    }

    private void setPending(File src, boolean move, int msgRes) {
        App.get().setPending(new App.PendingOp(src, move));
        toast(msgRes);
    }

    private void doPaste() {
        final App.PendingOp p = App.get().getPending();
        if (p == null) { toast(R.string.no_pending); return; }
        final File dst = FileOps.uniqueTarget(current, p.src.getName());
        showProgress(getString(R.string.pasting));
        bg(new Runnable() {
            public void run() {
                boolean ok;
                try {
                    ok = FileOps.copyRecursive(p.src, dst, new FileOps.CopyProgress() {
                        public void onProgress(long copied, long total, String cur) {
                            final int pct = total > 0 ? (int) (copied * 100 / total) : 0;
                            ui.post(new Runnable() {
                                public void run() {
                                    updateProgress(getString(R.string.copy_progress, pct, cur));
                                }
                            });
                        }
                    });
                    if (ok && p.move) FileOps.deleteRecursive(p.src);
                } catch (Exception e) { ok = false; }
                final boolean fok = ok;
                ui.post(new Runnable() {
                    public void run() {
                        hideProgress();
                        App.get().setPending(null);
                        toast(fok ? R.string.op_done : R.string.op_failed);
                        refresh();
                    }
                });
            }
        });
    }

    private void copyPath(File f) {
        String path = f.getAbsolutePath();
        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                android.content.ClipboardManager cm =
                    (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(android.content.ClipData.newPlainText("path", path));
            } else {
                android.text.ClipboardManager cm =
                    (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(path);
            }
            toast(R.string.path_copied);
        } catch (Exception e) {
            toast(R.string.op_failed);
        }
    }

    private void doShare(File f) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("application/octet-stream");
            i.putExtra(Intent.EXTRA_STREAM, ShareProvider.uriFor(f));
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(i, getString(R.string.share)));
        } catch (Exception e) {
            toast(R.string.op_failed);
        }
    }

    private void confirmExtract(final File zip) {
        File dest = Prefs.getUnzipMode(this) == Prefs.UNZIP_HERE
            ? zip.getParentFile()
            : new File(zip.getParentFile(), ZipExtractor.baseName(zip));
        String msg = getString(R.string.extract_to, dest.getAbsolutePath());
        UikitUi.showConfirm(this, getString(R.string.extract), msg,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int w) {
                    d.dismiss();
                    doExtract(zip);
                }
            }).show();
    }

    private void doExtract(final File zip) {
        final File dest = Prefs.getUnzipMode(this) == Prefs.UNZIP_HERE
            ? zip.getParentFile()
            : new File(zip.getParentFile(), ZipExtractor.baseName(zip));

        showProgress(getString(R.string.extracting));
        bg(new Runnable() {
            public void run() {
                try {
                    final int n = ZipExtractor.extract(zip, dest, new ZipExtractor.Progress() {
                        public void onProgress(final int count, final String cur) {
                            ui.post(new Runnable() {
                                public void run() {
                                    updateProgress(getString(R.string.extract_progress, count, cur));
                                }
                            });
                        }
                    });
                    ui.post(new Runnable() {
                        public void run() {
                            hideProgress();
                            UikitToast.show(MainActivity.this,
                                getString(R.string.extract_done, n));
                            refresh();
                        }
                    });
                } catch (final Exception e) {
                    ui.post(new Runnable() {
                        public void run() {
                            hideProgress();
                            String err = e.getMessage();
                            if (err == null) err = e.getClass().getSimpleName();
                            UikitUi.showConfirm(MainActivity.this,
                                getString(R.string.failed),
                                getString(R.string.extract_failed, err),
                                null).show();
                        }
                    });
                }
            }
        });
    }

    private void showMore() {
        String[] opts = new String[]{
            getString(R.string.sort),
            getString(R.string.toggle_hidden),
            getString(R.string.refresh),
            getString(R.string.open_picker),
            getString(R.string.settings),
            getString(R.string.about)
        };
        UikitUi.showList(this, getString(R.string.more), opts, new UikitUi.OnItemSelectListener() {
            public void onItemSelect(int p) {
                switch (p) {
                    case 0: chooseSort(); break;
                    case 1:
                        Prefs.setHidden(MainActivity.this, !Prefs.isHidden(MainActivity.this));
                        refresh();
                        break;
                    case 2: refresh(); break;
                    case 3: pickFromSystem(); break;
                    case 4: startActivity(new Intent(MainActivity.this, SettingsActivity.class)); break;
                    case 5: showAbout(); break;
                }
            }
        }).show();
    }

    private void chooseSort() {
        String[] modes = new String[]{
            getString(R.string.sort_name), getString(R.string.sort_size),
            getString(R.string.sort_time), getString(R.string.sort_type)
        };
        UikitUi.showList(this, getString(R.string.sort), modes, new UikitUi.OnItemSelectListener() {
            public void onItemSelect(int p) {
                Prefs.setSort(MainActivity.this, p);
                refresh();
            }
        }).show();
    }

    private void showAbout() {
        UikitUi.showConfirm(this, getString(R.string.about),
            getString(R.string.about_text), null).show();
    }

    private void pickFromSystem() {
        Intent i;
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
        } else {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
        }
        try {
            startActivityForResult(i, REQ_PICK);
        } catch (android.content.ActivityNotFoundException e) {
            UikitToast.show(this, getString(R.string.no_file_picker));
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQ_PICK && res == RESULT_OK && data != null && data.getData() != null) {
            final Uri uri = data.getData();
            showProgress(getString(R.string.importing));
            final Context ctx = this;
            bg(new Runnable() {
                public void run() {
                    try {
                        final File local = UriReader.materialize(ctx, uri);
                        ui.post(new Runnable() {
                            public void run() {
                                hideProgress();
                                File dst = FileOps.uniqueTarget(current, local.getName());
                                try {
                                    FileOps.copyRecursive(local, dst, null);
                                    local.delete();
                                    toast(R.string.op_done);
                                    refresh();
                                } catch (Exception e) { toast(R.string.op_failed); }
                            }
                        });
                    } catch (final Exception e) {
                        ui.post(new Runnable() {
                            public void run() {
                                hideProgress();
                                toast(R.string.import_failed);
                            }
                        });
                    }
                }
            });
        }
    }

    private void showProgress(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            updateProgress(msg);
            return;
        }
        boolean dark = UikitTheme.isDarkMode(this);
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(24), dp(20), dp(24), dp(20));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(dark ? 0xFF1E1E1E : 0xFFFFFFFF);
        bg.setCornerRadius(dp(20));
        box.setBackground(bg);

        progressText = new TextView(this);
        progressText.setText(msg);
        progressText.setTextSize(13);
        progressText.setGravity(Gravity.CENTER);
        progressText.setTextColor(dark ? 0xFFE0E0E0 : 0xFF1C1B1F);
        box.addView(progressText);

        progressDialog.setContentView(box);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void updateProgress(String s) {
        if (progressText != null) progressText.setText(s);
    }

    private void hideProgress() {
        if (progressDialog != null) {
            try { progressDialog.dismiss(); } catch (Exception ignored) {}
            progressDialog = null;
            progressText = null;
        }
    }

    private boolean goUp() {
        if (current != null && !current.equals(root) && current.getParentFile() != null) {
            current = current.getParentFile();
            if (scroll != null) scroll.scrollTo(0, 0);
            refresh();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!goUp()) super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] p, int[] r) {
        super.onRequestPermissionsResult(req, p, r);
        if (req == Permissions.REQ_STORAGE) {
            root = Permissions.root(this);
            current = root;
            refresh();
            if (!Permissions.hasStorage(this)) toast(R.string.perm_msg);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (UikitUi.handleSwipeBack(this, ev)) return true;
        return super.dispatchTouchEvent(ev);
    }

    private void toast(int res) { UikitToast.show(this, getString(res)); }

    private void bg(final Runnable r) { new Thread(r).start(); }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
