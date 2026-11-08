package org.catrobat.catroid.raptor;

public class AudioComponent implements Component {
    public String soundFileName;
    public float volume = 1.0f;
    public float pitch = 1.0f;
    public boolean loop;
    public boolean playOnAwake = true;
    public float startDelay;
    public boolean is3D = true;
    public float maxDistance = 30.0f;
    public boolean isMusic;

    public transient float delayTimer;
    public transient boolean hasStarted;
    public transient boolean isPreviewPlaying;
}
