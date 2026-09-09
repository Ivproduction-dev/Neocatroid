package org.catrobat.catroid.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class DebugMenuView extends FrameLayout {

    private final WindowManager windowManager;
    private final WindowManager.LayoutParams params;

    private float dX, dY;
    private final LinearLayout variablesContainer;
    private final Map<String, TextView> entryViewMap = new HashMap<>();

    private boolean isUpdatePending = false;
    private long lastUpdateTime = 0;
    private static final int UPDATE_INTERVAL_MS = 250;
    private static final int MAX_TEXT_DISPLAY_LENGTH = 60;
    private static final int MAX_LIST_ITEMS_DISPLAY = 50;

    private static final int LOG_DUMP_LINES = 1000;
    private static final int LOG_DISPLAY_LINES = 300;
    private static final int LOG_REFRESH_INTERVAL_MS = 2000;
    private static final int COLOR_TAB_INACTIVE = Color.parseColor("#607D8B");

    private boolean logMode = false;
    private View variablesScroll;
    private LinearLayout logLayout;
    private ScrollView logScroll;
    private TextView logText;
    private TextView varsTab;
    private TextView logTab;
    private int logSkipLines = 0;
    private int lastDumpSize = 0;
    private final Runnable logRefreshTask = new Runnable() {
        @Override
        public void run() {
            if (logMode) {
                refreshLogs();
            }
        }
    };

    private static final int COLOR_ACCENT = Color.parseColor("#A8DFF4");
    private static final int COLOR_TEXT_PRIMARY = Color.parseColor("#FFFFFF");
    private static final int COLOR_TEXT_SECONDARY = Color.parseColor("#B0BEC5");
    private static final int COLOR_LIST_TITLE = Color.parseColor("#AADBF0");
    private static final int COLOR_INPUT_BG = Color.parseColor("#00476F");

    public DebugMenuView(Context context) {
        super(context);
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.params = createLayoutParams();

        LayoutInflater.from(context).inflate(R.layout.dialog_debug_menu, this, true);

        View xmlRoot = getChildAt(0);
        if (xmlRoot != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) xmlRoot.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            xmlRoot.setLayoutParams(lp);
        }

        this.variablesContainer = findViewById(R.id.variables_container);
        if (this.variablesContainer == null) {
            throw new IllegalStateException("Could not find variables_container in dialog_debug_menu.xml");
        }

        setupWindowControls();
        setupResizer();
        buildInitialLayout();
        setupTabsAndLogPanel();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(logRefreshTask);
    }

    @SuppressLint("DefaultLocale")
    public void update() {
        if (logMode) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL_MS) {
            return;
        }
        lastUpdateTime = currentTime;

        post(() -> {
            Project project = ProjectManager.getInstance().getCurrentProject();
            if (project == null) return;

            for (UserVariable var : project.getUserVariables()) {
                updateVariableView("global." + var.getName(), var.getName(), String.valueOf(var.getValue()));
            }

            for (UserList list : project.getUserLists()) {
                updateListView("global." + list.getName(), list, variablesContainer);
            }

            List<Sprite> spritesCopy;
            try { spritesCopy = new ArrayList<>(project.getSpriteListWithClones()); }
            catch (Exception e) { return; }

            for (Sprite sprite : spritesCopy) {
                for (UserVariable var : sprite.getUserVariables()) {
                    updateVariableView(sprite.getName() + "." + var.getName(), var.getName(), String.valueOf(var.getValue()));
                }
                for (UserList list : sprite.getUserLists()) {
                    updateListView(sprite.getName() + "." + list.getName(), list, variablesContainer);
                }
            }
        });
    }

    private void updateVariableView(String uniqueId, String name, String rawValue) {
        TextView tv = entryViewMap.get(uniqueId);
        if (tv != null) {
            String newText = formatVariableText(name, rawValue);
            if (!tv.getText().toString().equals(newText)) {
                tv.setText(newText);
            }
        }
    }

    private void updateListView(String uniqueId, UserList list, ViewGroup container) {
        TextView tv = entryViewMap.get(uniqueId);
        if (tv != null) {
            ListState state = (ListState) tv.getTag();
            String newText = formatListText(list.getName(), list.getValue().size(), state.isExpanded);
            if (!tv.getText().toString().equals(newText)) {
                tv.setText(newText);
            }
            if (state.isExpanded) {
                refreshListItems(list, state, container, tv);
            }
        }
    }

    private String formatVariableText(String name, String value) {
        String displayValue = value;
        if (value.length() > MAX_TEXT_DISPLAY_LENGTH) {
            displayValue = value.substring(0, MAX_TEXT_DISPLAY_LENGTH) + "…";
        }
        return "  • " + name + " = " + displayValue;
    }

    private String formatListText(String name, int size, boolean isExpanded) {
        String prefix = isExpanded ? "  ▼ " : "  ▶ ";
        return getContext().getString(R.string.debug_menu_list_format, prefix, name, size);
    }

    private void buildInitialLayout() {
        Project project = ProjectManager.getInstance().getCurrentProject();
        if (project == null) return;

        variablesContainer.removeAllViews();
        entryViewMap.clear();

        addHeader(variablesContainer, getContext().getString(R.string.debug_menu_global_data));
        for (UserVariable var : project.getUserVariables()) {
            addDebugVariable(variablesContainer, "global." + var.getName(), var);
        }
        for (UserList list : project.getUserLists()) {
            addDebugList(variablesContainer, "global." + list.getName(), list);
        }

        for (Sprite sprite : project.getSpriteListWithClones()) {
            if (sprite != null && (!sprite.getUserVariables().isEmpty() || !sprite.getUserLists().isEmpty())) {
                addHeader(variablesContainer, getContext().getString(R.string.debug_menu_sprite) + ": " + sprite.getName().toUpperCase());
                for (UserVariable var : sprite.getUserVariables()) {
                    addDebugVariable(variablesContainer, sprite.getName() + "." + var.getName(), var);
                }
                for (UserList list : sprite.getUserLists()) {
                    addDebugList(variablesContainer, sprite.getName() + "." + list.getName(), list);
                }
            }
        }
    }

    private void setupTabsAndLogPanel() {
        ViewGroup dialogRoot = (ViewGroup) getChildAt(0);
        variablesScroll = (View) variablesContainer.getParent();

        LinearLayout tabRow = new LinearLayout(getContext());
        tabRow.setOrientation(LinearLayout.HORIZONTAL);

        varsTab = createTab(getContext().getString(R.string.debug_menu_tab_vars), true);
        varsTab.setOnClickListener(v -> setLogMode(false));
        logTab = createTab(getContext().getString(R.string.debug_menu_tab_log), false);
        logTab.setOnClickListener(v -> setLogMode(true));

        tabRow.addView(varsTab);
        tabRow.addView(logTab);
        dialogRoot.addView(tabRow, 1);

        logLayout = new LinearLayout(getContext());
        logLayout.setOrientation(LinearLayout.VERTICAL);
        logLayout.setVisibility(View.GONE);

        LinearLayout logToolbar = new LinearLayout(getContext());
        logToolbar.setOrientation(LinearLayout.HORIZONTAL);

        TextView refreshBtn = createLogAction(getContext().getString(R.string.debug_menu_log_refresh));
        refreshBtn.setOnClickListener(v -> refreshLogs());
        TextView copyBtn = createLogAction(getContext().getString(R.string.debug_menu_copy_full));
        copyBtn.setOnClickListener(v -> copyToClipboard("applog", logText.getText().toString()));
        TextView clearBtn = createLogAction(getContext().getString(R.string.debug_menu_log_clear));
        clearBtn.setOnClickListener(v -> clearLogs());

        logToolbar.addView(refreshBtn);
        logToolbar.addView(copyBtn);
        logToolbar.addView(clearBtn);
        logLayout.addView(logToolbar);

        float density = getResources().getDisplayMetrics().density;
        logScroll = new ScrollView(getContext());
        logScroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (300 * density)));

        logText = new TextView(getContext());
        logText.setTypeface(Typeface.MONOSPACE);
        logText.setTextSize(11);
        logText.setTextColor(COLOR_TEXT_PRIMARY);
        logText.setPadding(8, 8, 8, 8);
        logText.setTextIsSelectable(true);
        logScroll.addView(logText);
        logLayout.addView(logScroll);

        dialogRoot.addView(logLayout);
    }

    private TextView createTab(String text, boolean active) {
        TextView tab = new TextView(getContext());
        tab.setText(text);
        tab.setGravity(Gravity.CENTER);
        tab.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tab.setTextSize(13);
        float density = getResources().getDisplayMetrics().density;
        tab.setPadding(8, (int) (8 * density), 8, (int) (8 * density));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tab.setLayoutParams(lp);
        setTabActive(tab, active);
        return tab;
    }

    private void setTabActive(TextView tab, boolean active) {
        tab.setTextColor(active ? COLOR_ACCENT : COLOR_TAB_INACTIVE);
    }

    private TextView createLogAction(String text) {
        TextView action = new TextView(getContext());
        action.setText(text);
        action.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        action.setTextSize(12);
        action.setTextColor(COLOR_ACCENT);
        float density = getResources().getDisplayMetrics().density;
        action.setPadding((int) (12 * density), (int) (6 * density),
                (int) (12 * density), (int) (6 * density));
        return action;
    }

    private void setLogMode(boolean enabled) {
        logMode = enabled;
        setTabActive(varsTab, !enabled);
        setTabActive(logTab, enabled);
        variablesScroll.setVisibility(enabled ? View.GONE : View.VISIBLE);
        logLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
        removeCallbacks(logRefreshTask);
        if (enabled) {
            logSkipLines = 0;
            refreshLogs();
        }
    }

    private void refreshLogs() {
        new Thread(() -> {
            final AppLogReader.LogResult result = AppLogReader.readAppErrors(LOG_DUMP_LINES);
            post(() -> applyLogResult(result));
        }).start();
    }

    private void applyLogResult(AppLogReader.LogResult result) {
        if (!logMode) {
            return;
        }
        if (!result.isOk()) {
            logText.setText(String.format(getContext().getString(R.string.debug_menu_log_unavailable),
                    result.error));
        } else {
            lastDumpSize = result.lines.size();
            int from = Math.min(logSkipLines, result.lines.size());
            List<String> fresh = result.lines.subList(from, result.lines.size());
            int displayFrom = Math.max(0, fresh.size() - LOG_DISPLAY_LINES);
            if (fresh.isEmpty()) {
                logText.setText(getContext().getString(R.string.debug_menu_log_empty));
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = displayFrom; i < fresh.size(); i++) {
                    sb.append(fresh.get(i)).append('\n');
                }
                logText.setText(sb.toString());
            }
            logScroll.post(() -> logScroll.fullScroll(View.FOCUS_DOWN));
        }
        removeCallbacks(logRefreshTask);
        postDelayed(logRefreshTask, LOG_REFRESH_INTERVAL_MS);
    }

    private void clearLogs() {
        logSkipLines = lastDumpSize;
        logText.setText(getContext().getString(R.string.debug_menu_log_empty));
    }

    private void addHeader(ViewGroup container, String text) {
        TextView header = new TextView(getContext());
        header.setText(text);
        header.setTextSize(12);
        header.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        header.setTextColor(COLOR_ACCENT);
        header.setPadding(8, 24, 8, 4);
        container.addView(header);
    }

    private void addDebugVariable(ViewGroup container, String uniqueId, UserVariable var) {
        TextView entryView = new TextView(getContext());
        entryView.setText(formatVariableText(var.getName(), String.valueOf(var.getValue())));
        entryView.setTextSize(13);
        entryView.setTypeface(Typeface.MONOSPACE);
        entryView.setTextColor(COLOR_TEXT_PRIMARY);
        entryView.setPadding(8, 8, 8, 8);

        entryView.setOnClickListener(v -> showEditVariableDialog(var));
        entryView.setOnLongClickListener(v -> {
            copyToClipboard(var.getName(), String.valueOf(var.getValue()));
            return true;
        });

        container.addView(entryView);
        entryViewMap.put(uniqueId, entryView);
    }

    private void addDebugList(ViewGroup container, String uniqueId, UserList list) {
        TextView entryView = new TextView(getContext());
        entryView.setText(formatListText(list.getName(), list.getValue().size(), false));
        entryView.setTextSize(13);
        entryView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        entryView.setTextColor(COLOR_LIST_TITLE);
        entryView.setPadding(8, 8, 8, 8);

        entryView.setTag(new ListState(false, new ArrayList<>()));
        entryView.setOnClickListener(v -> toggleList(container, entryView, list));

        entryView.setOnLongClickListener(v -> {
            copyToClipboard(list.getName(), String.valueOf(list.getValue()));
            return true;
        });

        container.addView(entryView);
        entryViewMap.put(uniqueId, entryView);
    }

    private void toggleList(ViewGroup container, TextView headerView, UserList listData) {
        ListState state = (ListState) headerView.getTag();

        if (state.isExpanded) {
            for (View child : state.childViews) {
                container.removeView(child);
            }
            if (state.moreView != null) {
                container.removeView(state.moreView);
                state.moreView = null;
            }
            state.childViews.clear();
            state.isExpanded = false;
        } else {
            state.isExpanded = true;
            refreshListItems(listData, state, container, headerView);
        }
        headerView.setText(formatListText(listData.getName(), listData.getValue().size(), state.isExpanded));
    }

    @SuppressLint("DefaultLocale")
    private void refreshListItems(UserList listData, ListState state, ViewGroup container, TextView headerView) {
        int headerIndex = container.indexOfChild(headerView);
        int itemsCount = listData.getValue().size();
        int displayLimit = Math.min(itemsCount, MAX_LIST_ITEMS_DISPLAY);

        if (state.moreView != null) {
            container.removeView(state.moreView);
            state.moreView = null;
        }

        while (state.childViews.size() > displayLimit) {
            View v = state.childViews.remove(state.childViews.size() - 1);
            container.removeView(v);
        }

        for (int i = 0; i < displayLimit; i++) {
            Object item = listData.getValue().get(i);
            String rawVal = String.valueOf(item);
            String displayVal = rawVal.length() > MAX_TEXT_DISPLAY_LENGTH ? rawVal.substring(0, MAX_TEXT_DISPLAY_LENGTH) + "…" : rawVal;
            String newText = String.format("      [%d] %s", i + 1, displayVal);

            if (i < state.childViews.size()) {
                TextView itemView = (TextView) state.childViews.get(i);
                if (!itemView.getText().toString().equals(newText)) {
                    itemView.setText(newText);
                }
                final int index = i;
                itemView.setOnClickListener(v -> showEditListItemDialog(listData, index, rawVal));
                itemView.setOnLongClickListener(v -> {
                    copyToClipboard("Item " + (index+1), rawVal);
                    return true;
                });
            } else {
                TextView itemView = new TextView(getContext());
                itemView.setText(newText);
                itemView.setTextSize(12);
                itemView.setTypeface(Typeface.MONOSPACE);
                itemView.setTextColor(COLOR_TEXT_SECONDARY);
                itemView.setPadding(8, 4, 8, 4);

                final int index = i;
                itemView.setOnClickListener(v -> showEditListItemDialog(listData, index, rawVal));
                itemView.setOnLongClickListener(v -> {
                    copyToClipboard("Item " + (index+1), rawVal);
                    return true;
                });

                container.addView(itemView, headerIndex + 1 + i);
                state.childViews.add(itemView);
            }
        }

        if (itemsCount > MAX_LIST_ITEMS_DISPLAY) {
            TextView moreView = new TextView(getContext());
            moreView.setText(String.format(getContext().getString(R.string.debug_menu_more_items), itemsCount - MAX_LIST_ITEMS_DISPLAY));
            moreView.setTextSize(12);
            moreView.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
            moreView.setTextColor(Color.GRAY);
            container.addView(moreView, headerIndex + 1 + displayLimit);
            state.moreView = moreView;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupResizer() {
        TextView resizer = new TextView(getContext());
        resizer.setText("↘");
        resizer.setTextColor(COLOR_ACCENT);
        resizer.setTextSize(22);
        resizer.setPadding(32, 32, 32, 32);

        FrameLayout.LayoutParams handleParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END
        );
        addView(resizer, handleParams);

        resizer.setOnTouchListener(new View.OnTouchListener() {
            private int originalX, originalY;
            private int originalWidth, originalHeight;
            private float initialX, initialY;
            private View resizePreview;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        originalX = params.x;
                        originalY = params.y;
                        originalWidth = getWidth();
                        originalHeight = getHeight();
                        initialX = event.getRawX();
                        initialY = event.getRawY();

                        View dialogRoot = getChildAt(0);
                        if (dialogRoot != null) {
                            dialogRoot.setVisibility(View.GONE);
                        }

                        resizePreview = new View(getContext());
                        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                        gd.setColor(Color.argb(80, 255, 255, 255));
                        gd.setCornerRadius(32f);
                        gd.setStroke(6, COLOR_ACCENT);
                        resizePreview.setBackground(gd);

                        FrameLayout.LayoutParams previewParams = new FrameLayout.LayoutParams(originalWidth, originalHeight);
                        previewParams.leftMargin = originalX;
                        previewParams.topMargin = originalY;
                        previewParams.gravity = Gravity.TOP | Gravity.START;
                        resizePreview.setLayoutParams(previewParams);
                        addView(resizePreview);

                        params.width = WindowManager.LayoutParams.MATCH_PARENT;
                        params.height = WindowManager.LayoutParams.MATCH_PARENT;
                        params.x = 0;
                        params.y = 0;
                        windowManager.updateViewLayout(DebugMenuView.this, params);

                        resizer.bringToFront();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX() - initialX);
                        int dy = (int) (event.getRawY() - initialY);

                        int minSize = (int)(200 * getResources().getDisplayMetrics().density);
                        int newWidth = Math.max(originalWidth + dx, minSize);
                        int newHeight = Math.max(originalHeight + dy, minSize);

                        if (resizePreview != null) {
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) resizePreview.getLayoutParams();
                            lp.width = newWidth;
                            lp.height = newHeight;
                            resizePreview.setLayoutParams(lp);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        int finalWidth = originalWidth;
                        int finalHeight = originalHeight;

                        if (resizePreview != null) {
                            finalWidth = resizePreview.getWidth();
                            finalHeight = resizePreview.getHeight();
                            removeView(resizePreview);
                            resizePreview = null;
                        }

                        View root = getChildAt(0);
                        if (root != null) {
                            root.setVisibility(View.VISIBLE);
                        }

                        params.width = finalWidth;
                        params.height = finalHeight;
                        params.x = originalX;
                        params.y = originalY;
                        windowManager.updateViewLayout(DebugMenuView.this, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void showEditVariableDialog(UserVariable var) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_NeoCatroid_Dialog);
        builder.setTitle(String.format(getContext().getString(R.string.debug_menu_edit_variable), var.getName()));

        View container = createStyledInputContainer(String.valueOf(var.getValue()));
        EditText input = container.findViewById(R.id.formula_editor_edit_field);

        builder.setView(container);
        builder.setPositiveButton(R.string.debug_menu_save, (dialog, which) -> {
            var.setValue(input.getText().toString());
            update();
        });
        builder.setNegativeButton(R.string.debug_menu_cancel, (dialog, which) -> dialog.cancel());
        builder.setNeutralButton(R.string.debug_menu_copy_full, (dialog, which) -> copyToClipboard(var.getName(), String.valueOf(var.getValue())));
        builder.show();
    }

    private void showEditListItemDialog(UserList list, int index, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_NeoCatroid_Dialog);
        builder.setTitle(String.format(getContext().getString(R.string.debug_menu_edit_list_item), index + 1, list.getName()));

        View container = createStyledInputContainer(currentValue);
        EditText input = container.findViewById(R.id.formula_editor_edit_field);

        builder.setView(container);
        builder.setPositiveButton(R.string.debug_menu_save, (dialog, which) -> {
            list.getValue().set(index, input.getText().toString());
            update();
        });
        builder.setNegativeButton(R.string.debug_menu_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private View createStyledInputContainer(String initialText) {
        float density = getResources().getDisplayMetrics().density;

        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (16 * density);
        lp.setMargins(margin, margin, margin, margin);

        EditText input = new EditText(getContext());
        input.setId(R.id.formula_editor_edit_field);
        input.setText(initialText);
        input.setTextColor(COLOR_TEXT_PRIMARY);
        input.setHintTextColor(COLOR_TEXT_SECONDARY);
        int padding = (int) (12 * density);
        input.setPadding(padding, padding, padding, padding);

        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(COLOR_INPUT_BG);
        gd.setCornerRadius(12 * density);
        gd.setStroke((int)(2 * density), COLOR_ACCENT);
        input.setBackground(gd);

        if (initialText.length() > 30) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setMaxLines(8);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        input.setLayoutParams(lp);
        container.addView(input);

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(container);
        return scrollView;
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.debug_menu_copied, Toast.LENGTH_SHORT).show();
        }
    }

    private WindowManager.LayoutParams createLayoutParams() {
        final int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        int widthPx = (int) (280 * getContext().getResources().getDisplayMetrics().density);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                widthPx,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                flags,
                android.graphics.PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;
        return params;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupWindowControls() {
        View titleBar = findViewById(R.id.title_bar);
        View closeButton = findViewById(R.id.close_button);

        closeButton.setOnClickListener(v -> DebugMenuManager.getInstance().hide());

        if (titleBar instanceof ViewGroup) {
            ViewGroup titleGroup = (ViewGroup) titleBar;

            TextView consoleBtn = new TextView(getContext());
            consoleBtn.setText(">_");
            consoleBtn.setTextColor(COLOR_ACCENT);
            consoleBtn.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            consoleBtn.setTextSize(20);
            consoleBtn.setPadding(24, 0, 24, 0);
            consoleBtn.setGravity(android.view.Gravity.CENTER);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = android.view.Gravity.CENTER_VERTICAL;
            consoleBtn.setLayoutParams(lp);

            consoleBtn.setOnClickListener(v -> {
                DebugMenuManager.getInstance().hide();

                int currentOrientation = getContext().getResources().getConfiguration().orientation;
                int activityOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                if (currentOrientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                    activityOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }

                android.content.Intent intent = new android.content.Intent(getContext(), RuntimeConsoleActivity.class);
                intent.putExtra("orientation", activityOrientation);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            });

            titleGroup.addView(consoleBtn, titleGroup.getChildCount() - 1);
        }

        titleBar.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = params.x - event.getRawX();
                    dY = params.y - event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    params.x = (int) (event.getRawX() + dX);
                    params.y = (int) (event.getRawY() + dY);

                    scheduleWindowUpdate();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scheduleWindowUpdate();
                    return true;
            }
            return false;
        });
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return params;
    }

    private void scheduleWindowUpdate() {
        if (isUpdatePending) {
            return;
        }
        isUpdatePending = true;
        android.view.Choreographer.getInstance().postFrameCallback(frameTimeNanos -> {
            isUpdatePending = false;
            if (getParent() != null) {
                try {
                    windowManager.updateViewLayout(DebugMenuView.this, params);
                } catch (Exception ignored) {
                }
            }
        });
    }

    private static class ListState {
        boolean isExpanded;
        List<View> childViews;
        View moreView;

        ListState(boolean isExpanded, List<View> childViews) {
            this.isExpanded = isExpanded;
            this.childViews = childViews;
            this.moreView = null;
        }
    }
}
