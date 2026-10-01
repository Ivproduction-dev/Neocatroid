package org.catrobat.catroid.editor2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

final class Neo3DJoystickView extends View {

    interface Listener {
        void onMove(float x, float y);
        void onRelease();
    }

    private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint knobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float knobX;
    private float knobY;
    private float radius;
    private boolean active;
    private Listener listener;

    Neo3DJoystickView(Context context) {
        super(context);
        init();
    }

    Neo3DJoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setStrokeWidth(4f);
        basePaint.setColor(0xAAFFFFFF);
        knobPaint.setStyle(Paint.Style.FILL);
        knobPaint.setColor(Color.WHITE);
        setClickable(true);
        setFocusable(true);
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    float[] consumeVector(float[] out) {
        if (!active) return null;
        float dx = knobX;
        float dy = knobY;
        if (dx == 0f && dy == 0f) return null;
        out[0] = dx;
        out[1] = dy;
        return out;
    }

    void reset() {
        active = false;
        knobX = 0f;
        knobY = 0f;
        invalidate();
        if (listener != null) listener.onRelease();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(w, h) * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() * 0.5f;
        float cy = getHeight() * 0.5f;
        float base = radius <= 0f ? Math.min(getWidth(), getHeight()) * 0.5f : radius;
        canvas.drawCircle(cx, cy, base - 2f, basePaint);
        canvas.drawCircle(cx + knobX * (base - 2f), cy + knobY * (base - 2f),
                (base - 2f) * 0.4f, knobPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            float cx = getWidth() * 0.5f;
            float cy = getHeight() * 0.5f;
            float base = radius <= 0f ? Math.min(getWidth(), getHeight()) * 0.5f : radius;
            float dx = (event.getX() - cx) / base;
            float dy = (event.getY() - cy) / base;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len > 1f) {
                dx /= len;
                dy /= len;
            }
            knobX = dx;
            knobY = dy;
            active = true;
            invalidate();
            if (listener != null) listener.onMove(dx, dy);
            return true;
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            reset();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
