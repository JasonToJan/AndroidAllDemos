package jan.jason.ndkdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class JniCallbackDemo {

    private static final String TAG = "JniCallbackDemo";

    static {
        System.loadLibrary("jni_callback");
    }

    public native void startTiming();
    public native void stopTiming();

    private int timeCount;
    private Activity context;

    public JniCallbackDemo(Activity context){
        this.context=context;
    }

    private void printTime() {
        Log.e(TAG, "timeCount = " + timeCount);
        timeCount++;

        if(context!=null){
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(context!=null){
                        Toast.makeText(context, "currentTime: "+timeCount, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static class JniHandler {

        public static String getBuildVersion() {
            return Build.VERSION.RELEASE;
        }

        public long getRuntimeMemorySize() {
            return Runtime.getRuntime().freeMemory();
        }

        private void updateStatus(String msg) {
            if (msg.toLowerCase().contains("error")) {
                Log.e("JniHandler", "Native Err: " + msg);
            } else {
                Log.i("JniHandler", "Native Msg: " + msg);
            }
        }
    }
}