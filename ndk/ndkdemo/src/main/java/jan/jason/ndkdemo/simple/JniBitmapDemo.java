package jan.jason.ndkdemo.simple;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * jni使用bitmap
 */
public class JniBitmapDemo {

    private static final String TAG = "JniBitmapDemo";

    static {
        System.loadLibrary("bitmap");
    }

    public native void passBitmap(Bitmap bitmap);


    public void test() {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(0xff336699); // AARRGGBB 擦除颜色
        byte[] bytes = new byte[bitmap.getWidth() * bitmap.getHeight() * 4];
        Buffer dst = ByteBuffer.wrap(bytes);
        bitmap.copyPixelsToBuffer(dst);//将Bitmap的像素值拷贝到bytes数组中
        // ARGB_8888 真实的存储顺序是 R-G-B-A
        Log.d(TAG, "R: " + Integer.toHexString(bytes[0] & 0xff));
        Log.d(TAG, "G: " + Integer.toHexString(bytes[1] & 0xff));
        Log.d(TAG, "B: " + Integer.toHexString(bytes[2] & 0xff));
        Log.d(TAG, "A: " + Integer.toHexString(bytes[3] & 0xff));

        passBitmap(bitmap);
    }
}
