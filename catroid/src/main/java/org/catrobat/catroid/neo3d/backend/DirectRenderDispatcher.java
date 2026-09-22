package org.catrobat.catroid.neo3d.backend;

public class DirectRenderDispatcher implements RenderDispatcher {
    @Override
    public void dispatch(Runnable command) {
        if (command != null) {
            command.run();
        }
    }
}
