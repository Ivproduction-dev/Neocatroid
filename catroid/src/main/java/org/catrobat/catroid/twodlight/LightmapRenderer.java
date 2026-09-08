package org.catrobat.catroid.twodlight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import org.catrobat.catroid.physics.PhysicsWorld;

import java.util.List;

public class LightmapRenderer {

    private static final float LIGHTMAP_SCALE = 0.25f;
    private static final String TAG = "Lightmap2D";

    private static final String MULTIPLY_VERTEX =
            "attribute vec4 a_position;\n"
                    + "attribute vec4 a_color;\n"
                    + "attribute vec2 a_texCoord0;\n"
                    + "uniform mat4 u_projTrans;\n"
                    + "varying vec4 v_color;\n"
                    + "varying vec2 v_texCoords;\n"
                    + "void main() {\n"
                    + "   v_color = a_color;\n"
                    + "   v_texCoords = a_texCoord0;\n"
                    + "   gl_Position = u_projTrans * a_position;\n"
                    + "}\n";
    private static final String MULTIPLY_FRAGMENT =
            "varying vec4 v_color;\n"
                    + "varying vec2 v_texCoords;\n"
                    + "uniform sampler2D u_texture;\n"
                    + "void main() {\n"
                    + "   vec3 light = texture2D(u_texture, v_texCoords).rgb;\n"
                    + "   gl_FragColor = vec4(light, 1.0);\n"
                    + "}\n";

    private FrameBuffer lightFbo;
    private com.badlogic.gdx.graphics.g2d.TextureRegion lightRegion;
    private SpriteBatch multiplyBatch;
    private ShapeRenderer shapes;
    private ShaderProgram multiplyShader;
    private final OrthographicCamera lightCam = new OrthographicCamera();
    private final ShadowCaster shadowCaster = new ShadowCaster();
    private final Color centerColor = new Color();
    private final Color edgeColor = new Color();
    private int fboWidth;
    private int fboHeight;
    private boolean shaderLogged;

    public void render(LightManager2D manager, OrthographicCamera camera, PhysicsWorld physicsWorld) {
        if (manager == null || camera == null) {
            return;
        }
        List<Light2D> active = manager.getActiveLights();
        if (active.isEmpty()) {
            return;
        }
        try {
            ensureResources(camera);
            if (lightFbo == null) {
                return;
            }
            renderLightmap(manager, active, camera, physicsWorld);
            multiplyOntoScene(camera);
        } catch (Throwable throwable) {
            Gdx.app.error(TAG, "Light render failed, skipping pass", throwable);
        }
    }

