package org.catrobat.catroid.neo3d;

public class Neo3DAnimationClip {

    private final String name;
    private float durationSec;
    private boolean loop = true;
    private float speed = 1f;

    public Neo3DAnimationClip(String name, float durationSec) {
        this.name = name;
        this.durationSec = durationSec;
    }

    public String getName() {
        return name;
    }

    public float getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(float durationSec) {
        this.durationSec = durationSec;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public static class State {
        private final Neo3DAnimationClip clip;
        private float timeSec;
        private boolean playing = true;

        public State(Neo3DAnimationClip clip) {
            this.clip = clip;
        }

        public Neo3DAnimationClip getClip() {
            return clip;
        }

        public String getClipName() {
            return clip == null ? null : clip.getName();
        }

        public float getTimeSec() {
            return timeSec;
        }

        public boolean isPlaying() {
            return playing;
        }

        public void setPlaying(boolean playing) {
            this.playing = playing;
        }

        public void seek(float timeSec) {
            this.timeSec = timeSec;
        }

        public void tick(float deltaSec) {
            if (!playing || clip.durationSec <= 0f) {
                return;
            }
            timeSec += deltaSec * clip.speed;
            if (clip.loop) {
                timeSec %= clip.durationSec;
            } else if (timeSec >= clip.durationSec) {
                timeSec = clip.durationSec;
                playing = false;
            }
        }
    }
}
