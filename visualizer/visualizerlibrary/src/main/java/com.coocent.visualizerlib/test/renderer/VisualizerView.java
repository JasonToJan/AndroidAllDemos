package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.utils.LogUtils;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

/**

 */
public class VisualizerView extends View {
    private final String TAG = "VisualizerView";
    private byte[] mBytes;
    private byte[] mFFTBytes;
    private Rect mRect = new Rect();
    private Visualizer mVisualizer;

    private Set<Renderer> mRenderers;

    private Paint mFlashPaint = new Paint();
    private Paint mFadePaint = new Paint();
    public int left1, top1, right1, bottom1;
    private Canvas mCanvas;
    private long mAudioSamplingTime;
    private long mFftSamplingTime;
    private int mSamplingTime = 100;//数据采样时间间隔
    private boolean isLink = false;

    public VisualizerView(Context context) {
        this(context, null);
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // setBackgroundColor(Color.TRANSPARENT);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁止硬件加速
        init();
    }

    private void init() {
        mBytes = null;
        mFFTBytes = null;

//        mFlashPaint.setColor(Color.argb(122, 255, 255, 255));
//        mFadePaint.setColor(Color.argb(238, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
//        mFadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
//
        mRenderers = new HashSet<>();

        mVisualizer=new Visualizer(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.left1 = left;
        this.top1 = top;
        this.right1 = right;
        this.bottom1 = bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.d("测试，执行了VisualizerView onDraw 1");
        this.mCanvas = canvas;
        mRect.set(0, 0, getWidth(), getHeight());

        try {
            if (mBytes != null) {
                // Render all audio renderers
//                LogUtils.d("测试，执行了VisualizerView onDraw 2 长度："+mRenderers.size());

                for (Renderer r : mRenderers) {
//                    LogUtils.d("测试，执行了VisualizerView onDraw 3");
                    r.audioRender(canvas, mBytes, mRect);
                }

            }

            if (mFFTBytes != null) {
                // Render all FFT renderers
                for (Renderer r : mRenderers) {
                    r.fftRender(canvas, mFFTBytes, mRect);
                }

            }
        } catch (ConcurrentModificationException e) {

        }


    }

    public void link(int musicId, MediaPlayer mediaPlayer) {
        // LogUtils.e(TAG, "nsc link =" + mVisualizer + " isLink=="+isLink);
        // Create the Visualizer object and attach it to our media player.
//        if (musicId == 0) return;
        try {
            //使用前先释放
//            if (mVisualizer != null) {
//                LogUtils.d("这里Link函数中释放了");
//                release();
//            }
            if (mVisualizer == null) {
                LogUtils.d("这里link函数中new了一个Visualizer");
                //mVisualizer =VisualizerManager.getInstance().getOneVisualizer(true);
                mVisualizer=new Visualizer(musicId);


                // isLink = true;
            }
//            LogUtils.e("测试"+TAG, "nsc link=" + mVisualizer.getEnabled() + " isLink=" + isLink + " musicId=" + musicId);
            LogUtils.d("这里设置了enable为false");
           // mVisualizer.setEnabled(false);
           // mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            // Pass through Visualizer data to VisualizerView
            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - mAudioSamplingTime >= mSamplingTime) {
                        mBytes = bytes;
                        invalidate();
                        mAudioSamplingTime = currentTimeMillis;
//                        LogUtils.d("测试+onWaveFormData执行了");
                    }

                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                             int samplingRate) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - mFftSamplingTime >= mSamplingTime) {
                        mFFTBytes = bytes;
                        invalidate();
                        mFftSamplingTime = currentTimeMillis;
                    }

                }
            };
            if (mVisualizer != null) {
                mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
                // Enabled Visualizer and disable when we're done with the stream
                LogUtils.d("这里设置了enable为true");
                mVisualizer.setEnabled(true);
            }

        } catch (RuntimeException e) {

        }
    }

    public void setListenNull(){
        if(mVisualizer!=null){
            LogUtils.d("这里设置了空的");
            mVisualizer.setDataCaptureListener(null,0,false,false);
        }
    }

    public void setListenResume(){
        if(mVisualizer!=null){
            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - mAudioSamplingTime >= mSamplingTime) {
                        mBytes = bytes;
                        invalidate();
                        mAudioSamplingTime = currentTimeMillis;
//                        LogUtils.d("测试+onWaveFormData执行了");
                    }

                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                             int samplingRate) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - mFftSamplingTime >= mSamplingTime) {
                        mFFTBytes = bytes;
                        invalidate();
                        mFftSamplingTime = currentTimeMillis;
                    }

                }
            };
            mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
            // Enabled Visualizer and disable when we're done with the stream
            LogUtils.d("2222这里设置了enable为true");
            mVisualizer.setEnabled(true);
        }
    }

    public void link2(int musicId) {
        // LogUtils.e(TAG, "nsc link =" + mVisualizer + " isLink=="+isLink);
        // Create the Visualizer object and attach it to our media player.
        if (musicId == 0) return;
        try {
            if (mVisualizer == null) {
                mVisualizer = new Visualizer(musicId);
            }
           // LogUtils.e(TAG, "nsc link=" + mVisualizer.getEnabled() + " isLink=" + isLink + " musicId=" + musicId);
            mVisualizer.setEnabled(false);
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            // Pass through Visualizer data to VisualizerView
            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - mAudioSamplingTime >= mSamplingTime) {
                        mBytes = bytes;
                        invalidate();
                        mAudioSamplingTime = currentTimeMillis;
                    }

                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                             int samplingRate) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - mFftSamplingTime >= mSamplingTime) {
                        mFFTBytes = bytes;
                        invalidate();
                        mFftSamplingTime = currentTimeMillis;
                    }

                }
            };
            if (mVisualizer != null) {
                mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
                // Enabled Visualizer and disable when we're done with the stream
                mVisualizer.setEnabled(true);
            }

        } catch (RuntimeException e) {

        }


    }


    public boolean getVisualizerEnable() {
        if (mVisualizer != null) {
            return mVisualizer.getEnabled();
        }
        return false;
    }


    public void setVisualizerEnable(boolean flag) {
        try {
            if (mVisualizer != null) {
                LogUtils.d("这里VisualizerView设置了Enable为："+flag);
                mVisualizer.setEnabled(flag);
            }
        } catch (RuntimeException e) {
            LogUtils.e(TAG, "setVisualizerEnable =" + e.getMessage());
        }
    }

    public void release() {
        if (mVisualizer != null) {
            // mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
            isLink = false;
        }
    }


    public void addRenderer(Renderer renderer) {
        if (renderer != null) {
            mRenderers.add(renderer);
//            if (mCanvas != null) {
//                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            }
        }
    }

    public void clearRenderers() {
        if (mRenderers != null) {
            mRenderers.clear();
        }
    }

}
