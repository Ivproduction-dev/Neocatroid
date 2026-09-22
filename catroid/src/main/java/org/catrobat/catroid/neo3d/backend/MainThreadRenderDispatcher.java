package org.catrobat.catroid.neo3d.backend;

import android.os.Handler;
import android.os.Looper;

public class MainThreadRenderDispatcher implements RenderDispatcher {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void dispatch(Runnable command) {
        if (command == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            command.run();
        } else {
            handler.post(command);
        }
    }
}
