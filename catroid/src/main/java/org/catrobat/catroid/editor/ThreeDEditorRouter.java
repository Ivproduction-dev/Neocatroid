package org.catrobat.catroid.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.editor2.Neo3DEditorActivity;

public final class ThreeDEditorRouter {

    public static final String PREFERENCE_KEY = "setting_3d_editor_version";
    public static final String CLASSIC = "classic";
    public static final String NEO3D = "neo3d";
    public static final String ASK = "ask";

    private ThreeDEditorRouter() {
    }

    public static void open(Context context) {
        String selection = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREFERENCE_KEY, null);
        if (selection == null || ASK.equals(selection)) {
            showFirstChoice(context);
        } else {
            launch(context, selection);
        }
    }

    public static void switchEditor(Activity activity, String selection) {
        PreferenceManager.getDefaultSharedPreferences(activity).edit()
                .putString(PREFERENCE_KEY, selection)
                .apply();
        launch(activity, selection);
        activity.finish();
    }

    private static void showFirstChoice(Context context) {
        new AlertDialog.Builder(context, R.style.Theme_NeoCatroid_Dialog)
                .setTitle(R.string.editor_3d_version_title)
                .setMessage(R.string.editor_3d_version_description)
                .setPositiveButton(R.string.editor_3d_version_new, (dialog, which) -> {
                    PreferenceManager.getDefaultSharedPreferences(context).edit()
                            .putString(PREFERENCE_KEY, NEO3D).apply();
                    launch(context, NEO3D);
                })
                .setNegativeButton(R.string.editor_3d_version_classic, (dialog, which) -> {
                    PreferenceManager.getDefaultSharedPreferences(context).edit()
                            .putString(PREFERENCE_KEY, CLASSIC).apply();
                    launch(context, CLASSIC);
                })
                .show();
    }

    private static void launch(Context context, String selection) {
        Class<?> destination = NEO3D.equals(selection)
                ? Neo3DEditorActivity.class : EditorActivity.class;
        context.startActivity(new Intent(context, destination));
    }
}
