package com.d9tilov.currencyapp.view;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class CircleDrawable extends Drawable {
    private float scale = 1;

    private final RectF rect = new RectF();
    private boolean mutated;

    private CircleState circleState;

    public CircleDrawable(Bitmap bitmap) {
        if (bitmap == null) {
            circleState = new CircleState(null, null, 0, 0);
            return;
        }

        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int size;
        if (w == h) {
            size = w;
        } else {
            int sz = Math.min(w, h);
            int x = (w - sz) / 2;
            int y = (h - sz) / 2;

            size = sz;
            Matrix matrix = new Matrix();
            matrix.setTranslate(-x, -y);
            bitmapShader.setLocalMatrix(matrix);
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        circleState = new CircleState(bitmap, paint, size, size);
    }

    CircleDrawable(CircleState mCircleState) {
        this.circleState = mCircleState;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        float width = bounds.width();
        float height = bounds.height();

        float scaleX = width / circleState.bitmapWidth;
        float scaleY = height / circleState.bitmapHeight;

        scale = scaleX > scaleY ? scaleX : scaleY;

        float newWidth = width / scale;
        float newHeight = height / scale;

        rect.set(0, 0, newWidth, newHeight);
    }

    @Override
    public int getIntrinsicWidth() {
        return circleState.radius;
    }

    @Override
    public int getIntrinsicHeight() {
        return circleState.radius;
    }

    @Override
    public void draw(Canvas canvas) {
        if (circleState.paint != null) {
            canvas.save();
            canvas.scale(scale, scale);
            canvas.drawOval(rect, circleState.paint);
        }
        canvas.restore();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        if (circleState.paint != null) {
            circleState.paint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        if (circleState.paint != null) {
            return circleState.paint.getAlpha();
        }
        return 0;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (circleState.paint != null) {
            circleState.paint.setColorFilter(cf);
            invalidateSelf();
        }
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if (circleState.paint != null) {
            circleState.paint.setFilterBitmap(filter);
            invalidateSelf();
        }
    }

    @Override
    public void setDither(boolean dither) {
        if (circleState.paint != null) {
            circleState.paint.setDither(dither);
            invalidateSelf();
        }
    }

    @TargetApi(21)
    @Override
    public void getOutline(Outline outline) {
        outline.setOval((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
    }

    public Bitmap getBitmap() {
        return circleState.getBitmap();
    }

    @Override
    public ConstantState getConstantState() {
        return circleState;
    }

    @Override
    public Drawable mutate() {
        if (!mutated && super.mutate() == this) {
            circleState = new CircleState(circleState);
            mutated = true;
        }
        return this;
    }

    private static final class CircleState extends ConstantState {
        final Paint paint;

        final Bitmap bitmap;
        final int bitmapWidth;
        final int bitmapHeight;
        final int radius;

        CircleState(Bitmap bitmap, Paint paint, int bitmapWidth, int bitmapHeight) {
            this.bitmap = bitmap;
            this.paint = paint;
            this.bitmapWidth = bitmapWidth;
            this.bitmapHeight = bitmapHeight;
            radius = Math.max(this.bitmapWidth, this.bitmapHeight);
        }

        CircleState(CircleState bitmapState) {
            this(bitmapState.bitmap, new Paint(bitmapState.paint), bitmapState.bitmapWidth, bitmapState.bitmapHeight);
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        public Drawable newDrawable() {
            return new CircleDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof CircleDrawable)) {
            return false;
        }

        Bitmap bm = getBitmap();
        Bitmap oBm = ((CircleDrawable) o).getBitmap();
        return bm != null && bm.equals(oBm);
    }

    @Override
    public int hashCode() {
        Bitmap bm = getBitmap();
        return bm != null ? bm.hashCode() : 0;
    }
}
