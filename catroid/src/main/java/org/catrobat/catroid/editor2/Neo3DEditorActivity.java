package org.catrobat.catroid.editor2;

import android.app.Activity;
import android.content.Intent;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.text.InputType;
import android.view.Choreographer;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.editor.ThreeDEditorRouter;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DLight;
import org.catrobat.catroid.neo3d.Neo3DMath;
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody;
import org.catrobat.catroid.neo3d.Neo3DPersistedObject;
import org.catrobat.catroid.neo3d.backend.Neo3DFilamentBackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Neo3DEditorActivity extends Activity {

    private static final int REQUEST_MODEL = 4102;
    private final Choreographer.FrameCallback frameCallback = this::renderFrame;
    private Neo3DEditorDocument document;
    private SurfaceView surface;
    private TextView status;
    private LinearLayout hierarchyPanel;
    private LinearLayout inspectorContent;
    private long previousFrameNs;
    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean draggingCamera;
    private float previousPinchSpan;
    private float previousPointerCenterX;
    private float previousPointerCenterY;
    private int transformTool;
    private int moveAxis;
    private boolean snapEnabled;
    private static final float SNAP_STEP = 0.5f;
    private final Button[] axisButtons = new Button[4];
    private Button snapButton;
    private Button gridButton;
    private View axisBar;
    private String hierarchyQuery = "";
    private final android.os.Handler searchHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable pendingSearch;
    private final android.os.Handler transformHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable pendingLiveApply;
    private boolean gestureChanged;
    private String gestureObjectId;
    private Button undoButton;
    private Button redoButton;
    private final Button[] transformButtons = new Button[4];
    private View wasdBar;
    private Neo3DJoystickView joystick;
    private Button pasteButton;    private float camVelForward;
    private float camVelStrafe;
    private float camVelUp;
    private String statusBase = "";
    private long lastFpsStatusNs;
    private static final long FPS_STATUS_INTERVAL_NS = 500_000_000L;
    private final List<AlertDialog> activeDialogs = new ArrayList<>();
    private final Map<String, KeyframePlayback> keyPlayers = new HashMap<>();

    private static final class KeyframePlayback {
        float time;
        final float[] position = new float[3];
        final float[] rotation = new float[3];
        final float[] scale = new float[3];
    }

    private void trackDialog(AlertDialog dialog) {
        activeDialogs.add(dialog);
        dialog.setOnDismissListener(d -> activeDialogs.remove(dialog));
    }
    private final ExecutorService importQueue = Executors.newSingleThreadExecutor();
    private volatile boolean destroyed;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (ProjectManager.getInstance().getCurrentProject() == null
                || ProjectManager.getInstance().getCurrentlyEditedScene() == null) {
            finish();
            return;
        }
        document = new Neo3DEditorDocument(this);
        document.setModelLoadFailureListener(() -> {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, R.string.editor_3d_unsupported_model,
                        Toast.LENGTH_LONG).show();
            }
        });
        buildWorkspace();
        attachRenderer();
        refreshPanels();
        applyControlsMode();
        checkForRecovery();
        maybeShowControlsDialog();
    }

    private void buildWorkspace() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColor(R.color.app_background_dark));
        root.addView(buildToolbar(), new LinearLayout.LayoutParams(-1, dp(56)));

        boolean wide = getResources().getConfiguration().screenWidthDp >= 840;
        if (wide) {
            LinearLayout workspace = new LinearLayout(this);
            workspace.setOrientation(LinearLayout.HORIZONTAL);
            hierarchyPanel = buildHierarchyPanel();
            workspace.addView(hierarchyPanel, new LinearLayout.LayoutParams(dp(220), -1));
            workspace.addView(buildViewport(), new LinearLayout.LayoutParams(0, -1, 1f));
            inspectorContent = buildInspectorPanel();
            workspace.addView(inspectorContent, new LinearLayout.LayoutParams(dp(320), -1));
            root.addView(workspace, new LinearLayout.LayoutParams(-1, 0, 1f));
            root.addView(buildStatusBar(), new LinearLayout.LayoutParams(-1, dp(40)));
        } else {
            root.addView(buildViewport(), new LinearLayout.LayoutParams(-1, 0, 1f));
            root.addView(buildCompactFooter(), new LinearLayout.LayoutParams(-1, dp(52)));
        }
        setContentView(root);
    }

    private View buildToolbar() {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(8), dp(4), dp(8), dp(4));
        bar.setBackgroundColor(getColor(R.color.toolbar_background));
        addToolbarButton(bar, R.string.editor_3d_scene_settings,
                this::showSceneSettingsDialog);
        addToolbarButton(bar, R.string.editor_3d_view_presets, this::showViewPresetsDialog);
        undoButton = addToolbarButton(bar, R.string.editor_3d_undo, this::undo);
        redoButton = addToolbarButton(bar, R.string.editor_3d_redo, this::redo);
        transformButtons[0] = addToolbarButton(bar, R.string.editor_3d_tool_select,
                () -> setTransformTool(0));
        transformButtons[1] = addToolbarButton(bar, R.string.editor_3d_tool_move,
                () -> setTransformTool(1));
        transformButtons[2] = addToolbarButton(bar, R.string.editor_3d_tool_rotate,
                () -> setTransformTool(2));
        transformButtons[3] = addToolbarButton(bar, R.string.editor_3d_tool_scale,
                () -> setTransformTool(3));
        addToolbarButton(bar, R.string.editor_3d_add_cube, () -> addPrimitive(0));        addToolbarButton(bar, R.string.editor_3d_add_sphere, () -> addPrimitive(1));
        addToolbarButton(bar, R.string.editor_3d_add_cylinder, () -> addPrimitive(2));
        addToolbarButton(bar, R.string.editor_3d_add_camera, () -> {
            select(document.addCamera());
        });
        addToolbarButton(bar, R.string.editor_3d_add_light, this::showLightChoices);
        addToolbarButton(bar, R.string.editor_3d_import_model, this::chooseModel);
        addToolbarButton(bar, R.string.editor_3d_save, () -> saveAndNotify());
        addToolbarButton(bar, R.string.editor_3d_switch_classic,
                () -> ThreeDEditorRouter.switchEditor(this, ThreeDEditorRouter.CLASSIC));
        setTransformTool(transformTool);
        scroll.addView(bar);
        return scroll;
    }

    private Button addToolbarButton(LinearLayout bar, int title, Runnable action) {
        Button button = new Button(this);
        button.setText(title);
        button.setAllCaps(false);
        if (action != null) button.setOnClickListener(view -> action.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(44));
        params.setMargins(dp(3), 0, dp(3), 0);
        bar.addView(button, params);
        return button;
    }

    private View buildViewport() {
        FrameLayout frame = new FrameLayout(this);
        frame.setBackgroundColor(Color.rgb(31, 34, 40));
        surface = new SurfaceView(this);
        frame.addView(surface, new FrameLayout.LayoutParams(-1, -1));
        TextView hint = new TextView(this);
        hint.setText(R.string.editor_3d_viewport_hint);
        hint.setTextColor(Color.WHITE);
        hint.setBackgroundColor(0x66000000);
        hint.setPadding(dp(10), dp(6), dp(10), dp(6));
        FrameLayout.LayoutParams hintParams = new FrameLayout.LayoutParams(-2, -2,
                android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
        hintParams.bottomMargin = dp(12);
        frame.addView(hint, hintParams);
        axisBar = buildAxisBar();
        FrameLayout.LayoutParams axisParams = new FrameLayout.LayoutParams(-2, -2,
                android.view.Gravity.TOP | android.view.Gravity.END);
        axisParams.topMargin = dp(8);
        axisParams.rightMargin = dp(8);
        frame.addView(axisBar, axisParams);
        updateAxisBar();
        wasdBar = buildWasdBar();
        FrameLayout.LayoutParams wasdParams = new FrameLayout.LayoutParams(-2, -2,
                android.view.Gravity.BOTTOM | android.view.Gravity.START);
        wasdParams.leftMargin = dp(12);
        wasdParams.bottomMargin = dp(56);
        frame.addView(wasdBar, wasdParams);
        joystick = new Neo3DJoystickView(this);
        FrameLayout.LayoutParams joyParams = new FrameLayout.LayoutParams(dp(128), dp(128),
                android.view.Gravity.BOTTOM | android.view.Gravity.START);
        joyParams.leftMargin = dp(16);
        joyParams.bottomMargin = dp(56);
        frame.addView(joystick, joyParams);
        surface.setOnTouchListener(this::handleViewportTouch);
        return frame;
    }

    private View buildWasdBar() {
        android.widget.GridLayout grid = new android.widget.GridLayout(this);
        grid.setColumnCount(3);
        grid.setBackgroundColor(0x66000000);
        grid.setPadding(dp(4), dp(4), dp(4), dp(4));
        addHoldButton(grid, "Q", () -> camVelUp = -1f, () -> camVelUp = 0f);
        addHoldButton(grid, "W", () -> camVelForward = 1f, () -> camVelForward = 0f);
        addHoldButton(grid, "E", () -> camVelUp = 1f, () -> camVelUp = 0f);
        addHoldButton(grid, "A", () -> camVelStrafe = -1f, () -> camVelStrafe = 0f);
        addHoldButton(grid, "S", () -> camVelForward = -1f, () -> camVelForward = 0f);
        addHoldButton(grid, "D", () -> camVelStrafe = 1f, () -> camVelStrafe = 0f);
        return grid;
    }

    private void addHoldButton(android.widget.GridLayout grid, String label,
            Runnable onDown, Runnable onUp) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(4), dp(4), dp(4), dp(4));
        button.setOnTouchListener((view, event) -> {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                onDown.run();
                return true;
            }
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                onUp.run();
                return true;
            }
            return false;
        });
        grid.addView(button);
    }

    private final float[] joyOut = new float[2];

    private void applyCameraVelocity(float delta) {
        float f = camVelForward;
        float s = camVelStrafe;
        float u = camVelUp;
        if (joystick != null && joystick.getVisibility() == View.VISIBLE) {
            float[] joy = joystick.consumeVector(joyOut);
            if (joy != null) {
                s += joy[0];
                f += -joy[1];
            }
        }
        if (f == 0f && s == 0f && u == 0f || document == null) return;
        Neo3DGameObject camera = document.viewportCamera();
        if (camera == null || camera.getCamera() == null) return;
        float[] p = camera.getTransform().getPosition();
        float speed = cameraFlySpeed(camera, p);
        if (!camera.getCamera().isUseTransformOrientation()) {
            float[] target = camera.getCamera().getLookAtTarget();
            float fx = target[0] - p[0];
            float fy = target[1] - p[1];
            float fz = target[2] - p[2];
            float len = (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
            if (len < 1e-6f) return;
            fx /= len;
            fy /= len;
            fz /= len;
            float[] up = camera.getCamera().getUp();
            float rx = fy * up[2] - fz * up[1];
            float ry = fz * up[0] - fx * up[2];
            float rz = fx * up[1] - fy * up[0];
            float rl = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
            if (rl < 1e-6f) return;
            rx /= rl;
            ry /= rl;
            rz /= rl;
            float step = speed * delta;
            float nfX = p[0] + fx * f * step + rx * s * step;
            float nfY = p[1] + (fy * f + u) * step + ry * s * step;
            float nfZ = p[2] + fz * f * step + rz * s * step;
            float nDx = nfX - target[0];
            float nDy = nfY - target[1];
            float nDz = nfZ - target[2];
            float nLen = (float) Math.sqrt(nDx * nDx + nDy * nDy + nDz * nDz);
            if (nLen > 0.25f) {
                camera.getTransform().setPosition(nfX, nfY, nfZ);
                document.engine().syncObject(document.scene().getId(), camera.getId());
            }
            return;
        }
        float[] euler = camera.getTransform().getEulerDeg();
        double yaw = Math.toRadians(euler[0]);
        double pitch = Math.toRadians(euler[1]);
        float cosP = (float) Math.cos(pitch);
        float fx = (float) (-Math.sin(yaw) * cosP);
        float fy = (float) Math.sin(pitch);
        float fz = (float) (-Math.cos(yaw) * cosP);
        float rx = (float) Math.cos(yaw);
        float rz = (float) -Math.sin(yaw);
        camera.getTransform().setPosition(
                p[0] + (fx * f + rx * s) * speed * delta,
                p[1] + (fy * f + u) * speed * delta,
                p[2] + (fz * f + rz * s) * speed * delta);
        document.engine().syncObject(document.scene().getId(), camera.getId());
    }

    private float cameraFlySpeed(Neo3DGameObject camera, float[] position) {
        float dist = 6f;
        if (camera.getCamera() != null && !camera.getCamera().isUseTransformOrientation()) {
            float[] target = camera.getCamera().getLookAtTarget();
            float dx = target[0] - position[0];
            float dy = target[1] - position[1];
            float dz = target[2] - position[2];
            dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        } else {
            Neo3DGameObject selected = document.selectedObject();
            float[] anchor = selected != null
                    ? selected.getTransform().getPosition() : new float[]{0f, 0f, 0f};
            float dx = anchor[0] - position[0];
            float dy = anchor[1] - position[1];
            float dz = anchor[2] - position[2];
            dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        if (dist < 0.5f) dist = 0.5f;
        if (dist > 200f) dist = 200f;
        return dist * 0.8f;
    }

    private float dragGain() {
        Neo3DGameObject camera = document != null ? document.viewportCamera() : null;
        if (camera == null) return 1f;
        float[] p = camera.getTransform().getPosition();
        float gain = cameraFlySpeed(camera, p) / 6f;
        if (gain < 0.1f) gain = 0.1f;
        if (gain > 50f) gain = 50f;
        return gain;
    }

    private View buildAxisBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(0x66000000);
        bar.setPadding(dp(4), dp(2), dp(4), dp(2));
        int[] labels = {R.string.editor_3d_axis_all, R.string.editor_3d_axis_x,
                R.string.editor_3d_axis_y, R.string.editor_3d_axis_z};
        for (int i = 0; i < labels.length; i++) {
            final int axis = i;
            Button button = new Button(this);
            button.setText(labels[i]);
            button.setAllCaps(false);
            button.setMinWidth(0);
            button.setMinimumWidth(0);
            button.setPadding(dp(10), dp(2), dp(10), dp(2));
            button.setOnClickListener(view -> setMoveAxis(axis));
            axisButtons[i] = button;
            bar.addView(button);
        }
        snapButton = new Button(this);
        snapButton.setText(R.string.editor_3d_snap);
        snapButton.setAllCaps(false);
        snapButton.setMinWidth(0);
        snapButton.setMinimumWidth(0);
        snapButton.setPadding(dp(10), dp(2), dp(10), dp(2));
        snapButton.setOnClickListener(view -> {
            snapEnabled = !snapEnabled;
            updateAxisBar();
        });
        bar.addView(snapButton);
        gridButton = new Button(this);
        gridButton.setText(R.string.editor_3d_grid);
        gridButton.setAllCaps(false);
        gridButton.setMinWidth(0);
        gridButton.setMinimumWidth(0);
        gridButton.setPadding(dp(10), dp(2), dp(10), dp(2));
        gridButton.setOnClickListener(view -> {
            document.setGridVisible(!document.isGridVisible());
            updateAxisBar();
        });
        bar.addView(gridButton);
        return bar;
    }

    private void setMoveAxis(int axis) {
        moveAxis = axis;
        updateAxisBar();
    }

    private void updateAxisBar() {
        for (int i = 0; i < axisButtons.length; i++) {
            if (axisButtons[i] != null) {
                axisButtons[i].setTextColor(getColor(i == moveAxis
                        ? R.color.accent : R.color.solid_white));
            }
        }
        if (snapButton != null) {
            snapButton.setTextColor(getColor(snapEnabled
                    ? R.color.accent : R.color.solid_white));
        }
        if (gridButton != null && document != null) {
            gridButton.setTextColor(getColor(document.isGridVisible()
                    ? R.color.accent : R.color.solid_white));
        }
        if (axisBar != null) {
            axisBar.setVisibility(transformTool == 0 ? View.GONE : View.VISIBLE);
        }
    }

    private float snapf(float value) {
        return snapEnabled ? Math.round(value / SNAP_STEP) * SNAP_STEP : value;
    }

    private LinearLayout buildHierarchyPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(10), dp(10), dp(10), dp(8));
        panel.setBackgroundColor(getColor(R.color.surface_card));
        panel.addView(heading(R.string.editor_3d_hierarchy));
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint(R.string.editor_3d_search_hint);
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(Color.LTGRAY);
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pendingSearch != null) searchHandler.removeCallbacks(pendingSearch);
                final String query = s.toString();
                pendingSearch = () -> {
                    hierarchyQuery = query.trim().toLowerCase(java.util.Locale.ROOT);
                    refreshHierarchy();
                };
                searchHandler.postDelayed(pendingSearch, 250L);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        panel.addView(search, fullWidth());
        pasteButton = new Button(this);
        pasteButton.setText(R.string.editor_3d_paste);
        pasteButton.setAllCaps(false);
        pasteButton.setOnClickListener(view -> {
            java.util.List<Neo3DGameObject> pasted = document.pasteClipboard();
            if (!pasted.isEmpty()) {
                document.clearSelection();
                for (Neo3DGameObject item : pasted) document.toggleSelect(item.getId());
                refreshPanels();
                Toast.makeText(this, getString(R.string.editor_3d_pasted, pasted.size()),
                        Toast.LENGTH_SHORT).show();
            }
        });
        panel.addView(pasteButton, fullWidth());
        ScrollView list = new ScrollView(this);
        hierarchyPanel = panel;
        panel.addView(list, new LinearLayout.LayoutParams(-1, 0, 1f));
        LinearLayout items = new LinearLayout(this);
        items.setOrientation(LinearLayout.VERTICAL);
        list.addView(items);
        panel.setTag(items);
        return panel;
    }

    private LinearLayout buildInspectorPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(12), dp(10), dp(12), dp(8));
        panel.setBackgroundColor(getColor(R.color.surface_card));
        panel.addView(heading(R.string.editor_3d_inspector));
        ScrollView scroll = new ScrollView(this);
        inspectorContent = new LinearLayout(this);
        inspectorContent.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(inspectorContent);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));
        return panel;
    }

    private View buildCompactFooter() {
        LinearLayout footer = new LinearLayout(this);
        footer.setGravity(android.view.Gravity.CENTER_VERTICAL);
        footer.setBackgroundColor(getColor(R.color.toolbar_background));
        addFooterButton(footer, R.string.editor_3d_hierarchy, this::showObjectsDialog);
        addFooterButton(footer, R.string.editor_3d_inspector, this::showInspectorDialog);
        addFooterButton(footer, R.string.editor_3d_save, this::saveAndNotify);
        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setSingleLine(true);
        status.setPadding(dp(8), 0, dp(8), 0);
        footer.addView(status, new LinearLayout.LayoutParams(0, -2, 1f));
        return footer;
    }

    private void addFooterButton(LinearLayout footer, int title, Runnable action) {
        Button button = new Button(this);
        button.setText(title);
        button.setAllCaps(false);
        button.setOnClickListener(view -> action.run());
        footer.addView(button, new LinearLayout.LayoutParams(-2, dp(48)));
    }

    private View buildStatusBar() {
        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setGravity(android.view.Gravity.CENTER_VERTICAL);
        status.setPadding(dp(12), 0, dp(12), 0);
        status.setBackgroundColor(getColor(R.color.toolbar_background));
        return status;
    }

    private TextView heading(int text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.solid_white));
        view.setTextSize(17f);
        view.setTypeface(null, android.graphics.Typeface.BOLD);
        view.setPadding(0, dp(4), 0, dp(12));
        return view;
    }

    private void attachRenderer() {
        if (document.engine().getBackend() instanceof Neo3DFilamentBackend) {
            ((Neo3DFilamentBackend) document.engine().getBackend()).attachSurfaceView(surface);
        }
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override public void surfaceCreated(SurfaceHolder holder) { }
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                document.engine().resize(width, height);
            }
            @Override public void surfaceDestroyed(SurfaceHolder holder) { }
        });
    }

    private boolean handleViewportTouch(View view, MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            float x1 = event.getX(0), y1 = event.getY(0);
            float x2 = event.getX(1), y2 = event.getY(1);
            float span = (float) Math.hypot(x2 - x1, y2 - y1);
            float centerX = (x1 + x2) * 0.5f;
            float centerY = (y1 + y2) * 0.5f;
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                previousPinchSpan = span;
                previousPointerCenterX = centerX;
                previousPointerCenterY = centerY;
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                Neo3DGameObject camera = document.viewportCamera();
                if (camera != null && previousPinchSpan > 0f) {
                    float[] position = camera.getTransform().getPosition();
                    float zoom = previousPinchSpan > 0f ? span / previousPinchSpan : 1f;
                    float z = Math.max(0.25f, Math.min(200f, position[2] / zoom));
                    float panX = (centerX - previousPointerCenterX) / Math.max(1f, view.getWidth()) * z;
                    float panY = (centerY - previousPointerCenterY) / Math.max(1f, view.getHeight()) * z;
                    camera.getTransform().setPosition(position[0] - panX, position[1] + panY, z);
                    previousPinchSpan = span;
                    previousPointerCenterX = centerX;
                    previousPointerCenterY = centerY;
                }
            }
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            previousPinchSpan = 0f;
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = lastX = event.getX(); downY = lastY = event.getY();
            draggingCamera = false;
            gestureChanged = false;
            gestureObjectId = document.selectedId();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX() - lastX, dy = event.getY() - lastY;
            if (draggingCamera || Math.hypot(event.getX() - downX, event.getY() - downY)
                    > ViewConfiguration.get(this).getScaledTouchSlop()) {
                draggingCamera = true;
                if (transformTool != 0 && gestureObjectId != null) {
                    transformSelectedObject(dx, dy);
                    gestureChanged = true;
                } else {
                    document.engine().dragCameraLook(document.scene().getId(), dx, dy);
                }
            }
            lastX = event.getX(); lastY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (gestureChanged && gestureObjectId != null) {
                java.util.List<Neo3DGameObject> changed = document.selectedObjects();
                if (changed.isEmpty()) {
                    document.sync(gestureObjectId);
                } else {
                    for (Neo3DGameObject item : changed) document.sync(item.getId());
                }
                refreshPanels();
            } else if (!draggingCamera) {
                String objectId = document.engine().pickObject(document.scene().getId(),
                        event.getX(), event.getY());
                if (objectId != null && document.data(objectId) != null) {
                    document.select(objectId);
                    refreshPanels();
                }
            }
        }
        return true;
    }

    private void transformSelectedObject(float dx, float dy) {
        java.util.List<Neo3DGameObject> targets = document.selectedObjects();
        if (targets.isEmpty()) {
            Neo3DGameObject fallback = document.scene().getObject(gestureObjectId);
            if (fallback == null) return;
            targets = new java.util.ArrayList<>();
            targets.add(fallback);
        }
        for (Neo3DGameObject object : targets) {
            applyDragToObject(object, dx, dy);
            document.engine().syncObject(document.scene().getId(), object.getId());
        }
    }

    private void applyDragToObject(Neo3DGameObject object, float dx, float dy) {
        if (transformTool == 1) {
            float[] position = object.getTransform().getPosition();
            float unitsPerDp = 2f * dragGain() / Math.max(1f, surface.getHeight());
            float nx = position[0];
            float ny = position[1];
            float nz = position[2];
            if (moveAxis == 1) {
                nx = position[0] + dx * unitsPerDp;
            } else if (moveAxis == 2) {
                ny = position[1] - dy * unitsPerDp;
            } else if (moveAxis == 3) {
                nz = position[2] - dy * unitsPerDp;
            } else {
                nx = position[0] + dx * unitsPerDp;
                ny = position[1] - dy * unitsPerDp;
            }
            object.getTransform().setPosition(snapf(nx), snapf(ny), snapf(nz));
        } else if (transformTool == 2) {
            float[] rotation = object.getTransform().getEulerDeg();
            if (moveAxis == 1) {
                object.getTransform().setRotationEulerDeg(rotation[0],
                        rotation[1] + dy * 0.5f, rotation[2]);
            } else if (moveAxis == 2) {
                object.getTransform().setRotationEulerDeg(rotation[0] + dx * 0.5f,
                        rotation[1], rotation[2]);
            } else if (moveAxis == 3) {
                object.getTransform().setRotationEulerDeg(rotation[0], rotation[1],
                        rotation[2] + dx * 0.5f);
            } else {
                object.getTransform().setRotationEulerDeg(rotation[0] + dx * 0.5f,
                        rotation[1] + dy * 0.5f, rotation[2]);
            }
        } else if (transformTool == 3) {
            float[] scale = object.getTransform().getScale();
            float factor = (float) Math.exp((dx - dy) * 0.01f);
            if (moveAxis == 1) {
                object.getTransform().setScale(clamp(scale[0] * factor, 0.01f, 1000f),
                        scale[1], scale[2]);
            } else if (moveAxis == 2) {
                object.getTransform().setScale(scale[0],
                        clamp(scale[1] * factor, 0.01f, 1000f), scale[2]);
            } else if (moveAxis == 3) {
                object.getTransform().setScale(scale[0], scale[1],
                        clamp(scale[2] * factor, 0.01f, 1000f));
            } else {
                object.getTransform().setScale(clamp(scale[0] * factor, 0.01f, 1000f),
                        clamp(scale[1] * factor, 0.01f, 1000f),
                        clamp(scale[2] * factor, 0.01f, 1000f));
            }
        }
    }

    private void setTransformTool(int tool) {
        transformTool = tool;
        for (int i = 0; i < transformButtons.length; i++) {
            if (transformButtons[i] != null) {
                transformButtons[i].setTextColor(getColor(i == tool
                        ? R.color.accent : R.color.solid_white));
            }
        }
        updateAxisBar();
    }

    private void undo() {
        if (document.undo()) refreshPanels();
    }

    private void redo() {
        if (document.redo()) refreshPanels();
    }

    private void refreshPanels() {
        if (undoButton != null) undoButton.setEnabled(document.canUndo());
        if (redoButton != null) redoButton.setEnabled(document.canRedo());
        scheduleUndoButtonsRefresh();
        refreshHierarchy();
        refreshInspector();
        Neo3DGameObject selected = document.selectedObject();
        statusBase = selected == null ? getString(R.string.editor_3d_no_selection)
                : getString(R.string.editor_3d_selected, selected.getName());
        if (status != null) {
            status.setText(statusBase);
        }
    }

    private void refreshHierarchyOnly() {
        if (undoButton != null) undoButton.setEnabled(document.canUndo());
        if (redoButton != null) redoButton.setEnabled(document.canRedo());
        scheduleUndoButtonsRefresh();
        refreshHierarchy();
    }

    private final Runnable undoButtonsRefresh = () -> {
        if (document == null) return;
        if (undoButton != null) undoButton.setEnabled(document.canUndo());
        if (redoButton != null) redoButton.setEnabled(document.canRedo());
    };

    private void scheduleUndoButtonsRefresh() {
        searchHandler.removeCallbacks(undoButtonsRefresh);
        searchHandler.postDelayed(undoButtonsRefresh, 400L);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_Z) {
            if (event.isShiftPressed()) redo(); else undo();
            return true;
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_Y) {
            redo();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_FORWARD_DEL) {
            if (document.selectionCount() > 1) {
                confirmDeleteSelected();
                return true;
            }
            Neo3DGameObject selected = document.selectedObject();
            if (selected != null) confirmDelete(selected);
            return selected != null;
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_D) {
            Neo3DGameObject selected = document.selectedObject();
            if (selected != null) {
                Neo3DGameObject copy = document.duplicate(selected.getId());
                if (copy != null) document.select(copy.getId());
                refreshPanels();
            }
            return selected != null;
        }
        if (keyCode == KeyEvent.KEYCODE_1) setTransformTool(0);
        else if (keyCode == KeyEvent.KEYCODE_2) setTransformTool(1);
        else if (keyCode == KeyEvent.KEYCODE_3) setTransformTool(2);
        else if (keyCode == KeyEvent.KEYCODE_4) setTransformTool(3);
        else return super.onKeyDown(keyCode, event);
        return true;
    }

    private static final class HierarchyRow {
        final LinearLayout box;
        final Button button;
        final CheckBox check;
        HierarchyRow(LinearLayout box, Button button, CheckBox check) {
            this.box = box;
            this.button = button;
            this.check = check;
        }
    }

    private void refreshHierarchy() {
        if (hierarchyPanel == null) return;
        if (pasteButton != null) {
            pasteButton.setVisibility(document != null && document.hasClipboard()
                    ? View.VISIBLE : View.GONE);
        }
        Object tag = hierarchyPanel.getTag();
        if (!(tag instanceof LinearLayout)) return;
        LinearLayout items = (LinearLayout) tag;
        List<Neo3DGameObject> objects = document.objects();
        Map<String, Neo3DPersistedObject> dataByName = new HashMap<>();
        for (Neo3DGameObject object : objects) {
            Neo3DPersistedObject data = document.data(object.getId());
            if (data != null) dataByName.put(object.getName(), data);
        }
        List<Neo3DGameObject> visible = objects;
        if (!hierarchyQuery.isEmpty()) {
            java.util.Set<String> keep = new java.util.HashSet<>();
            for (Neo3DGameObject object : objects) {
                if (object.getName().toLowerCase(java.util.Locale.ROOT).contains(hierarchyQuery)) {
                    keep.add(object.getName());
                    Neo3DPersistedObject data = document.data(object.getId());
                    String parentName = data == null ? null : data.parentName;
                    int guard = 0;
                    while (parentName != null && !parentName.isEmpty() && guard++ < 32
                            && keep.add(parentName)) {
                        Neo3DPersistedObject parentData = dataByName.get(parentName);
                        parentName = parentData == null ? null : parentData.parentName;
                    }
                }
            }
            visible = new ArrayList<>();
            for (Neo3DGameObject object : objects) {
                if (keep.contains(object.getName())) visible.add(object);
            }
        }
        int index = 0;
        Map<String, Integer> depthCache = new HashMap<>();
        Map<String, String> labelCache = new HashMap<>();
        for (Neo3DGameObject object : visible) {
            Neo3DPersistedObject data = document.data(object.getId());
            depthCache.put(object.getName(),
                    computeDepth(object.getName(), dataByName, depthCache, null));
            String type = data == null ? "" : data.camera ? getString(R.string.editor_3d_type_camera)
                    : data.lightType != null ? getString(R.string.editor_3d_type_light)
                    : typeName(data);
            labelCache.put(object.getName(), type + "  ·  " + object.getName());
        }
        for (Neo3DGameObject object : visible) {
            HierarchyRow holder;
            View child = index < items.getChildCount() ? items.getChildAt(index) : null;
            if (child instanceof LinearLayout && child.getTag() instanceof HierarchyRow) {
                holder = (HierarchyRow) child.getTag();
            } else {
                if (child != null) items.removeViewAt(index);
                LinearLayout rowBox = new LinearLayout(this);
                rowBox.setOrientation(LinearLayout.HORIZONTAL);
                rowBox.setGravity(android.view.Gravity.CENTER_VERTICAL);
                CheckBox multi = new CheckBox(this);
                multi.setButtonTintList(android.content.res.ColorStateList.valueOf(
                        getColor(R.color.accent)));
                Button row = new Button(this);
                row.setAllCaps(false);
                row.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);
                rowBox.addView(multi, new LinearLayout.LayoutParams(-2, -2));
                rowBox.addView(row, new LinearLayout.LayoutParams(0, dp(44), 1f));
                holder = new HierarchyRow(rowBox, row, multi);
                rowBox.setTag(holder);
                items.addView(rowBox, index, new LinearLayout.LayoutParams(-1, dp(48)));
            }
            Button row = holder.button;
            Integer depth = depthCache.get(object.getName());
            row.setPadding(dp(12 + (depth == null ? 0 : depth) * 18), 0, dp(8), 0);
            String label = labelCache.get(object.getName());
            row.setText(label == null ? object.getName()
                    : (document.isSelected(object.getId()) ? label + "  ✓" : label));
            row.setOnClickListener(view -> {
                document.select(object.getId());
                refreshPanels();
            });
            row.setOnLongClickListener(view -> {
                ClipData dragData = ClipData.newPlainText("neo3d-object", object.getId());
                view.startDragAndDrop(dragData, new View.DragShadowBuilder(view),
                        object.getId(), 0);
                return true;
            });
            row.setOnDragListener((view, event) -> {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) return true;
                if (event.getAction() == DragEvent.ACTION_DROP
                        && event.getLocalState() instanceof String) {
                    String sourceId = (String) event.getLocalState();
                    if (!sourceId.equals(object.getId())
                            && document.setParent(sourceId, object.getId())) refreshPanels();
                    return true;
                }
                return event.getAction() == DragEvent.ACTION_DRAG_ENDED;
            });
            CheckBox multi = holder.check;
            multi.setOnCheckedChangeListener(null);
            multi.setChecked(document.isSelected(object.getId()));
            multi.setOnCheckedChangeListener((button, checked) -> {
                document.toggleSelect(object.getId());
                refreshHierarchyOnly();
                refreshInspector();
            });
            index++;
        }
        while (items.getChildCount() > index) {
            items.removeViewAt(items.getChildCount() - 1);
        }
        items.setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) return true;
            if (event.getAction() == DragEvent.ACTION_DROP
                    && event.getLocalState() instanceof String) {
                if (document.setParent((String) event.getLocalState(), null)) refreshPanels();
                return true;
            }
            return event.getAction() == DragEvent.ACTION_DRAG_ENDED;
        });
    }

    private int computeDepth(String name, Map<String, Neo3DPersistedObject> dataByName,
            Map<String, Integer> cache, java.util.Set<String> visiting) {
        if (name == null) return 0;
        Integer cached = cache.get(name);
        if (cached != null) return cached;
        if (visiting == null) visiting = new java.util.HashSet<>();
        if (!visiting.add(name)) return 0;
        Neo3DPersistedObject data = dataByName.get(name);
        String parentName = data == null ? null : data.parentName;
        int depth = 0;
        if (parentName != null && !parentName.isEmpty()) {
            depth = computeDepth(parentName, dataByName, cache, visiting) + 1;
            if (depth > 32) depth = 32;
        }
        visiting.remove(name);
        cache.put(name, depth);
        return depth;
    }

    private void refreshInspector() {
        if (pendingLiveApply != null) {
            transformHandler.removeCallbacks(pendingLiveApply);
            pendingLiveApply.run();
            pendingLiveApply = null;
        }
        if (inspectorContent == null) return;
        inspectorContent.removeAllViews();
        Neo3DGameObject object = document.selectedObject();
        Neo3DPersistedObject data = object == null ? null : document.data(object.getId());
        if (object == null || data == null) {
            inspectorContent.addView(heading(R.string.editor_3d_inspector_empty));
            return;
        }
        if (document.selectionCount() > 1) {
            addMultiEditor();
            return;
        }
        addNameEditor(object);
        addTransformEditor(object);
        addKeyframeEditor(object, data);
        addMaterialEditor(object, data);
        addPhysicsEditor(object, data);
        addAnimationEditor(object);
        if (data.camera) addCameraEditor(object, data);
        if (data.lightType != null) addLightEditor(object, data);
        addParentEditor(object);
        addObjectActions(object);
    }

    private void addMultiEditor() {
        java.util.List<Neo3DGameObject> selected = document.selectedObjects();
        TextView multiTitle = new TextView(this);
        multiTitle.setText(getString(R.string.editor_3d_selected_count, selected.size()));
        multiTitle.setTextColor(getColor(R.color.accent));
        multiTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        multiTitle.setPadding(0, dp(12), 0, dp(4));
        inspectorContent.addView(multiTitle);
        CheckBox visible = checkBox(R.string.editor_3d_visible, true);
        visible.setOnCheckedChangeListener((button, checked) -> {
            for (Neo3DGameObject item : document.selectedObjects()) {
                item.setVisible(checked);
                document.sync(item.getId());
            }
            refreshHierarchyOnly();
        });
        inspectorContent.addView(visible);
        CheckBox active = checkBox(R.string.editor_3d_active, true);
        active.setOnCheckedChangeListener((button, checked) -> {
            for (Neo3DGameObject item : document.selectedObjects()) {
                item.setActive(checked);
                document.sync(item.getId());
            }
            refreshHierarchyOnly();
        });
        inspectorContent.addView(active);
        LinearLayout row1 = new LinearLayout(this);
        Button duplicateAll = new Button(this);
        duplicateAll.setText(R.string.editor_3d_duplicate);
        duplicateAll.setOnClickListener(view -> {
            java.util.List<String> fresh = new java.util.ArrayList<>();
            for (Neo3DGameObject item : document.selectedObjects()) {
                Neo3DGameObject copy = document.duplicate(item.getId());
                if (copy != null) fresh.add(copy.getId());
            }
            document.clearSelection();
            for (String id : fresh) document.toggleSelect(id);
            refreshPanels();
        });
        Button deleteAll = new Button(this);
        deleteAll.setText(R.string.editor_3d_delete_object);
        deleteAll.setOnClickListener(view -> confirmDeleteSelected());
        row1.addView(duplicateAll, new LinearLayout.LayoutParams(0, -2, 1f));
        row1.addView(deleteAll, new LinearLayout.LayoutParams(0, -2, 1f));
        inspectorContent.addView(row1);
        LinearLayout row2 = new LinearLayout(this);
        Button copy = new Button(this);
        copy.setText(R.string.editor_3d_copy);
        copy.setOnClickListener(view -> {
            document.copySelectedToClipboard();
            refreshHierarchyOnly();
            Toast.makeText(this, getString(R.string.editor_3d_copied,
                    document.selectionCount()), Toast.LENGTH_SHORT).show();
        });
        Button paste = new Button(this);
        paste.setText(R.string.editor_3d_paste);
        paste.setEnabled(document.hasClipboard());
        paste.setOnClickListener(view -> {
            java.util.List<Neo3DGameObject> pasted = document.pasteClipboard();
            if (!pasted.isEmpty()) {
                document.clearSelection();
                for (Neo3DGameObject item : pasted) document.toggleSelect(item.getId());
                refreshPanels();
                Toast.makeText(this, getString(R.string.editor_3d_pasted, pasted.size()),
                        Toast.LENGTH_SHORT).show();
            }
        });
        row2.addView(copy, new LinearLayout.LayoutParams(0, -2, 1f));
        row2.addView(paste, new LinearLayout.LayoutParams(0, -2, 1f));
        inspectorContent.addView(row2);
    }

    private void confirmDeleteSelected() {
        int count = document.selectionCount();
        if (count == 0) return;
        new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_delete_object)
                .setMessage(getString(R.string.editor_3d_delete_selected_confirm, count))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    for (Neo3DGameObject item : document.selectedObjects()) {
                        document.remove(item.getId());
                    }
                    document.clearSelection();
                    refreshPanels();
                }).show();
    }

    private void addKeyframeEditor(Neo3DGameObject object, Neo3DPersistedObject data) {
        inspectorContent.addView(section(R.string.editor_3d_keyframes));
        if (data.keyframes == null) data.keyframes = new ArrayList<>();
        sortKeyframes(data);
        CheckBox loop = checkBox(R.string.editor_3d_keyframe_loop, data.keyframeLoop);
        loop.setOnCheckedChangeListener((button, checked) -> {
            data.keyframeLoop = checked;
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
        inspectorContent.addView(loop);
        for (Neo3DPersistedObject.Neo3DKeyframe frame : new ArrayList<>(data.keyframes)) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            TextView label = new TextView(this);
            label.setText(getString(R.string.editor_3d_keyframe_time, frame.time));
            label.setTextColor(Color.WHITE);
            row.addView(label, new LinearLayout.LayoutParams(0, -2, 1f));
            Button delete = new Button(this);
            delete.setText(R.string.delete);
            delete.setAllCaps(false);
            delete.setOnClickListener(view -> {
                if (data.keyframes != null) data.keyframes.remove(frame);
                stopKeyframe(object.getId(), true);
                document.sync(object.getId());
                refreshPanels();
            });
            row.addView(delete, new LinearLayout.LayoutParams(-2, -2));
            inspectorContent.addView(row);
        }
        LinearLayout controls = new LinearLayout(this);
        Button addKey = new Button(this);
        addKey.setText(R.string.editor_3d_add_keyframe);
        addKey.setAllCaps(false);
        addKey.setOnClickListener(view -> {
            if (data.keyframes == null) data.keyframes = new ArrayList<>();
            Neo3DPersistedObject.Neo3DKeyframe frame =
                    new Neo3DPersistedObject.Neo3DKeyframe();
            float[] pos = object.getTransform().getPosition();
            float[] rot = object.getTransform().getEulerDeg();
            float[] scl = object.getTransform().getScale();
            frame.time = keyframeDuration(data) + 1f;
            frame.x = pos[0]; frame.y = pos[1]; frame.z = pos[2];
            frame.yawDeg = rot[0]; frame.pitchDeg = rot[1]; frame.rollDeg = rot[2];
            frame.scaleX = scl[0]; frame.scaleY = scl[1]; frame.scaleZ = scl[2];
            data.keyframes.add(frame);
            sortKeyframes(data);
            document.sync(object.getId());
            refreshPanels();
        });
        Button play = new Button(this);
        play.setText(R.string.editor_3d_play);
        play.setAllCaps(false);
        play.setOnClickListener(view -> startKeyframe(object.getId()));
        Button stop = new Button(this);
        stop.setText(R.string.editor_3d_stop);
        stop.setAllCaps(false);
        stop.setOnClickListener(view -> {
            stopKeyframe(object.getId(), true);
            refreshPanels();
        });
        controls.addView(addKey, new LinearLayout.LayoutParams(0, -2, 1f));
        controls.addView(play, new LinearLayout.LayoutParams(0, -2, 1f));
        controls.addView(stop, new LinearLayout.LayoutParams(0, -2, 1f));
        inspectorContent.addView(controls, fullWidth());
    }

    private void sortKeyframes(Neo3DPersistedObject data) {
        if (data.keyframes == null) return;
        java.util.Collections.sort(data.keyframes,
                (a, b) -> Float.compare(a == null ? 0f : a.time, b == null ? 0f : b.time));
        data.keyframes.removeAll(java.util.Collections.singleton(null));
    }

    private float keyframeDuration(Neo3DPersistedObject data) {
        if (data.keyframes == null || data.keyframes.isEmpty()) return 0f;
        return data.keyframes.get(data.keyframes.size() - 1).time;
    }

    private void startKeyframe(String objectId) {
        Neo3DGameObject object = document.scene().getObject(objectId);
        if (object == null) return;
        Neo3DPersistedObject data = document.data(objectId);
        if (data == null || data.keyframes == null || data.keyframes.isEmpty()) return;
        KeyframePlayback player = new KeyframePlayback();
        float[] pos = object.getTransform().getPosition();
        float[] rot = object.getTransform().getEulerDeg();
        float[] scl = object.getTransform().getScale();
        System.arraycopy(pos, 0, player.position, 0, 3);
        System.arraycopy(rot, 0, player.rotation, 0, 3);
        System.arraycopy(scl, 0, player.scale, 0, 3);
        player.time = 0f;
        keyPlayers.put(objectId, player);
    }

    private void stopKeyframe(String objectId, boolean restore) {
        KeyframePlayback player = keyPlayers.remove(objectId);
        if (restore && player != null) {
            Neo3DGameObject object = document.scene().getObject(objectId);
            if (object != null) {
                object.getTransform().setPosition(
                        player.position[0], player.position[1], player.position[2]);
                object.getTransform().setRotationEulerDeg(
                        player.rotation[0], player.rotation[1], player.rotation[2]);
                object.getTransform().setScale(
                        player.scale[0], player.scale[1], player.scale[2]);
                document.engine().syncObject(document.scene().getId(), objectId);
            }
        }
    }

    private void tickKeyframes(float delta) {
        if (keyPlayers.isEmpty()) return;
        for (String objectId : new ArrayList<>(keyPlayers.keySet())) {
            KeyframePlayback player = keyPlayers.get(objectId);
            Neo3DGameObject object = document.scene().getObject(objectId);
            Neo3DPersistedObject data = document.data(objectId);
            if (player == null || object == null || data == null
                    || data.keyframes == null || data.keyframes.isEmpty()) {
                keyPlayers.remove(objectId);
                continue;
            }
            float duration = keyframeDuration(data);
            player.time += delta;
            if (duration <= 0f) continue;
            if (player.time >= duration) {
                if (data.keyframeLoop && duration > 0f) {
                    player.time %= duration;
                } else {
                    sampleKeyframes(data, duration, object);
                    document.engine().syncObject(document.scene().getId(), objectId);
                    stopKeyframe(objectId, true);
                    refreshPanels();
                    continue;
                }
            }
            sampleKeyframes(data, player.time, object);
            document.engine().syncObject(document.scene().getId(), objectId);
        }
    }

    private void sampleKeyframes(Neo3DPersistedObject data,
            float time, Neo3DGameObject object) {
        java.util.List<Neo3DPersistedObject.Neo3DKeyframe> frames = data.keyframes;
        Neo3DPersistedObject.Neo3DKeyframe first = frames.get(0);
        Neo3DPersistedObject.Neo3DKeyframe last = frames.get(frames.size() - 1);
        if (frames.size() == 1 || time <= first.time) {
            object.getTransform().setPosition(first.x, first.y, first.z);
            object.getTransform().setRotationEulerDeg(
                    first.yawDeg, first.pitchDeg, first.rollDeg);
            object.getTransform().setScale(first.scaleX, first.scaleY, first.scaleZ);
            return;
        }
        if (time >= last.time) {
            object.getTransform().setPosition(last.x, last.y, last.z);
            object.getTransform().setRotationEulerDeg(
                    last.yawDeg, last.pitchDeg, last.rollDeg);
            object.getTransform().setScale(last.scaleX, last.scaleY, last.scaleZ);
            return;
        }
        Neo3DPersistedObject.Neo3DKeyframe before = first;
        Neo3DPersistedObject.Neo3DKeyframe after = last;
        for (int i = 0; i < frames.size() - 1; i++) {
            if (frames.get(i).time <= time && frames.get(i + 1).time >= time) {
                before = frames.get(i);
                after = frames.get(i + 1);
                break;
            }
        }
        float span = after.time - before.time;
        float f = span <= 0f ? 0f : (time - before.time) / span;
        object.getTransform().setPosition(
                before.x + (after.x - before.x) * f,
                before.y + (after.y - before.y) * f,
                before.z + (after.z - before.z) * f);
        object.getTransform().setRotationEulerDeg(
                before.yawDeg + (after.yawDeg - before.yawDeg) * f,
                before.pitchDeg + (after.pitchDeg - before.pitchDeg) * f,
                before.rollDeg + (after.rollDeg - before.rollDeg) * f);
        object.getTransform().setScale(
                before.scaleX + (after.scaleX - before.scaleX) * f,
                before.scaleY + (after.scaleY - before.scaleY) * f,
                before.scaleZ + (after.scaleZ - before.scaleZ) * f);
    }

    private void addAnimationEditor(Neo3DGameObject object) {
        List<org.catrobat.catroid.neo3d.Neo3DAnimationClip> clips =
                new ArrayList<>(object.getAnimationClips());
        if (clips.isEmpty()) return;
        inspectorContent.addView(section(R.string.editor_3d_animations));
        List<String> labels = new ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0; i < clips.size(); i++) {
            String name = clips.get(i).getName();
            labels.add(name == null || name.isEmpty()
                    ? getString(R.string.editor_3d_animation_number, i + 1) : name);
            if (object.getAnimationState() != null
                    && clips.get(i).getName().equals(object.getAnimationState().getClipName())) {
                selectedIndex = i;
            }
        }
        Spinner chooser = new Spinner(this);
        ArrayAdapter<String> chooserAdapter = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item_white_text, labels);
        chooserAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white_text);
        chooser.setAdapter(chooserAdapter);
        chooser.setSelection(selectedIndex);
        inspectorContent.addView(chooser, fullWidth());
        SeekBar seek = new SeekBar(this);
        seek.setMax(1000);
        org.catrobat.catroid.neo3d.Neo3DAnimationClip.State state = object.getAnimationState();
        if (state != null && state.getClip().getDurationSec() > 0f) {
            seek.setProgress(Math.round(state.getTimeSec() / state.getClip().getDurationSec() * 1000f));
        }
        inspectorContent.addView(seek, fullWidth());
        LinearLayout controls = new LinearLayout(this);
        Button play = new Button(this);
        Button pause = new Button(this);
        Button stop = new Button(this);
        play.setText(R.string.editor_3d_play);
        pause.setText(R.string.editor_3d_pause);
        stop.setText(R.string.editor_3d_stop);
        play.setAllCaps(false);
        pause.setAllCaps(false);
        stop.setAllCaps(false);
        controls.addView(play, new LinearLayout.LayoutParams(0, dp(48), 1f));
        controls.addView(pause, new LinearLayout.LayoutParams(0, dp(48), 1f));
        controls.addView(stop, new LinearLayout.LayoutParams(0, dp(48), 1f));
        inspectorContent.addView(controls, fullWidth());
        chooser.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                    int position, long id) {
                object.playAnimation(clips.get(position).getName());
                seek.setProgress(0);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
        play.setOnClickListener(view -> object.playAnimation(
                clips.get(chooser.getSelectedItemPosition()).getName()));
        pause.setOnClickListener(view -> object.stopAnimation());
        stop.setOnClickListener(view -> {
            object.playAnimation(clips.get(chooser.getSelectedItemPosition()).getName());
            object.stopAnimation();
            seek.setProgress(0);
        });
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int progress, boolean user) {
                if (!user) return;
                object.playAnimation(clips.get(chooser.getSelectedItemPosition()).getName());
                org.catrobat.catroid.neo3d.Neo3DAnimationClip.State current = object.getAnimationState();
                current.setPlaying(false);
                current.seek(current.getClip().getDurationSec() * progress / 1000f);
            }
            @Override public void onStartTrackingTouch(SeekBar bar) { object.stopAnimation(); }
            @Override public void onStopTrackingTouch(SeekBar bar) { }
        });
    }

    private void addNameEditor(Neo3DGameObject object) {
        inspectorContent.addView(section(R.string.editor_3d_name));
        EditText name = input(object.getName(), InputType.TYPE_CLASS_TEXT);
        inspectorContent.addView(name, fullWidth());
        addApplyButton(inspectorContent, R.string.editor_3d_rename,
                () -> {
                    document.rename(object.getId(), name.getText().toString().trim());
                    refreshPanels();
                });
        CheckBox visible = checkBox(R.string.editor_3d_visible, object.isVisible());
        visible.setOnCheckedChangeListener((button, checked) -> {
            object.setVisible(checked);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
        inspectorContent.addView(visible);
        CheckBox active = checkBox(R.string.editor_3d_active, object.isActive());
        active.setOnCheckedChangeListener((button, checked) -> {
            object.setActive(checked);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
        inspectorContent.addView(active);
    }

    private void addTransformEditor(Neo3DGameObject object) {
        inspectorContent.addView(section(R.string.editor_3d_transform));
        float[] pos = object.getTransform().getPosition();
        float[] rot = object.getTransform().getEulerDeg();
        float[] scale = object.getTransform().getScale();
        EditText[] fields = new EditText[9];
        addAxisRow(R.string.editor_3d_position, new String[]{"X", "Y", "Z"}, pos, fields, 0);
        addAxisRow(R.string.editor_3d_rotation, new String[]{"Yaw", "Pitch", "Roll"}, rot, fields, 3);
        addAxisRow(R.string.editor_3d_scale, new String[]{"X", "Y", "Z"}, scale, fields, 6);
        addApplyButton(inspectorContent, R.string.editor_3d_apply,
                () -> applyTransform(object, fields));
        attachLiveTransform(object, fields);
    }

    private void attachLiveTransform(Neo3DGameObject object, EditText[] fields) {
        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (pendingLiveApply != null) transformHandler.removeCallbacks(pendingLiveApply);
                pendingLiveApply = () -> {
                    float[] position = new float[3];
                    float[] rotation = new float[3];
                    float[] liveScale = new float[3];
                    for (int i = 0; i < 3; i++) {
                        position[i] = snapf(number(fields[i], 0f));
                        rotation[i] = number(fields[i + 3], 0f);
                        liveScale[i] = number(fields[i + 6], 1f);
                    }
                    object.getTransform().setPosition(position[0], position[1], position[2]);
                    object.getTransform().setRotationEulerDeg(rotation[0], rotation[1], rotation[2]);
                    object.getTransform().setScale(liveScale[0], liveScale[1], liveScale[2]);
                    document.sync(object.getId());
                };
                transformHandler.postDelayed(pendingLiveApply, 400L);
            }
        };
        for (EditText field : fields) {
            if (field != null) field.addTextChangedListener(watcher);
        }
    }

    private void addAxisRow(int title, String[] axes, float[] values, EditText[] fields, int offset) {
        inspectorContent.addView(section(title));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 3; i++) {
            LinearLayout cell = new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);
            TextView label = new TextView(this);
            label.setText(axes[i]); label.setTextColor(Color.LTGRAY);
            EditText value = input(Float.toString(values[i]), InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            fields[offset + i] = value;
            cell.addView(label); cell.addView(value);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
            params.setMargins(dp(2), 0, dp(2), 0);
            row.addView(cell, params);
        }
        inspectorContent.addView(row);
    }

    private void applyTransform(Neo3DGameObject object, EditText[] fields) {
        float[] position = new float[3], rotation = new float[3], scale = new float[3];
        for (int i = 0; i < 3; i++) {
            position[i] = snapf(number(fields[i], 0f));
            rotation[i] = number(fields[i + 3], 0f);
            scale[i] = number(fields[i + 6], 1f);
        }
        object.getTransform().setPosition(position[0], position[1], position[2]);
        object.getTransform().setRotationEulerDeg(rotation[0], rotation[1], rotation[2]);
        object.getTransform().setScale(scale[0], scale[1], scale[2]);
        document.sync(object.getId());
        refreshHierarchyOnly();
    }

    private void addMaterialEditor(Neo3DGameObject object, Neo3DPersistedObject data) {
        if (data.camera || data.lightType != null) return;
        inspectorContent.addView(section(R.string.editor_3d_material));
        EditText color = input(String.format("#%02X%02X%02X", Math.round(data.colorRed * 255),
                Math.round(data.colorGreen * 255), Math.round(data.colorBlue * 255)),
                InputType.TYPE_CLASS_TEXT);
        EditText metallic = labeledInput(R.string.editor_3d_metallic, data.metallic);
        EditText roughness = labeledInput(R.string.editor_3d_roughness, data.roughness);
        EditText emissive = input(String.format(Locale.ROOT, "#%02X%02X%02X",
                Math.round(data.emissiveRed * 255f), Math.round(data.emissiveGreen * 255f),
                Math.round(data.emissiveBlue * 255f)), InputType.TYPE_CLASS_TEXT);
        emissive.setHint(R.string.editor_3d_emissive);
        inspectorContent.addView(color, fullWidth());
        inspectorContent.addView(metallic, fullWidth());
        inspectorContent.addView(roughness, fullWidth());
        inspectorContent.addView(emissive, fullWidth());
        addApplyButton(inspectorContent, R.string.editor_3d_apply, () -> {
            try {
                int parsed = Color.parseColor(color.getText().toString().trim());
                int parsedEmissive = Color.parseColor(emissive.getText().toString().trim());
                data.colorRed = Color.red(parsed) / 255f;
                data.colorGreen = Color.green(parsed) / 255f;
                data.colorBlue = Color.blue(parsed) / 255f;
                data.metallic = clamp(number(metallic, data.metallic), 0f, 1f);
                data.roughness = clamp(number(roughness, data.roughness), 0f, 1f);
                data.emissiveRed = Color.red(parsedEmissive) / 255f;
                data.emissiveGreen = Color.green(parsedEmissive) / 255f;
                data.emissiveBlue = Color.blue(parsedEmissive) / 255f;
                document.sync(object.getId());
                refreshHierarchyOnly();
                Toast.makeText(this, R.string.editor_3d_material_applied, Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                color.setError(getString(R.string.editor_3d_color_invalid));
            }
        });
    }

    private void addPhysicsEditor(Neo3DGameObject object, Neo3DPersistedObject data) {
        if (data.camera || data.lightType != null) return;
        inspectorContent.addView(section(R.string.editor_3d_physics));
        CheckBox enabled = checkBox(R.string.editor_3d_physics_enabled,
                data.physicsMotionType >= 0);
        inspectorContent.addView(enabled);
        Spinner motion = spinner(R.array.editor_3d_motion_entries);
        Spinner shape = spinner(R.array.editor_3d_shape_entries);
        if (data.physicsMotionType >= 0) motion.setSelection(data.physicsMotionType);
        shape.setSelection(Math.max(0, Math.min(data.physicsShapeType,
                getResources().getStringArray(R.array.editor_3d_shape_entries).length - 1)));
        inspectorContent.addView(labelled(R.string.editor_3d_motion, motion));
        inspectorContent.addView(labelled(R.string.editor_3d_shape, shape));
        EditText mass = labeledInput(R.string.editor_3d_mass, data.physicsMass);
        EditText friction = labeledInput(R.string.editor_3d_friction, data.physicsFriction);
        EditText restitution = labeledInput(R.string.editor_3d_restitution, data.physicsRestitution);
        EditText gravity = labeledInput(R.string.editor_3d_gravity, data.physicsGravityFactor);
        EditText linearDamping = labeledInput(R.string.editor_3d_linear_damping,
                data.physicsLinearDamping);
        EditText angularDamping = labeledInput(R.string.editor_3d_angular_damping,
                data.physicsAngularDamping);
        CheckBox continuous = checkBox(R.string.editor_3d_continuous_collision,
                data.physicsContinuousCollision);
        inspectorContent.addView(mass, fullWidth());
        inspectorContent.addView(friction, fullWidth());
        inspectorContent.addView(restitution, fullWidth());
        inspectorContent.addView(gravity, fullWidth());
        inspectorContent.addView(linearDamping, fullWidth());
        inspectorContent.addView(angularDamping, fullWidth());
        inspectorContent.addView(continuous);
        EditText[] colliderSize = new EditText[3];
        addAxisRow(R.string.editor_3d_collider_size,
                new String[]{"X", "Y", "Z"},
                new float[]{data.physicsShapeScaleX, data.physicsShapeScaleY,
                        data.physicsShapeScaleZ},
                colliderSize, 0);
        addApplyButton(inspectorContent, R.string.editor_3d_apply, () -> {
            data.physicsMotionType = enabled.isChecked() ? motion.getSelectedItemPosition() : -1;
            data.physicsShapeType = shape.getSelectedItemPosition();
            data.physicsMass = Math.max(0.001f, number(mass, data.physicsMass));
            data.physicsFriction = clamp(number(friction, data.physicsFriction), 0f, 2f);
            data.physicsRestitution = clamp(number(restitution, data.physicsRestitution), 0f, 1f);
            data.physicsGravityFactor = clamp(number(gravity, data.physicsGravityFactor), 0f, 100f);
            data.physicsLinearDamping = clamp(number(linearDamping,
                    data.physicsLinearDamping), 0f, 10f);
            data.physicsAngularDamping = clamp(number(angularDamping,
                    data.physicsAngularDamping), 0f, 10f);
            data.physicsContinuousCollision = continuous.isChecked();
            data.physicsShapeScaleX = clamp(number(colliderSize[0],
                    data.physicsShapeScaleX), 0.01f, 100f);
            data.physicsShapeScaleY = clamp(number(colliderSize[1],
                    data.physicsShapeScaleY), 0.01f, 100f);
            data.physicsShapeScaleZ = clamp(number(colliderSize[2],
                    data.physicsShapeScaleZ), 0.01f, 100f);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
    }

    private void addCameraEditor(Neo3DGameObject object, Neo3DPersistedObject data) {
        inspectorContent.addView(section(R.string.editor_3d_camera));
        EditText fov = labeledInput(R.string.editor_3d_fov, data.cameraFov);
        EditText near = labeledInput(R.string.editor_3d_near, data.cameraNear);
        EditText far = labeledInput(R.string.editor_3d_far, data.cameraFar);
        EditText aperture = labeledInput(R.string.editor_3d_aperture, data.cameraAperture);
        EditText shutter = labeledInput(R.string.editor_3d_shutter, data.cameraShutterSpeed);
        EditText sensitivity = labeledInput(R.string.editor_3d_sensitivity,
                data.cameraSensitivity);
        inspectorContent.addView(fov, fullWidth()); inspectorContent.addView(near, fullWidth());
        inspectorContent.addView(far, fullWidth());
        inspectorContent.addView(aperture, fullWidth());
        inspectorContent.addView(shutter, fullWidth());
        inspectorContent.addView(sensitivity, fullWidth());
        CheckBox main = checkBox(R.string.editor_3d_main_camera, data.cameraMain);
        inspectorContent.addView(main);
        addApplyButton(inspectorContent, R.string.editor_3d_apply, () -> {
            data.cameraFov = clamp(number(fov, data.cameraFov), 10f, 120f);
            data.cameraNear = Math.max(0.01f, number(near, data.cameraNear));
            data.cameraFar = Math.max(data.cameraNear + 1f, number(far, data.cameraFar));
            data.cameraAperture = clamp(number(aperture, data.cameraAperture), 0.7f, 64f);
            data.cameraShutterSpeed = clamp(number(shutter, data.cameraShutterSpeed),
                    0.0001f, 10f);
            data.cameraSensitivity = clamp(number(sensitivity, data.cameraSensitivity),
                    1f, 102400f);
            if (main.isChecked()) {
                for (Neo3DGameObject item : document.objects()) {
                    Neo3DPersistedObject other = document.data(item.getId());
                    if (other.camera && other != data) {
                        other.cameraMain = false;
                        item.getCamera().setMainCamera(false);
                    }
                }
            }
            data.cameraMain = main.isChecked();
            object.getCamera().setMainCamera(data.cameraMain);
            object.getCamera().setFovDeg(data.cameraFov);
            object.getCamera().setNear(data.cameraNear);
            object.getCamera().setFar(data.cameraFar);
            object.getCamera().setExposure(data.cameraAperture, data.cameraShutterSpeed,
                    data.cameraSensitivity);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
    }

    private void addLightEditor(Neo3DGameObject object, Neo3DPersistedObject data) {
        inspectorContent.addView(section(R.string.editor_3d_light));
        Spinner type = spinner(R.array.editor_3d_light_type_entries);
        type.setSelection("POINT".equals(data.lightType) ? 1 : "SPOT".equals(data.lightType) ? 2 : 0);
        EditText intensity = labeledInput(R.string.editor_3d_intensity, data.lightIntensity);
        EditText range = labeledInput(R.string.editor_3d_range, data.lightRange);
        EditText color = input(String.format(Locale.ROOT, "#%02X%02X%02X",
                Math.round(data.lightRed * 255f), Math.round(data.lightGreen * 255f),
                Math.round(data.lightBlue * 255f)), InputType.TYPE_CLASS_TEXT);
        color.setHint(R.string.editor_3d_light_color_hex);
        EditText[] direction = new EditText[3];
        EditText innerCone = labeledInput(R.string.editor_3d_inner_cone,
                data.lightInnerConeDeg);
        EditText outerCone = labeledInput(R.string.editor_3d_outer_cone,
                data.lightOuterConeDeg);
        CheckBox shadows = checkBox(R.string.editor_3d_shadows, data.lightShadows);
        inspectorContent.addView(labelled(R.string.editor_3d_light_type, type));
        inspectorContent.addView(intensity, fullWidth()); inspectorContent.addView(range, fullWidth());
        inspectorContent.addView(color, fullWidth());
        inspectorContent.addView(innerCone, fullWidth());
        inspectorContent.addView(outerCone, fullWidth());
        addAxisRow(R.string.editor_3d_light_direction, new String[]{"X", "Y", "Z"},
                new float[]{data.lightDirectionX, data.lightDirectionY, data.lightDirectionZ},
                direction, 0);
        inspectorContent.addView(shadows);
        addApplyButton(inspectorContent, R.string.editor_3d_apply, () -> {
            String[] types = {"DIRECTIONAL", "POINT", "SPOT"};
            data.lightType = types[type.getSelectedItemPosition()];
            data.lightIntensity = Math.max(0f, number(intensity, data.lightIntensity));
            data.lightRange = Math.max(0.1f, number(range, data.lightRange));
            data.lightShadows = shadows.isChecked();
            try {
                int parsed = Color.parseColor(color.getText().toString().trim());
                data.lightRed = Color.red(parsed) / 255f;
                data.lightGreen = Color.green(parsed) / 255f;
                data.lightBlue = Color.blue(parsed) / 255f;
            } catch (IllegalArgumentException e) {
                color.setError(getString(R.string.editor_3d_color_invalid));
                return;
            }
            data.lightDirectionX = number(direction[0], data.lightDirectionX);
            data.lightDirectionY = number(direction[1], data.lightDirectionY);
            data.lightDirectionZ = number(direction[2], data.lightDirectionZ);
            data.lightInnerConeDeg = clamp(number(innerCone, data.lightInnerConeDeg), 0f, 90f);
            data.lightOuterConeDeg = clamp(number(outerCone, data.lightOuterConeDeg),
                    data.lightInnerConeDeg, 90f);
            Neo3DLight light = new Neo3DLight(
                    Neo3DLight.Type.valueOf(data.lightType), data.lightIntensity);
            light.setColor(data.lightRed, data.lightGreen, data.lightBlue);
            light.setRange(data.lightRange);
            light.setCastShadows(data.lightShadows);
            light.setDirection(data.lightDirectionX, data.lightDirectionY,
                    data.lightDirectionZ);
            light.setConeDeg(data.lightInnerConeDeg, data.lightOuterConeDeg);
            object.setLight(light);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
    }

    private void addParentEditor(Neo3DGameObject object) {
        inspectorContent.addView(section(R.string.editor_3d_parent));
        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        names.add(getString(R.string.editor_3d_no_parent)); ids.add("");
        for (Neo3DGameObject item : document.objects()) {
            if (item != object) { names.add(item.getName()); ids.add(item.getId()); }
        }
        Spinner parent = new Spinner(this);
        ArrayAdapter<String> parentAdapter = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item_white_text, names);
        parentAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white_text);
        parent.setAdapter(parentAdapter);
        String currentName = document.engine().getObjectParentName(document.scene().getId(), object.getId());
        int selection = names.indexOf(currentName);
        parent.setSelection(Math.max(0, selection));
        inspectorContent.addView(parent);
        addApplyButton(inspectorContent, R.string.editor_3d_apply, () -> {
            document.setParent(object.getId(), ids.get(parent.getSelectedItemPosition()));
            refreshPanels();
        });
    }

    private void addObjectActions(Neo3DGameObject object) {
        LinearLayout actions = new LinearLayout(this);
        Button duplicate = new Button(this);
        duplicate.setText(R.string.editor_3d_duplicate);
        duplicate.setOnClickListener(view -> select(document.duplicate(object.getId())));
        Button delete = new Button(this);
        delete.setText(R.string.editor_3d_delete_object);
        delete.setOnClickListener(view -> confirmDelete(object));
        actions.addView(duplicate, new LinearLayout.LayoutParams(0, -2, 1f));
        actions.addView(delete, new LinearLayout.LayoutParams(0, -2, 1f));
        inspectorContent.addView(actions);
        LinearLayout cameraActions = new LinearLayout(this);
        Button focus = new Button(this);
        focus.setText(R.string.editor_3d_focus);
        focus.setOnClickListener(view -> focusViewportOn(object));
        Button toCamera = new Button(this);
        toCamera.setText(R.string.editor_3d_move_to_camera);
        toCamera.setOnClickListener(view -> {
            Neo3DGameObject camera = document.viewportCamera();
            if (camera == null) return;
            float[] position = camera.getTransform().getPosition();
            object.getTransform().setPosition(position[0], position[1], position[2]);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
        Button faceCamera = new Button(this);
        faceCamera.setText(R.string.editor_3d_face_camera);
        faceCamera.setOnClickListener(view -> {
            Neo3DGameObject camera = document.viewportCamera();
            if (camera == null) return;
            float[] eye = object.getTransform().getPosition();
            float[] aim = camera.getTransform().getPosition();
            float[] yawPitch = Neo3DMath.yawPitchToTarget(eye, aim);
            float[] euler = object.getTransform().getEulerDeg();
            object.getTransform().setRotationEulerDeg(yawPitch[0], yawPitch[1], euler[2]);
            document.sync(object.getId());
            refreshHierarchyOnly();
        });
        cameraActions.addView(focus, new LinearLayout.LayoutParams(0, -2, 1f));
        cameraActions.addView(toCamera, new LinearLayout.LayoutParams(0, -2, 1f));
        cameraActions.addView(faceCamera, new LinearLayout.LayoutParams(0, -2, 1f));
        inspectorContent.addView(cameraActions);
    }

    private void showViewPresetsDialog() {
        String[] names = {
                getString(R.string.editor_3d_view_top),
                getString(R.string.editor_3d_view_front),
                getString(R.string.editor_3d_view_side),
                getString(R.string.editor_3d_view_iso)};
        new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_view_presets)
                .setItems(names, (dialog, which) -> applyViewPreset(which))
                .show();
    }

    private void applyViewPreset(int preset) {
        Neo3DGameObject camera = document.viewportCamera();
        if (camera == null || camera.getCamera() == null) return;
        Neo3DGameObject anchor = document.selectedObject();
        float[] target = anchor != null ? anchor.getTransform().getPosition()
                : new float[]{0f, 0f, 0f};
        float[] eye = camera.getTransform().getPosition();
        float dx = eye[0] - target[0];
        float dy = eye[1] - target[1];
        float dz = eye[2] - target[2];
        float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 2f) dist = 8f;
        if (dist > 200f) dist = 200f;
        float nx = target[0];
        float ny = target[1];
        float nz = target[2];
        float yaw = 0f;
        float pitch = 0f;
        switch (preset) {
            case 0:
                ny = target[1] + dist;
                nz = target[2] + 0.01f;
                pitch = -90f;
                break;
            case 1:
                nz = target[2] + dist;
                break;
            case 2:
                nx = target[0] + dist;
                yaw = 90f;
                break;
            default:
                nx = target[0] + dist * 0.577f;
                ny = target[1] + dist * 0.577f;
                nz = target[2] + dist * 0.577f;
                yaw = 45f;
                pitch = -35.26f;
                break;
        }
        camera.getTransform().setPosition(nx, ny, nz);
        if (camera.getCamera().isUseTransformOrientation()) {
            float[] euler = camera.getTransform().getEulerDeg();
            camera.getTransform().setRotationEulerDeg(yaw, pitch, euler[2]);
        } else {
            camera.getCamera().setLookAtTarget(target[0], target[1], target[2]);
        }
        document.engine().syncObject(document.scene().getId(), camera.getId());
    }

    private void focusViewportOn(Neo3DGameObject object) {        Neo3DGameObject camera = document.viewportCamera();
        if (camera == null || camera.getCamera() == null) return;
        float[] target = object.getTransform().getPosition();
        float[] eye = {target[0] + 4f, target[1] + 4f, target[2] + 4f};
        camera.getTransform().setPosition(eye[0], eye[1], eye[2]);
        if (camera.getCamera().isUseTransformOrientation()) {
            float[] yawPitch = Neo3DMath.yawPitchToTarget(eye, target);
            float[] euler = camera.getTransform().getEulerDeg();
            camera.getTransform().setRotationEulerDeg(yawPitch[0], yawPitch[1], euler[2]);
        } else {
            camera.getCamera().setLookAtTarget(target[0], target[1], target[2]);
        }
        document.engine().syncObject(document.scene().getId(), camera.getId());
        Toast.makeText(this, getString(R.string.editor_3d_focused_on, object.getName()),
                Toast.LENGTH_SHORT).show();
    }

    private void confirmDelete(Neo3DGameObject object) {
        new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_delete_object)
                .setMessage(getString(R.string.editor_3d_delete_confirm, object.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    document.remove(object.getId());
                    refreshPanels();
                }).show();
    }

    private void addPrimitive(int kind) { select(document.addPrimitive(kind)); }
    private void select(Neo3DGameObject object) {
        if (object == null || object.getId().equals(document.selectedId())) return;
        document.select(object.getId());
        refreshPanels();
    }

    private void showLightChoices() {
        String[] options = {getString(R.string.editor_3d_directional_light),
                getString(R.string.editor_3d_point_light), getString(R.string.editor_3d_spot_light)};
        new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_add_light)
                .setItems(options, (dialog, index) -> select(document.addLight(
                        new String[]{"DIRECTIONAL", "POINT", "SPOT"}[index])))
                .show();
    }

    private void showSceneSettingsDialog() {
        float[] currentColor = document.skyColor();
        EditText color = input(String.format(Locale.ROOT, "#%02X%02X%02X",
                Math.round(currentColor[0] * 255f), Math.round(currentColor[1] * 255f),
                Math.round(currentColor[2] * 255f)), InputType.TYPE_CLASS_TEXT);
        color.setHint(R.string.editor_3d_sky_color);
        EditText intensity = labeledInput(R.string.editor_3d_ibl_intensity,
                document.iblIntensity());
        CheckBox shadows = checkBox(R.string.editor_3d_scene_shadows,
                document.shadowsEnabled());
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(8), dp(20), dp(4));
        content.addView(color, fullWidth());
        content.addView(intensity, fullWidth());
        content.addView(shadows);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_scene_settings)
                .setView(content)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.editor_3d_apply, null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(button -> {
                    try {
                        int parsed = Color.parseColor(color.getText().toString().trim());
                        document.setSceneEnvironment(Color.red(parsed) / 255f,
                                Color.green(parsed) / 255f, Color.blue(parsed) / 255f,
                                Math.max(0f, number(intensity, document.iblIntensity())),
                                shadows.isChecked());
                        refreshPanels();
                        dialog.dismiss();
                    } catch (IllegalArgumentException e) {
                        color.setError(getString(R.string.editor_3d_color_invalid));
                    }
                }));
        dialog.show();
    }

    private void chooseModel() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"model/gltf-binary",
                "model/gltf+json", "model/obj", "model/stl", "application/zip",
                "application/octet-stream"});
        startActivityForResult(intent, REQUEST_MODEL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_MODEL || resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;
        String name = displayName(uri);
        String lowerName = name.toLowerCase(Locale.ROOT);
        if (!isSupportedModel(lowerName) && !lowerName.endsWith(".zip")) {
            Toast.makeText(this, R.string.editor_3d_unsupported_model, Toast.LENGTH_LONG).show();
            return;
        }
        statusMessage(R.string.editor_3d_import_model);
        importQueue.execute(() -> {
            try {
                String relative = lowerName.endsWith(".zip")
                        ? importModelArchive(uri) : copyImportedFile(uri, name);
                File modelFile = ProjectManager.getInstance().getCurrentProject().getFile(relative);
                String checkName = modelFile.getName().toLowerCase(Locale.ROOT);
                if ((checkName.endsWith(".glb") || checkName.endsWith(".gltf"))
                        && modelUsesCompression(modelFile, checkName)) {
                    deleteImportArtifact(modelFile);
                    runOnUiThread(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            Toast.makeText(this, R.string.editor_3d_model_compressed,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                String modelName = relative.substring(relative.lastIndexOf('/') + 1);
                runOnUiThread(() -> {
                    if (destroyed || isFinishing() || isDestroyed()) return;
                    select(document.addModel(modelName, relative));
                });
            } catch (IOException | IllegalArgumentException e) {
                String message = e.getMessage() == null ? "" : e.getMessage();
                final int toastRes;
                if (message.contains("too large")) {
                    toastRes = R.string.editor_3d_model_too_large;
                } else if (message.contains("exactly one")) {
                    toastRes = R.string.editor_3d_model_no_scene_model;
                } else {
                    toastRes = R.string.editor_3d_model_import_failed;
                }
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        Toast.makeText(this, toastRes, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private String displayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(0);
                if (displayName != null && !displayName.trim().isEmpty()) {
                    return displayName.replaceAll("[^A-Za-z0-9._-]", "_");
                }
            }
        } catch (RuntimeException ignored) {
        }
        String segment = uri.getLastPathSegment();
        return segment == null ? "model.glb"
                : segment.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private boolean isSupportedModel(String lowercaseName) {
        return lowercaseName.endsWith(".glb") || lowercaseName.endsWith(".gltf")
                || lowercaseName.endsWith(".obj") || lowercaseName.endsWith(".stl");
    }

    private String copyImportedFile(Uri uri, String name) throws IOException {
        String relative = "neo3d/" + System.currentTimeMillis() + "_" + name;
        File target = ProjectManager.getInstance().getCurrentProject().getFile(relative);
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create model directory");
        }
        try (InputStream input = getContentResolver().openInputStream(uri);
                FileOutputStream output = new FileOutputStream(target)) {
            if (input == null) throw new IOException("Empty model stream");
            copyLimited(input, output, 100L * 1024L * 1024L);
        }
        return relative;
    }

    private String importModelArchive(Uri uri) throws IOException {
        String folder = "neo3d/import-" + UUID.randomUUID();
        File root = ProjectManager.getInstance().getCurrentProject().getFile(folder);
        if (!root.mkdirs()) throw new IOException("Unable to create import directory");
        String rootPath = root.getCanonicalPath() + File.separator;
        List<String> models = new ArrayList<>();
        long totalBytes = 0;
        int entryCount = 0;
        try (InputStream source = getContentResolver().openInputStream(uri)) {
            if (source == null) throw new IOException("Empty archive");
            try (ZipInputStream zip = new ZipInputStream(source)) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    if (++entryCount > 1000) throw new IOException("Archive has too many files");
                    File output = new File(root, entry.getName()).getCanonicalFile();
                    if (!output.getPath().startsWith(rootPath)) {
                        throw new IOException("Archive contains an invalid path");
                    }
                    if (entry.isDirectory()) {
                        if (!output.mkdirs() && !output.isDirectory()) {
                            throw new IOException("Unable to create archive directory");
                        }
                    } else {
                        File parent = output.getParentFile();
                        if (parent != null && !parent.exists() && !parent.mkdirs()) {
                            throw new IOException("Unable to create archive directory");
                        }
                        try (FileOutputStream file = new FileOutputStream(output)) {
                            totalBytes += copyLimited(zip, file,
                                    100L * 1024L * 1024L - totalBytes);
                        }
                        String extension = output.getName().toLowerCase(Locale.ROOT);
                        if (isSupportedModel(extension)) {
                            models.add(folder + "/" + entry.getName().replace('\\', '/'));
                        }
                    }
                    zip.closeEntry();
                }
            }
        }
        if (models.size() != 1) {
            throw new IOException("Archive must contain exactly one supported model");
        }
        return models.get(0);
    }

    private long copyLimited(InputStream input, FileOutputStream output, long limit)
            throws IOException {
        if (limit < 0) throw new IOException("Import is too large");
        byte[] buffer = new byte[16384];
        long copied = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            copied += read;
            if (copied > limit) throw new IOException("Import is too large");
            output.write(buffer, 0, read);
        }
        return copied;
    }

    private static final String[] COMPRESSION_MARKERS = {
            "KHR_draco_mesh_compression", "EXT_meshopt_compression", "KHR_texture_basisu"};

    private boolean containsCompressionMarker(String text) {
        for (String marker : COMPRESSION_MARKERS) {
            if (text.contains(marker)) return true;
        }
        return false;
    }

    private boolean streamContainsMarker(java.io.InputStream input, long maxBytes) {
        int overlap = 0;
        for (String marker : COMPRESSION_MARKERS) {
            if (marker.length() > overlap) overlap = marker.length();
        }
        overlap--;
        byte[] buffer = new byte[65536 + overlap];
        int carry = 0;
        long total = 0;
        try {
            int read;
            while ((read = input.read(buffer, carry, 65536)) != -1) {
                total += read;
                if (total > maxBytes) return false;
                int available = carry + read;
                String text = new String(buffer, 0, available,
                        java.nio.charset.StandardCharsets.UTF_8);
                if (containsCompressionMarker(text)) return true;
                if (available > overlap) {
                    System.arraycopy(buffer, available - overlap, buffer, 0, overlap);
                    carry = overlap;
                } else {
                    carry = available;
                }
            }
        } catch (IOException | RuntimeException ignored) {
            return false;
        }
        return false;
    }

    private boolean modelUsesCompression(File file, String lowerName) {
        try {
            if (lowerName.endsWith(".gltf")) {
                try (java.io.FileInputStream input = new java.io.FileInputStream(file)) {
                    return streamContainsMarker(input, 64L * 1024L * 1024L);
                }
            }
            if (lowerName.endsWith(".glb")) {
                try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r")) {
                    if (raf.length() < 20) return false;
                    byte[] header = new byte[12];
                    raf.readFully(header);
                    if (header[0] != 'g' || header[1] != 'L'
                            || header[2] != 'T' || header[3] != 'F') {
                        return false;
                    }
                    long offset = 12;
                    byte[] chunkHead = new byte[8];
                    while (offset + 8 <= raf.length()) {
                        raf.seek(offset);
                        raf.readFully(chunkHead);
                        long chunkLen = (chunkHead[0] & 0xFFL)
                                | ((chunkHead[1] & 0xFFL) << 8)
                                | ((chunkHead[2] & 0xFFL) << 16)
                                | ((chunkHead[3] & 0xFFL) << 24);
                        boolean isJson = chunkHead[4] == 'J' && chunkHead[5] == 'S'
                                && chunkHead[6] == 'O' && chunkHead[7] == 'N';
                        long chunkEnd = offset + 8 + chunkLen;
                        if (chunkLen < 0 || chunkEnd < offset + 8
                                || chunkEnd > raf.length()) {
                            break;
                        }
                        if (isJson && chunkLen > 0) {
                            final long jsonStart = offset + 8;
                            final long jsonLen = chunkLen;
                            java.io.InputStream slice = new java.io.InputStream() {
                                long pos = jsonStart;
                                final long end = jsonStart + jsonLen;
                                @Override public int read() throws IOException {
                                    if (pos >= end) return -1;
                                    raf.seek(pos++);
                                    return raf.read();
                                }
                                @Override public int read(byte[] b, int off, int len)
                                        throws IOException {
                                    if (pos >= end) return -1;
                                    long capped = Math.min(len, end - pos);
                                    raf.seek(pos);
                                    int got = raf.read(b, off, (int) capped);
                                    if (got > 0) pos += got;
                                    return got;
                                }
                            };
                            return streamContainsMarker(slice, 64L * 1024L * 1024L);
                        }
                        offset = chunkEnd;
                    }
                }
            }
        } catch (IOException | RuntimeException | OutOfMemoryError ignored) {
        }
        return false;
    }

    private void deleteImportArtifact(File modelFile) {
        try {
            File projectDir = ProjectManager.getInstance().getCurrentProject().getFilesDir();
            File dir = modelFile.getParentFile();
            while (dir != null && !dir.equals(projectDir)
                    && !dir.getName().startsWith("import-")) {
                dir = dir.getParentFile();
            }
            if (dir != null && !dir.equals(projectDir)) {
                deleteRecursive(dir);
            } else {
                modelFile.delete();
            }
        } catch (RuntimeException ignored) {
        }
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) deleteRecursive(child);
            }
        }
        file.delete();
    }

    private void checkForRecovery() {
        if (document == null || !document.hasRecovery()) return;
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_crash_recovery_title)
                .setMessage(R.string.editor_3d_crash_recovery_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.editor_3d_recover_btn, (d, which) -> {
                    if (document.restoreFromRecovery()) {
                        refreshPanels();
                        Toast.makeText(this, R.string.editor_3d_recovered_success,
                                Toast.LENGTH_LONG).show();
                    } else {
                        document.discardRecovery();
                    }
                })
                .setNegativeButton(R.string.editor_3d_discard_btn, (d, which) ->
                        document.discardRecovery())
                .create();
        trackDialog(dialog);
        dialog.show();
    }

    private void maybeShowControlsDialog() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("pref_neo3d_controls_asked", false)) return;
        String[] values = getResources().getStringArray(R.array.editor_3d_controls_values);
        String[] entries = getResources().getStringArray(R.array.editor_3d_controls_entries);
        String[] descs = {
                getString(R.string.editor_3d_controls_wasd_desc),
                getString(R.string.editor_3d_controls_joystick_desc),
                getString(R.string.editor_3d_controls_gestures_desc)};
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(8), dp(20), dp(4));
        RadioGroup group = new RadioGroup(this);
        for (int i = 0; i < entries.length; i++) {
            RadioButton option = new RadioButton(this);
            option.setText(entries[i]);
            option.setTextColor(Color.WHITE);
            option.setId(100 + i);
            group.addView(option);
        }
        group.check(100);
        content.addView(group);
        TextView desc = new TextView(this);
        desc.setTextColor(Color.LTGRAY);
        desc.setPadding(0, dp(8), 0, dp(8));
        content.addView(desc);
        FrameLayout preview = new FrameLayout(this);
        android.graphics.drawable.GradientDrawable outline =
                new android.graphics.drawable.GradientDrawable();
        outline.setColor(getColor(R.color.app_background_dark));
        outline.setStroke(dp(2), getColor(R.color.accent));
        outline.setCornerRadius(dp(8));
        preview.setBackground(outline);
        preview.setPadding(dp(12), dp(12), dp(12), dp(12));
        content.addView(preview, new LinearLayout.LayoutParams(-1, dp(180)));
        Runnable updatePreview = () -> {
            preview.removeAllViews();
            int checked = group.getCheckedRadioButtonId() - 100;
            if (checked < 0 || checked >= descs.length) checked = 0;
            desc.setText(descs[checked]);
            if (checked == 1) {
                Neo3DJoystickView mock = new Neo3DJoystickView(this);
                preview.addView(mock, new FrameLayout.LayoutParams(dp(120), dp(120),
                        android.view.Gravity.CENTER));
            } else if (checked == 2) {
                TextView gestures = new TextView(this);
                gestures.setText(R.string.editor_3d_viewport_hint);
                gestures.setTextColor(Color.WHITE);
                gestures.setGravity(android.view.Gravity.CENTER);
                preview.addView(gestures, new FrameLayout.LayoutParams(-1, -1));
            } else {
                android.widget.GridLayout mock = new android.widget.GridLayout(this);
                mock.setColumnCount(3);
                String[] keys = {"Q", "W", "E", "A", "S", "D"};
                for (String key : keys) {
                    TextView cell = new TextView(this);
                    cell.setText(key);
                    cell.setTextColor(Color.WHITE);
                    cell.setGravity(android.view.Gravity.CENTER);
                    cell.setBackgroundColor(0x44FFFFFF);
                    cell.setPadding(dp(12), dp(8), dp(12), dp(8));
                    android.widget.GridLayout.LayoutParams params =
                            new android.widget.GridLayout.LayoutParams();
                    params.setMargins(dp(2), dp(2), dp(2), dp(2));
                    mock.addView(cell, params);
                }
                preview.addView(mock, new FrameLayout.LayoutParams(-2, -2,
                        android.view.Gravity.CENTER));
            }
        };
        group.setOnCheckedChangeListener((g, checkedId) -> updatePreview.run());
        updatePreview.run();
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_controls_title)
                .setView(content)
                .setCancelable(false)
                .setPositiveButton(R.string.editor_3d_controls_next, (d, which) -> {
                    int checked = group.getCheckedRadioButtonId() - 100;
                    if (checked < 0 || checked >= values.length) checked = 0;
                    prefs.edit()
                            .putString("setting_neo3d_controls", values[checked])
                            .putBoolean("pref_neo3d_controls_asked", true)
                            .apply();
                    applyControlsMode();
                    Toast.makeText(this, getString(R.string.editor_3d_controls_hint,
                            entries[checked]), Toast.LENGTH_LONG).show();
                })
                .create();
        trackDialog(dialog);
        dialog.show();
    }

    private void applyControlsMode() {
        String mode = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("setting_neo3d_controls", "wasd");
        if (wasdBar != null) {
            wasdBar.setVisibility("wasd".equals(mode) ? View.VISIBLE : View.GONE);
        }
        if (joystick != null) {
            joystick.setVisibility("joystick".equals(mode) ? View.VISIBLE : View.GONE);
            if (!"joystick".equals(mode)) joystick.reset();
        }
        camVelForward = 0f;
        camVelStrafe = 0f;
        camVelUp = 0f;
    }

    private void showObjectsDialog() {
        List<Neo3DGameObject> objects = document.objects();
        String[] names = new String[objects.size()];
        for (int i = 0; i < objects.size(); i++) names[i] = objects.get(i).getName();
        new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_hierarchy)
                .setItems(names, (dialog, which) -> select(objects.get(which))).show();
    }

    private void showInspectorDialog() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout previous = inspectorContent;
        inspectorContent = new LinearLayout(this);
        inspectorContent.setOrientation(LinearLayout.VERTICAL);
        inspectorContent.setPadding(dp(16), dp(8), dp(16), dp(8));
        scroll.addView(inspectorContent);
        refreshInspector();
        new AlertDialog.Builder(this, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_inspector).setView(scroll)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(dialog -> inspectorContent = previous).show();
    }

    private void saveAndNotify() {
        document.save(saved -> Toast.makeText(this, saved
                ? R.string.editor_3d_scene_saved_to_project
                : R.string.editor_3d_save_failed, Toast.LENGTH_SHORT).show());
    }

    private void statusMessage(int message) {
        if (status != null) status.setText(message);
    }

    private View section(int text) {
        TextView label = new TextView(this);
        label.setText(text); label.setTextColor(getColor(R.color.accent));
        label.setTypeface(null, android.graphics.Typeface.BOLD);
        label.setPadding(0, dp(12), 0, dp(4));
        return label;
    }

    private CheckBox checkBox(int text, boolean checked) {
        CheckBox box = new CheckBox(this);
        box.setText(text); box.setChecked(checked); box.setTextColor(Color.WHITE);
        return box;
    }

    private EditText labeledInput(int label, float value) {
        EditText field = input(Float.toString(value), InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        field.setHint(label);
        return field;
    }

    private EditText input(String value, int inputType) {
        EditText field = new EditText(this);
        field.setSingleLine(true); field.setText(value); field.setInputType(inputType);
        field.setTextColor(Color.WHITE); field.setHintTextColor(Color.LTGRAY);
        return field;
    }

    private Spinner spinner(int entries) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, entries,
                R.layout.simple_spinner_item_white_text);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white_text);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private View labelled(int title, View content) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView label = new TextView(this);
        label.setText(title); label.setTextColor(Color.WHITE);
        row.addView(label, new LinearLayout.LayoutParams(0, -2, 1f));
        row.addView(content, new LinearLayout.LayoutParams(0, -2, 1f));
        return row;
    }

    private void addApplyButton(LinearLayout parent, int title, Runnable apply) {
        Button button = new Button(this);
        button.setText(title); button.setAllCaps(false);
        button.setOnClickListener(view -> apply.run());
        parent.addView(button, fullWidth());
    }

    private LinearLayout.LayoutParams fullWidth() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    private float number(EditText field, float fallback) {
        try {
            float value = Float.parseFloat(field.getText().toString().trim());
            return Float.isNaN(value) || Float.isInfinite(value) ? fallback : value;
        }
        catch (NumberFormatException e) { return fallback; }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private String typeName(Neo3DPersistedObject data) {
        if ("CUBE".equals(data.objectType)) return getString(R.string.editor_3d_type_cube);
        if ("SPHERE".equals(data.objectType)) return getString(R.string.editor_3d_type_sphere);
        if ("CYLINDER".equals(data.objectType)) return getString(R.string.editor_3d_type_cylinder);
        return getString(R.string.editor_3d_type_model);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void renderFrame(long frameTimeNs) {
        if (document == null || document.engine().isDisposed()) return;
        float delta = previousFrameNs == 0 ? 1f / 60f
                : Math.min(0.1f, (frameTimeNs - previousFrameNs) / 1_000_000_000f);
        previousFrameNs = frameTimeNs;
        applyCameraVelocity(delta);
        tickKeyframes(delta);
        document.engine().update(document.scene().getId(), delta, frameTimeNs);
        if (status != null && frameTimeNs - lastFpsStatusNs > FPS_STATUS_INTERVAL_NS) {
            lastFpsStatusNs = frameTimeNs;
            int fps = Math.round(document.engine().getFpsEma());
            status.setText(statusBase + "  ·  " + fps + " fps");
        }
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    @Override protected void onResume() {
        super.onResume();
        if (document != null) document.engine().onResume();
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    @Override protected void onPause() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
        camVelForward = 0f;
        camVelStrafe = 0f;
        camVelUp = 0f;
        if (joystick != null) joystick.reset();
        if (document != null) { document.save(); document.engine().onPause(); }
        super.onPause();
    }

    @Override protected void onDestroy() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
        for (AlertDialog dialog : new ArrayList<>(activeDialogs)) {
            try {
                dialog.dismiss();
            } catch (RuntimeException ignored) {
            }
        }
        activeDialogs.clear();
        searchHandler.removeCallbacksAndMessages(null);
        transformHandler.removeCallbacksAndMessages(null);
        destroyed = true;
        importQueue.shutdown();
        if (document != null) document.dispose();
        super.onDestroy();
    }
}
