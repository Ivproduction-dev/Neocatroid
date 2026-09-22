package org.catrobat.catroid.neo3d.backend;

public interface RenderDispatcher {
    void dispatch(Runnable command);
}
