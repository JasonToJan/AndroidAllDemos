package jan.jason.ndkdemo.OpenGL;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OpenGlTriangleActivity extends AppCompatActivity {

    private GLES3JNIView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new GLES3JNIView(getApplication());
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }
}
