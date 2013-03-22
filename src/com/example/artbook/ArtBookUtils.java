package com.example.artbook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-3-13
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public class ArtBookUtils {
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        long time=System.currentTimeMillis();
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        v.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, width, height);
        v.draw(c);

        Log.d("artbook","load bitmap time: "+(System.currentTimeMillis()-time));
        return b;
    }

    public static void saveBitmap(Bitmap bitmap, String fileName) {
        long time=System.currentTimeMillis();
        String filePath = Environment.getExternalStorageDirectory() + "/" + fileName;
        try {
            OutputStream stream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Log.d("artbook","save bitmap time: "+(System.currentTimeMillis()-time));
    }
}
