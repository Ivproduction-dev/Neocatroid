package org.catrobat.catroid.fast2d.mirror;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.TilemapLookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.fast2d.FastTwoDManager;

final class MirrorNode {
    final Sprite sprite;
    final String entityId;
    int seenStamp;

    private boolean hasEntity;
    private float x = Float.NaN;
    private float y = Float.NaN;
    private float rotation = Float.NaN;
    private float scaleX = Float.NaN;
    private float scaleY = Float.NaN;
    private float z = Float.NaN;
    private float r = Float.NaN;
    private float g = Float.NaN;
    private float b = Float.NaN;
    private float a = Float.NaN;
    private boolean visible;
    private boolean visibleSet;
    private String texturePath;

    MirrorNode(Sprite sprite) {
        this.sprite = sprite;
        this.entityId = "mirror-" + Integer.toHexString(System.identityHashCode(sprite));
    }

    void sync(FastTwoDManager manager) {
        Look look = sprite.look;
        if (look == null) {
            drop(manager);
            return;
        }
        LookData data = look.getLookData();
        Drawable drawable = look.getDrawable();
        if (data == null || data.getFile() == null
                || !(drawable instanceof TextureRegionDrawable)
                || data instanceof TilemapLookData
                || look.needsCustomShader()
                || look.isGifPlaying()
                || look.isSpritesheetPlaying()
                || look.hasCornerMesh()
                || look.hasParticleEffect) {
            drop(manager);
            return;
        }
        float cx = look.getX() + look.getWidth() / 2f;
        float cy = look.getY() + look.getHeight() / 2f;
        float rot = look.getRotation();
        float sx = look.getScaleX();
        float sy = look.getScaleY();
        float z = look.getZIndex();
        Color color = look.getColor();
        float alpha = color.a * look.getAlpha();
        boolean vis = look.isVisible() && look.isLookVisible() && alpha > 0f;
        String path = data.getFile().getAbsolutePath();
        if (!hasEntity) {
            manager.obtainEntityImmediate(entityId);
            manager.writeTransformImmediate(entityId, cx, cy, rot, sx, sy, z);
            manager.writeTextureImmediate(entityId, path);
            manager.writeColorImmediate(entityId, color.r, color.g, color.b, alpha);
            manager.writeVisibleImmediate(entityId, vis);
            x = cx;
            y = cy;
            rotation = rot;
            scaleX = sx;
            scaleY = sy;
            this.z = z;
            r = color.r;
            g = color.g;
            b = color.b;
            a = alpha;
            visible = vis;
            visibleSet = true;
            texturePath = path;
            hasEntity = true;
            return;
        }
        if (cx != x || cy != y || rot != rotation || sx != scaleX || sy != scaleY || z != this.z) {
            manager.writeTransformImmediate(entityId, cx, cy, rot, sx, sy, z);
            x = cx;
            y = cy;
            rotation = rot;
            scaleX = sx;
            scaleY = sy;
            this.z = z;
        }
        if (texturePath == null || !texturePath.equals(path)) {
            manager.writeTextureImmediate(entityId, path);
            texturePath = path;
        }
        if (!visibleSet || r != color.r || g != color.g || b != color.b || a != alpha) {
            manager.writeColorImmediate(entityId, color.r, color.g, color.b, alpha);
            r = color.r;
            g = color.g;
            b = color.b;
            a = alpha;
            visibleSet = true;
        }
        if (!visibleSet || visible != vis) {
            manager.writeVisibleImmediate(entityId, vis);
            visible = vis;
            visibleSet = true;
        }
    }

    void drop(FastTwoDManager manager) {
        if (hasEntity) {
            manager.removeEntityImmediate(entityId);
            hasEntity = false;
        }
        texturePath = null;
        visibleSet = false;
    }
}
