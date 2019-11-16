package com.d9tilov.currencyapp.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LruCache;

import com.d9tilov.currencyapp.utils.CurrencyUtils;

import java.util.Currency;

import javax.annotation.Nonnull;

public class LetterDrawable extends Drawable {
    private static final LruCache<String, DrawableState> CACHE = new LruCache<>(150);

    private static final Rect TMP_RECT = new Rect();

    private static final double DEFAULT_SCALE_TEXT = 0.54;
    private static final double BIG_SCALE_TEXT = 0.64;
    private static final int ASCII_OFFSET = 0x41;
    private static final int UNICODE_FLAG_OFFSET = 0x1F1E6;

    private final RectF rect = new RectF();
    private final DrawableState letterState;
    Paint bgPaint = new Paint();

    private float textOffset;

    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    static LetterDrawable get(LetterDrawableParams params) {
        String key = params.getName() + " - "
                + params.getColorStartGradient() + " - "
                + params.getColorEndGradient() + " - "
                + params.isRound() + " - "
                + params.isBigText() + " - "
                + params.isConference();

        DrawableState drawableState = CACHE.get(key);
        if (drawableState == null) {
            drawableState = params.createDrawableState();
            CACHE.put(key, drawableState);
        }
        return drawableState.newDrawable();
    }

    private LetterDrawable(DrawableState state) {
        super();
        letterState = state;
        calcTextOffset();
    }

    private void calcTextOffset() {
        letterState.textPaint.setTextSize(getTextSize());
        String code = "RUB";
        int firstChar = Character.codePointAt(code, 0) - ASCII_OFFSET + UNICODE_FLAG_OFFSET;
        int secondChar = Character.codePointAt(code, 1) - ASCII_OFFSET + UNICODE_FLAG_OFFSET;
        String res =  new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
        letterState.textPaint.getTextBounds(letterState.name, 0, res.length(), TMP_RECT);
        textOffset = TMP_RECT.height() / 2 + TMP_RECT.top;
    }

    private int getTextSize() {
        int minSize = Math.min(getBounds().width(), getBounds().height());
        double scale = letterState.bigText ? BIG_SCALE_TEXT : DEFAULT_SCALE_TEXT;
        return (int) (minSize * scale);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        rect.set(bounds);
        calcTextOffset();
    }

    @Override
    public void draw(@Nonnull Canvas canvas) {
        drawBackground(canvas);
//        if (letterState.emoji == null) {
        drawText(canvas);
//        } else {
//            drawEmoji(canvas);
//        }
    }

    private void drawText(@Nonnull Canvas canvas) {
//        letterState.textPaint.setAlpha(alpha);
        letterState.textPaint.setTextSize(getTextSize());
        float cY = rect.centerY() - textOffset;
        String code = "RUB";
        int firstChar = Character.codePointAt(code, 0) - ASCII_OFFSET + UNICODE_FLAG_OFFSET;
        int secondChar = Character.codePointAt(code, 1) - ASCII_OFFSET + UNICODE_FLAG_OFFSET;
        String res =  new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
        Log.d("moggot1234", "text = " + res);
        canvas.drawText(res, 0, res.length(), rect.centerX(), cY, letterState.textPaint);
    }

    private void drawBackground(@Nonnull Canvas canvas) {
        canvas.drawOval(rect, bgPaint);
    }

    @Override
    public void setAlpha(int alpha) {
//        this.alpha = alpha;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public ConstantState getConstantState() {
        return letterState;
    }

    private static Paint createTextPaint() {
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(0x000000);
        textPaint.setTextAlign(Paint.Align.CENTER);
        return textPaint;
    }

    public String getName() {
        return letterState.name;
    }

    private static final class DrawableState extends ConstantState {

        final String name;
        final Paint textPaint;
        final boolean round;
        final boolean bigText;
        final int startColor;
        final int endColor;

        DrawableState(LetterDrawableParams params, Paint textPaint) {
            String code = "RUB";
            int firstChar = Character.codePointAt(code, 0) - ASCII_OFFSET + UNICODE_FLAG_OFFSET;
            int secondChar = Character.codePointAt(code, 1) - ASCII_OFFSET + UNICODE_FLAG_OFFSET;
            String res =  new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
            this.name = res;
            this.textPaint = textPaint;
            this.round = params.isRound();
            this.bigText = params.isBigText();
            this.startColor = params.getColorStartGradient();
            this.endColor = params.getColorEndGradient();
        }

        @Override
        public LetterDrawable newDrawable() {
            return new LetterDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }

        @Override
        @SuppressWarnings("PMD.SimplifyBooleanReturns")
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            DrawableState that = (DrawableState) o;

            if (round != that.round) {
                return false;
            }
            if (!name.equals(that.name)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (round ? 1 : 0);
            return result;
        }
    }

    public static class LetterDrawableParams {
        private String name;
        private boolean round;
        private boolean conference;
        private boolean bigText;

        private int colorStartGradient;
        private int colorEndGradient;

        public LetterDrawableParams setColorGradient(int startColor, int endColor) {
            colorStartGradient = startColor;
            colorEndGradient = endColor;
            return this;
        }

        public int getColorStartGradient() {
            return colorStartGradient;
        }

        public int getColorEndGradient() {
            return colorEndGradient;
        }

        public LetterDrawableParams setName(String name) {
            this.name = name;
            return this;
        }

        public String getName() {
            return name;
        }

        public boolean isBigText() {
            return bigText;
        }

        public LetterDrawableParams setBigText(boolean bigText) {
            this.bigText = bigText;
            return this;
        }

        public boolean isRound() {
            return round;
        }

        public LetterDrawableParams setRound(boolean round) {
            this.round = round;
            return this;
        }

        public boolean isConference() {
            return conference;
        }

        public LetterDrawableParams setConference(boolean conference) {
            this.conference = conference;
            return this;
        }

        private DrawableState createDrawableState() {
            return new DrawableState(this, createTextPaint());
        }

    }

    public static void awaitLowMemory() {
    }
}
