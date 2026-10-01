package org.catrobat.catroid.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import org.catrobat.catroid.raptor.ParticleCurvePoint;

import java.util.List;


public class GradientPreviewView extends View {

    private Paint paint;
    private List<ParticleCurvePoint<com.badlogic.gdx.graphics.Color>> points;
    private int[] cachedColors;
    private float[] cachedPositions;
    private LinearGradient cachedGradient;
    private int cachedWidth = -1;
    private int cachedPointsIdentity = 0;

    public GradientPreviewView(Context context, List<ParticleCurvePoint<com.badlogic.gdx.graphics.Color>> points) {
        super(context);
        this.points = points;
        init();
    }

    public GradientPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    public void setPoints(List<ParticleCurvePoint<com.badlogic.gdx.graphics.Color>> points) {
        this.points = points;
        cachedGradient = null;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) cachedGradient = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (points == null || points.isEmpty()) {
            paint.setColor(android.graphics.Color.WHITE);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
            return;
        }

        if (points.size() == 1) {
            com.badlogic.gdx.graphics.Color c = points.get(0).value;
            paint.setColor(gdxToAndroid(c));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
            return;
        }


        int count = points.size();
        int identity = pointsIdentity(points);
        if (cachedGradient == null || cachedWidth != getWidth() || cachedPointsIdentity != identity) {
            if (cachedColors == null || cachedColors.length != count) {
                cachedColors = new int[count];
                cachedPositions = new float[count];
            }
            for (int i = 0; i < count; i++) {
                cachedColors[i] = gdxToAndroid(points.get(i).value);
                cachedPositions[i] = Math.max(0f, Math.min(1f, points.get(i).time));
            }


            for (int i = 1; i < count; i++) {
                if (cachedPositions[i] <= cachedPositions[i - 1]) {
                    cachedPositions[i] = cachedPositions[i - 1] + 0.001f;
                }
            }

            cachedGradient = new LinearGradient(
                    0, 0, getWidth(), 0,
                    cachedColors, cachedPositions,
                    Shader.TileMode.CLAMP);
            cachedWidth = getWidth();
            cachedPointsIdentity = identity;
        }

        paint.setShader(cachedGradient);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setShader(null);



    }

    private int pointsIdentity(List<ParticleCurvePoint<com.badlogic.gdx.graphics.Color>> pts) {
        int hash = pts.size();
        for (int i = 0; i < pts.size(); i++) {
            ParticleCurvePoint<com.badlogic.gdx.graphics.Color> p = pts.get(i);
            hash = hash * 31 + Float.floatToIntBits(p.time);
            if (p.value != null) {
                hash = hash * 31 + Float.floatToIntBits(p.value.r);
                hash = hash * 31 + Float.floatToIntBits(p.value.g);
                hash = hash * 31 + Float.floatToIntBits(p.value.b);
                hash = hash * 31 + Float.floatToIntBits(p.value.a);
            }
        }
        return hash;
    }

    private int gdxToAndroid(com.badlogic.gdx.graphics.Color c) {
        return android.graphics.Color.argb(
                (int)(c.a * 255),
                (int)(c.r * 255),
                (int)(c.g * 255),
                (int)(c.b * 255));
    }
}