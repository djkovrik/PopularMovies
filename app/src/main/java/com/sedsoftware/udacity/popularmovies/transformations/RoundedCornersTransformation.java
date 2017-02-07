package com.sedsoftware.udacity.popularmovies.transformations;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Image transformation class which rounds off image corners, used with Picasso transform command.
 */
public class RoundedCornersTransformation implements Transformation {

    private final int radius;
    private final int margin;

    public RoundedCornersTransformation(int radius, int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap.Config config = source.getConfig();
        Bitmap result = Bitmap.createBitmap(width, height, config);

        Canvas canvas = new Canvas(result);
        RectF rect = new RectF(margin, margin, width - margin, height - margin);
        canvas.drawRoundRect(rect, radius, radius, paint);

        if (source != result) {
            source.recycle();
        }

        return result;
    }

    @Override
    public String key() {
        return "rounded(" + radius + ", " + margin + ")";
    }
}
