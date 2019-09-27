package jan.jason.ndkdemo.OpenGL;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 关键的渲染器
 * @author https://github.com/103style
 * @date 2019/8/26 11:16
 */
public class GLES3Render implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("native-gles3");
    }

    public native void surfaceChanged(int w, int h);

    public native void drawFrame();

    private boolean isDoSufaceChanged;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        if(!isDoSufaceChanged){
            isDoSufaceChanged=true;
            surfaceChanged(w, h);//主要作用，把三角形画上去
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        drawFrame();//不断改变背景色
    }
}
