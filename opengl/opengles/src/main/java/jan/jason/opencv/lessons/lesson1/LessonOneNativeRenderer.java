package jan.jason.opencv.lessons.lesson1;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LessonOneNativeRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("lesson-lib");
    }

    public static native void nativeSurfaceCreate();

    public static native void nativeSurfaceChange(int width, int height);

    public static native void nativeDrawFrame();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("TEST##","onSurfaceCreated ");
        nativeSurfaceCreate();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("TEST##","onSurfaceChanged ");
        nativeSurfaceChange(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("TEST##","onDrawFrame ");
        nativeDrawFrame();
    }
}