    private void ensureResources(OrthographicCamera camera) {
        int width = Math.max(1, (int) Math.ceil(camera.viewportWidth * LIGHTMAP_SCALE));
        int height = Math.max(1, (int) Math.ceil(camera.viewportHeight * LIGHTMAP_SCALE));
        if (lightFbo == null || width != fboWidth || height != fboHeight) {
            disposeFbo();
            lightFbo = new FrameBuffer(com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888,
                    width, height, false);
            lightFbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear,
                    Texture.TextureFilter.Linear);
            lightRegion = new com.badlogic.gdx.graphics.g2d.TextureRegion(
                    lightFbo.getColorBufferTexture());
            lightRegion.flip(false, true);
            fboWidth = width;
            fboHeight = height;
        }
        if (multiplyBatch == null) {
            multiplyBatch = new SpriteBatch();
        }
        if (shapes == null) {
            shapes = new ShapeRenderer();
            shapes.setAutoShapeType(true);
        }
        if (multiplyShader == null) {
            ShaderProgram.pedantic = false;
            multiplyShader = new ShaderProgram(MULTIPLY_VERTEX, MULTIPLY_FRAGMENT);
            if (!multiplyShader.isCompiled() && !shaderLogged) {
                shaderLogged = true;
                Gdx.app.error(TAG, "Multiply shader failed: " + multiplyShader.getLog());
            }
        }
        lightCam.viewportWidth = camera.viewportWidth;
        lightCam.viewportHeight = camera.viewportHeight;
        lightCam.position.set(camera.position);
        lightCam.update();
    }

    private void renderLightmap(LightManager2D manager, List<Light2D> active,
            OrthographicCamera camera, PhysicsWorld physicsWorld) {
        float ambient = manager.getAmbient();
        lightFbo.begin();
        ScreenUtils.clear(ambient, ambient, ambient, 1f);
        final PhysicsWorld world = physicsWorld;
        LightRaycaster raycaster = null;
        if (world != null) {
            raycaster = new LightRaycaster() {
                @Override
                public float castRay(float startX, float startY, float endX, float endY,
                        java.util.Set<String> ignoredSpriteNames, float[] outHitPoint) {
                    return world.castLightRay(startX, startY, endX, endY, ignoredSpriteNames,
                            outHitPoint);
                }
            };
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.setProjectionMatrix(lightCam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Light2D light : active) {
            LightRaycaster lightRays = (light.isShadowsEnabled() && raycaster != null)
                    ? raycaster : null;
            ShadowCaster.ShadowFan fan = shadowCaster.computeFan(light.getX(), light.getY(),
                    light.getRadius(), lightRays, manager.getNoShadowSprites(),
                    ShadowCaster.DEFAULT_RAY_COUNT);
            drawFan(light, fan);
        }
        shapes.end();
        lightFbo.end();
        restoreDefaultBlend();
    }

    private void drawFan(Light2D light, ShadowCaster.ShadowFan fan) {
        float clamped = Math.max(0f, Math.min(1f, light.getIntensity()));
        float gain = Math.min(1f, clamped * 1.5f);
        float r = Math.min(1f, light.getRed() * gain);
        float g = Math.min(1f, light.getGreen() * gain);
        float b = Math.min(1f, light.getBlue() * gain);
        centerColor.set(r, g, b, clamped);
        edgeColor.set(r, g, b, 0f);
        float cx = light.getX();
        float cy = light.getY();
        for (int i = 0; i < fan.rayCount; i++) {
            shapes.triangle(cx, cy, fan.ringX[i], fan.ringY[i], fan.ringX[i + 1], fan.ringY[i + 1],
                    centerColor, edgeColor, edgeColor);
        }
    }

    private void multiplyOntoScene(OrthographicCamera camera) {
        if (multiplyShader == null || !multiplyShader.isCompiled()) {
            return;
        }
        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;
        float left = camera.position.x - halfW;
        float bottom = camera.position.y - halfH;
        multiplyBatch.setProjectionMatrix(camera.combined);
        multiplyBatch.setShader(multiplyShader);
        multiplyBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
        multiplyBatch.begin();
        multiplyBatch.draw(lightRegion, left, bottom, camera.viewportWidth, camera.viewportHeight);
        multiplyBatch.end();
        multiplyBatch.setShader(null);
        restoreDefaultBlend();
    }

    private void restoreDefaultBlend() {
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void disposeFbo() {
        if (lightFbo != null) {
            try {
                lightFbo.dispose();
            } catch (Exception e) {
                Gdx.app.error(TAG, "FBO dispose failed", e);
            }
            lightFbo = null;
            lightRegion = null;
            fboWidth = 0;
            fboHeight = 0;
        }
    }

    public void dispose() {
        disposeFbo();
        if (multiplyBatch != null) {
            try {
                multiplyBatch.dispose();
            } catch (Exception e) {
                Gdx.app.error(TAG, "Batch dispose failed", e);
            }
            multiplyBatch = null;
        }
        if (shapes != null) {
            try {
                shapes.dispose();
            } catch (Exception e) {
                Gdx.app.error(TAG, "Shapes dispose failed", e);
            }
            shapes = null;
        }
        if (multiplyShader != null) {
            try {
                multiplyShader.dispose();
            } catch (Exception e) {
                Gdx.app.error(TAG, "Shader dispose failed", e);
            }
            multiplyShader = null;
        }
    }
}
