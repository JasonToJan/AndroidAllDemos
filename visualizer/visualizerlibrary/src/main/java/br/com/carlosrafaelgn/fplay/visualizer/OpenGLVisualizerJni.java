//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013-2014, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package br.com.carlosrafaelgn.fplay.visualizer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewDebug.ExportedProperty;
import android.view.WindowManager;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.common.ArraySorter;
import com.coocent.visualizerlib.core.MainHandler;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.entity.SongInfo;
import com.coocent.visualizerlib.inter.IVisualizer;
import com.coocent.visualizerlib.inter.IVisualizerMenu;
import com.coocent.visualizerlib.ui.UI;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.CustomColorPickDialogUtils;
import com.coocent.visualizerlib.utils.DirectDrawer;
import com.coocent.visualizerlib.utils.FileUtils;
import com.coocent.visualizerlib.utils.ImageUtils;
import com.coocent.visualizerlib.utils.LogUtils;
import com.coocent.visualizerlib.utils.PermissionUtils;
import com.coocent.visualizerlib.view.ColorDrawable;
import com.coocent.visualizerlib.view.TextIconDrawable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;



public final class OpenGLVisualizerJni extends GLSurfaceView
		implements GLSurfaceView.Renderer, GLSurfaceView.EGLContextFactory,
		GLSurfaceView.EGLWindowSurfaceFactory, IVisualizer,
		MenuItem.OnMenuItemClickListener, MainHandler.Callback,IVisualizerMenu {

	private static final int MNU_COLOR = MNU_VISUALIZER + 1, MNU_SPEED0 = MNU_VISUALIZER + 2, MNU_SPEED1 = MNU_VISUALIZER + 3,
		MNU_SPEED2 = MNU_VISUALIZER + 4, MNU_CHOOSE_IMAGE = MNU_VISUALIZER + 5, MNU_DIFFUSION0 = MNU_VISUALIZER + 6,
		MNU_DIFFUSION1 = MNU_VISUALIZER + 7, MNU_DIFFUSION2 = MNU_VISUALIZER + 8, MNU_DIFFUSION3 = MNU_VISUALIZER + 9,
		MNU_RISESPEED0 = MNU_VISUALIZER + 10, MNU_RISESPEED1 = MNU_VISUALIZER + 11, MNU_RISESPEED2 = MNU_VISUALIZER + 12,
		MNU_RISESPEED3 = MNU_VISUALIZER + 13, MNU_SPEED_FASTEST = MNU_VISUALIZER + 14;

	private static final int MNU_CLEAR_IMAGE=10011;//添加清除图片方法
	public static final int READ_AND_WEITE_PERMISSION_CODE=10009;
	public static final int ACTIVITY_CHOOSE_IMAGE=1234;

	private static final int MSG_OPENGL_ERROR = 0x0600;
	private static final int MSG_CHOOSE_IMAGE = 0x0601;

	private static int GLVersion = -1;

	public static final String EXTRA_VISUALIZER_TYPE = "com.coocent.visualizerlib.OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE";

	public static final int TYPE_SPECTRUM = 0;
	public static final int TYPE_LIQUID = 1;
	public static final int TYPE_SPIN = 2;
	public static final int TYPE_PARTICLE = 3;
	public static final int TYPE_IMMERSIVE_PARTICLE = 4;
	public static final int TYPE_IMMERSIVE_PARTICLE_VR = 5;
	public static final int TYPE_SPECTRUM2 = 6;
	public static final int TYPE_LIQUID_POWER_SAVER = 7;
	public static final int TYPE_COLOR_WAVES = 8;

	private final int type;
	private volatile boolean supported, alerted, okToRender;
	private volatile int error;
	private volatile Uri selectedUri;
	private boolean browsing;
	private int colorIndex, speed, viewWidth, viewHeight, diffusion, riseSpeed, ignoreInput;
	private EGLConfig config;
	private Activity activity;
	private WindowManager windowManager;
	private OpenGLVisualizerSensorManager sensorManager;
	@SuppressWarnings("deprecation")
	private Camera camera;
	private SurfaceTexture cameraTexture;
	private int cameraNativeOrientation;
	private volatile boolean cameraOK;

	public Context context;
	private int mTextureID = -1;
	private DirectDrawer mDirectDrawer;
	private int mCameraId = -1;

	public OpenGLVisualizerJni(Context context) {
		super(context);
		this.context=context;
		type = 0;
	}

	public OpenGLVisualizerJni(Activity activity, boolean landscape, Intent extras) {
		super(activity);
		this.context = activity;
		VisualizerManager.getInstance().setVisualizerMenu(this);
		final int t = extras.getIntExtra(EXTRA_VISUALIZER_TYPE, TYPE_SPECTRUM);
		type = ((t < TYPE_LIQUID || t > TYPE_COLOR_WAVES) ? TYPE_SPECTRUM : t);
		setClickable(true);
		setFocusableInTouchMode(false);
		setFocusable(false);
		colorIndex = 0;
		speed = ((type == TYPE_COLOR_WAVES) ? 99 : ((type == TYPE_LIQUID || type == TYPE_LIQUID_POWER_SAVER) ? 0 : 2));
		diffusion = ((type == TYPE_IMMERSIVE_PARTICLE_VR) ? 3 : 1);
		riseSpeed = ((type == TYPE_IMMERSIVE_PARTICLE_VR) ? 3 : 2);
		ignoreInput = 0;
		this.activity = activity;

		//initialize these with default values to be used in
		if (landscape) {
			viewWidth = 1024;
			viewHeight = 512;
		} else {
			viewWidth = 512;
			viewHeight = 1024;
		}

		if (GLVersion != -1) {
			supported = (GLVersion >= 0x00020000);
			if (!supported)
				MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
		}

		try {
			windowManager = (WindowManager)VisualizerManager.getInstance().getApplication().getSystemService(Context.WINDOW_SERVICE);
		} catch (Throwable ex) {
			windowManager = null;
		}

		if (type == TYPE_IMMERSIVE_PARTICLE || type == TYPE_IMMERSIVE_PARTICLE_VR) {
			sensorManager = new OpenGLVisualizerSensorManager(false);
			if (!sensorManager.isCapable) {
				sensorManager = null;
				UI.toast(R.string.msg_no_sensors);
			} else {
				sensorManager.start();
				CharSequence originalText = VisualizerManager.getInstance().getApplication().getText(R.string.msg_immersive);
				final int iconIdx = originalText.toString().indexOf('\u21BA');
				if (iconIdx >= 0) {
					final SpannableStringBuilder txt = new SpannableStringBuilder(originalText);
					txt.setSpan(new ImageSpan(new TextIconDrawable(UI.ICON_3DPAN, UI.colorState_text_visualizer_static.getDefaultColor(), UI._22sp, 0), DynamicDrawableSpan.ALIGN_BASELINE), iconIdx, iconIdx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					originalText = txt;
				}
				UI.customToast(originalText, true, UI._22sp, UI.colorState_text_visualizer_static.getDefaultColor(), new ColorDrawable(0x7f000000 | (UI.color_visualizer565 & 0x00ffffff)));
			}
		}else{
			if(sensorManager!=null){
				LogUtils.d("","##"+"sensorManager is released.");
				sensorManager.release();
				sensorManager=null;
			}
		}
		
		//http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.3_r1/android/opengl/GLSurfaceView.java
		//getHolder().setFormat(PixelFormat.RGB_565);
		//setEGLContextClientVersion(2);
		//setEGLConfigChooser(5, 6, 5, 0, 0, 0);
		setEGLContextFactory(this);
		setEGLWindowSurfaceFactory(this);
		setRenderer(this);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
      //  setRenderMode(RENDERMODE_WHEN_DIRTY);//RENDERMODE_WHEN_DIRTY  RENDERMODE_CONTINUOUSLY
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			setPreserveEGLContext();

		//读一下之前保存的url
		if(PermissionUtils.hasWriteAndReadPermission(activity)){
			if(type==TYPE_LIQUID){
				String path=VisualizerManager.getInstance().getLiquidType1Url();
				try{
					selectedUri=FileUtils.getMediaUriFromPath(activity,path);
				}catch (Exception e){
					LogUtils.d("异常"+e.getMessage());
				}
			}else if(type==TYPE_LIQUID_POWER_SAVER){
				String path=VisualizerManager.getInstance().getLiquidType2Url();
				try{
					selectedUri=FileUtils.getMediaUriFromPath(activity,path);
				}catch (Exception e){
					LogUtils.d("异常"+e.getMessage());
				}
			}
		}
	}

	/**
	 * GLSurfaceView 开始创建时执行 在子线程中执行
	 * 进行一些版本判断比如是否支持OpenGL相关的东西
	 * @param gl
	 * @param config
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//mTextureID = createTextureID();

		if (type == TYPE_SPECTRUM)
			SimpleVisualizerJni.commonSetColorIndex(colorIndex);
		SimpleVisualizerJni.commonSetSpeed(speed);
		if (GLVersion == -1) {
			supported = true;
			try {
				//https://www.khronos.org/opengles/sdk/docs/man/xhtml/glGetString.xml
				final String version = gl.glGetString(GL10.GL_VERSION).toLowerCase(Locale.US);
				if (version.indexOf("opengl es ") == 0 &&
						version.length() > 12 &&
						version.charAt(10) >= '0' &&
						version.charAt(10) <= '9') {
					final int len = version.length();
					GLVersion = 0;
					int i = 10;
					char c = 0;
					for (; i < len; i++) {
						c = version.charAt(i);
						if (c < '0' || c > '9')
							break;
						GLVersion = (GLVersion << 4) | ((c - '0') << 16);
					}
					if (GLVersion == 0) {
						GLVersion = -1;
					} else {
						if (c == '.') {
							i++;
							int shift = 12;
							for (; i < len; i++) {
								if (shift < 0)
									break;
								c = version.charAt(i);
								if (c < '0' || c > '9')
									break;
								GLVersion |= ((c - '0') << shift);
								shift -= 4;
							}
						}
						supported = (GLVersion >= 0x00020000);
						if (!supported)
							MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
					}
				}
			} catch (Throwable ex) {
				GLVersion = -1;
				ex.printStackTrace();
			}
			if (GLVersion == -1) {
				//if the method above fails, try to get opengl version the hard way!
				Process ifc = null;
				BufferedReader bis = null;
				try {
					ifc = Runtime.getRuntime().exec("getprop ro.opengles.version");
					bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
					String line = bis.readLine();
					GLVersion = Integer.parseInt(line);
					supported = (GLVersion >= 0x00020000);
					if (!supported)
						MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
				} catch (Throwable ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (bis != null)
							bis.close();
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
					try {
						if (ifc != null)
							ifc.destroy();
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		if (!supported)
			return;
		//如果有相机功能的
		if (type == TYPE_IMMERSIVE_PARTICLE_VR) {
			final String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
			if (!extensions.contains("OES_EGL_image_external")) {
				error = 0;
				supported = false;
				MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
				return;
			}
			synchronized (this) {
				try {
					int cameraId = -1;
					final Camera.CameraInfo info = new Camera.CameraInfo();
					final int numberOfCameras = Camera.getNumberOfCameras();
					for (int i = 0; i < numberOfCameras; i++) {
						Camera.getCameraInfo(i, info);
						if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
							cameraId = i;
							break;
						}
					}
					if (cameraId == -1) {
						Camera.getCameraInfo(0, info);
						cameraId = 0;
					}
					mCameraId = cameraId;
					camera = ((cameraId >= 0) ? Camera.open(cameraId) : null);

					//setCameraDisplayOrientation(info,camera);

					//cameraNativeOrientation = info.orientation;
					//int degree = getDisplayOrientation(getDisplayRotation(),mCameraId);
					//camera.setDisplayOrientation(0);

				} catch (Throwable ex) {
					if (camera != null) {
						camera.release();
						camera = null;
					}
				}
				if (camera == null) {
					error = 0;
					supported = false;
					MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
					return;
				}
			}
		}
		if ((error = SimpleVisualizerJni.glOnSurfaceCreated(UI.color_visualizer, type,
				UI.screenWidth, UI.screenHeight, (UI._1dp < 2) ? 1 : 0,
				(sensorManager != null && sensorManager.hasGyro) ? 1 : 0)) != 0) {
			supported = false;
			MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
		} else if (type == TYPE_IMMERSIVE_PARTICLE_VR &&
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			synchronized (this) {
				cameraTexture = new SurfaceTexture(SimpleVisualizerJni.glGetOESTexture());
				//cameraTexture = new SurfaceTexture(mTextureID);
				//	mDirectDrawer = new DirectDrawer(mTextureID);
				cameraTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
					@Override
					public void onFrameAvailable(SurfaceTexture surfaceTexture) {
						cameraOK = true;
					}
				});
				try {
					camera.setPreviewTexture(cameraTexture);
//					int degree = getDisplayOrientation(getDisplayRotation(),mCameraId);
//					camera.setDisplayOrientation(180);
//					camera.setParameters(camera.getParameters());
				} catch (Throwable ex) {
					releaseCamera();
					error = 0;
					supported = false;
					MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
				}
			}
		}
	}

	/**
	 * GLSurfaceView 变化的时候执行，同样在子线程中执行
	 * @param gl
	 * @param width
	 * @param height
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//	GLES20.glViewport(0, 0, width, height);
		if (!supported)
			return;
		//http://developer.download.nvidia.com/tegra/docs/tegra_android_accelerometer_v5f.pdf
		int rotation;
		if (windowManager != null) {
			try {
				rotation = windowManager.getDefaultDisplay().getRotation();
			} catch (Throwable ex) {
				//silly assumption for phones....
				rotation = ((width < height) ? Surface.ROTATION_90 : Surface.ROTATION_0);
			}
		} else {
			//silly assumption for phones....
			rotation = ((width < height) ? Surface.ROTATION_90 : Surface.ROTATION_0);
		}

		int cameraPreviewW = 0, cameraPreviewH = 0;
		if (camera != null) {
			synchronized (this) {
				try {
					int degrees = 0;
					switch (rotation) {
						case Surface.ROTATION_0:
							degrees = 0;
							break;
						case Surface.ROTATION_90:
							degrees = 90;
							break;
						case Surface.ROTATION_180:
							degrees = 180;
							break;
						case Surface.ROTATION_270:
							degrees = 270;
							break;
					}
					final int cameraDisplayOrientation = (cameraNativeOrientation - degrees + 360) % 360;

					//LogUtils.d("cameraNativeOrientation="+cameraDisplayOrientation+" 预览旋转角度为："+cameraDisplayOrientation);

					//	int degree = getDisplayOrientation(getDisplayRotation(),mCameraId);
					//camera.setDisplayOrientation(0);

					//	camera.setDisplayOrientation(cameraDisplayOrientation);
					//LogUtils.e("nsc","onSurfaceChanged degree =="+degree);
					final Camera.Parameters parameters = camera.getParameters();

					//try to find the ideal preview size...
					final List<Camera.Size> localSizes = parameters.getSupportedPreviewSizes();
					int largestW = 0, largestH = 0;
					float smallestRatioError = 10000.0f;
					final float viewRatio = (float)width / (float)height;
					if (cameraDisplayOrientation == 0 || cameraDisplayOrientation == 180) {
						for (int i = localSizes.size() - 1; i >= 0; i--) {
							final int w = localSizes.get(i).width, h = localSizes.get(i).height;
							final float ratioError = Math.abs(((float)w / (float)h) - viewRatio);
							if (w < width && h < height && w >= largestW && h >= largestH && ratioError <= (smallestRatioError + 0.001f)) {
								smallestRatioError = ratioError;
								largestW = w;
								largestH = h;
								cameraPreviewW = w;
								cameraPreviewH = h;
							}
						}
					} else {
						//getSupportedPreviewSizes IS NOT AFFECTED BY setDisplayOrientation
						//therefore, w and h MUST BE SWAPPED in 3 places
						for (int i = localSizes.size() - 1; i >= 0; i--) {
							final int w = localSizes.get(i).width, h = localSizes.get(i).height;
							//SWAP HERE
							final float ratioError = Math.abs(((float)h / (float)w) - viewRatio);
							if (h < width && w < height //SWAP HERE
									&& w >= largestW && h >= largestH && ratioError <= (smallestRatioError + 0.001f)) {
								smallestRatioError = ratioError;
								largestW = w;
								largestH = h;
								//SWAP HERE
								cameraPreviewW = h;
								cameraPreviewH = w;
							}
						}
					}
					if (largestW == 0) {
						largestW = localSizes.get(0).width;
						largestH = localSizes.get(0).height;
					}

					parameters.setPreviewSize(largestW, largestH);

					final List<String> localFocusModes = parameters.getSupportedFocusModes();
					if (localFocusModes != null) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
								localFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
							parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
						} else if (localFocusModes.contains(Camera.Parameters.FOCUS_MODE_EDOF)) {
							parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
						} else if (localFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
							parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
						}
					}

					final List<String> localWhiteBalance = parameters.getSupportedWhiteBalance();
					if (localWhiteBalance != null && localWhiteBalance.contains(Camera.Parameters.WHITE_BALANCE_AUTO))
						parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

					camera.setParameters(parameters);
					camera.startPreview();
					//camera.getParameters();
				} catch (Throwable ex) {
					releaseCamera();
				}
			}
		}

		viewWidth = width;
		viewHeight = height;
		SimpleVisualizerJni.glOnSurfaceChanged(width, height, rotation, cameraPreviewW, cameraPreviewH, (UI._1dp < 2) ? 1 : 0);
		okToRender = true;
	}

	@Override
	public EGLContext createContext(final EGL10 egl, final EGLDisplay display, EGLConfig config) {

		//https://www.khronos.org/registry/egl/sdk/docs/man/html/eglChooseConfig.xhtml
		//https://www.khronos.org/registry/egl/sdk/docs/man/html/eglCreateContext.xhtml

		egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		this.config = null;
		//EGL_FALSE = 0
		//EGL_TRUE = 1
		//EGL_OPENGL_ES2_BIT = 4
		//EGL_CONTEXT_CLIENT_VERSION = 0x3098
		final EGLConfig[] configs = new EGLConfig[64], selectedConfigs = new EGLConfig[64];
		final int[] num_config = { 0 }, value = new int[1];
		final int[] none = { EGL10.EGL_NONE };
		final int[] v2 = { 0x3098, 2, EGL10.EGL_NONE };
		int selectedCount = 0;
		if (egl.eglGetConfigs(display, configs, 32, num_config) && num_config[0] > 0) {
			for (int i = 0; i < num_config[0]; i++) {
				egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_RENDERABLE_TYPE, value);
				if ((value[0] & 4) == 0) continue;
				egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_SURFACE_TYPE, value);
				if ((value[0] & EGL10.EGL_WINDOW_BIT) == 0) continue;
				//egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_COLOR_BUFFER_TYPE, value);
				//if (value[0] != EGL10.EGL_RGB_BUFFER) continue;
				egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_RED_SIZE, value);
				if (value[0] < 4) continue;
				egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_GREEN_SIZE, value);
				if (value[0] < 4) continue;
				egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_BLUE_SIZE, value);
				if (value[0] < 4) continue;
				selectedConfigs[selectedCount++] = configs[i];
			}
		}
		if (selectedCount == 0) {
			supported = false;
			MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
			return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, none);
		}
		ArraySorter.sort(selectedConfigs, 0, selectedCount, new ArraySorter.Comparer<EGLConfig>() {
			@Override
			public int compare(EGLConfig a, EGLConfig b) {
				int x;
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_COLOR_BUFFER_TYPE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_COLOR_BUFFER_TYPE, value);
				//prefer rgb buffers
				if (x != value[0])
					return (x == EGL10.EGL_RGB_BUFFER) ? -1 : 1;
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_NATIVE_RENDERABLE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_NATIVE_RENDERABLE, value);
				//prefer native configs
				if (x != value[0])
					return (x != 0) ? -1 : 1;
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_SAMPLE_BUFFERS, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_SAMPLE_BUFFERS, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_SAMPLES, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_SAMPLES, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_BUFFER_SIZE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_BUFFER_SIZE, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_DEPTH_SIZE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_DEPTH_SIZE, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_STENCIL_SIZE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_STENCIL_SIZE, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_ALPHA_MASK_SIZE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_ALPHA_MASK_SIZE, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_ALPHA_SIZE, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_ALPHA_SIZE, value);
				//prefer smaller values
				if (x != value[0])
					return (x - value[0]);
				egl.eglGetConfigAttrib(display, a, EGL10.EGL_CONFIG_ID, value);
				x = value[0];
				egl.eglGetConfigAttrib(display, b, EGL10.EGL_CONFIG_ID, value);
				//prefer smaller values
				return (x - value[0]);
			}
		});
		//according to this:
		//http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.3_r1/android/opengl/GLSurfaceView.java#941
		//the native_window parameter in cretaeWindowSurface is this SurfaceHolder
		final SurfaceHolder holder = getHolder();
		for (int i = 0; i < selectedCount; i++) {
			final int r, g, b;
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_BUFFER_SIZE, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_SAMPLE_BUFFERS, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_SAMPLES, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_DEPTH_SIZE, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_STENCIL_SIZE, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_ALPHA_SIZE, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_ALPHA_MASK_SIZE, value);
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_RED_SIZE, value);
			r = value[0];
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_GREEN_SIZE, value);
			g = value[0];
			egl.eglGetConfigAttrib(display, selectedConfigs[i], EGL10.EGL_BLUE_SIZE, value);
			b = value[0];
			if (r != 8 || g != 8 || b != 8) {
				if (r != 5 || g != 6 || b != 5)
					continue;
			}
			EGLSurface surface = null;
			try {
				this.config = selectedConfigs[i];
				EGLContext ctx = egl.eglCreateContext(display, this.config, EGL10.EGL_NO_CONTEXT, v2);
				if (ctx == null || ctx == EGL10.EGL_NO_CONTEXT)
					ctx = egl.eglCreateContext(display, this.config, EGL10.EGL_NO_CONTEXT, none);
				if (ctx != null && ctx != EGL10.EGL_NO_CONTEXT) {
					//try to create a surface and make it current successfully
					//before confirming this is the right config/context

					//下面这句代码可能会导致ANR
//					holder.setFormat((r == 5) ? PixelFormat.RGB_565 : PixelFormat.RGBA_8888);
					surface = egl.eglCreateWindowSurface(display, this.config, holder, null);
					if (surface != null && surface != EGL10.EGL_NO_SURFACE) {
						//try to make current
						if (egl.eglMakeCurrent(display, surface, surface, ctx)) {
							//yes! this combination works!!!
							return ctx;
						}
					}
					// clean up before trying again...
					egl.eglDestroyContext(display, ctx);
					this.config = null;
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			} finally {
				egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				if (surface != null && surface != EGL10.EGL_NO_SURFACE)
					egl.eglDestroySurface(display, surface);
			}
		}
		this.config = null;
		supported = false;
		MainHandler.sendMessage(this, MSG_OPENGL_ERROR);
		return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, none);
	}

	@Override
	public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object native_window) {
		try {
			return egl.eglCreateWindowSurface(display, (this.config != null) ? this.config : config, native_window, null);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 关键实时绘制过程
	 * @param gl
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if (okToRender) {
			if (selectedUri != null) {
				loadBitmap();
				selectedUri = null;
			}
			if (cameraOK && cameraTexture != null &&
					Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				synchronized (this) {
					if (cameraTexture != null){
						cameraTexture.updateTexImage();
//						float[] mtx = new float[16];
//						cameraTexture.getTransformMatrix(mtx);
//						mDirectDrawer.draw(mtx,mCameraId);
					}
				}
			}
			SimpleVisualizerJni.glDrawFrame();
		}
	}

	@Override
	public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
		if (egl != null && display != null && surface != null)
			egl.eglDestroySurface(display, surface);
	}

	@Override
	public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
		if (egl != null && display != null && context != null)
			egl.eglDestroyContext(display, context);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//is it really necessary to call any cleanup code for OpenGL in Android??????
		okToRender = false;
		super.surfaceDestroyed(holder);
		//some times surfaceDestroyed is called, but after resuming,
		//onSurfaceCreated is not called, only onSurfaceChanged is! :/
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case MSG_OPENGL_ERROR:
				if (!alerted) {
					alerted = true;
					UI.toast(VisualizerManager.getInstance().getApplication().getText(R.string.sorry) + " "
							+ ((error != 0) ? (VisualizerManager.getInstance().getApplication().getText(R.string.opengl_error).toString()
							+ UI.collon() + error) : VisualizerManager.getInstance().getApplication().getText(R.string.opengl_not_supported).toString()) + " :(");
				}
				break;
			case MSG_CHOOSE_IMAGE:
				chooseImage();
				break;
		}
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		final int id = item.getItemId();
		switch (id) {
			case MNU_COLOR:
//			colorIndex = ((colorIndex == 0) ? 257 : 0);
//			SimpleVisualizerJni.commonSetColorIndex(colorIndex);

				changeColor();

				break;
			case MNU_SPEED0:
			case MNU_SPEED1:
			case MNU_SPEED2:
				speed = id - MNU_SPEED0;
				SimpleVisualizerJni.commonSetSpeed(speed);
				break;
			case MNU_SPEED_FASTEST:
				speed = 99;
				SimpleVisualizerJni.commonSetSpeed(speed);
				break;
			case MNU_DIFFUSION0:
			case MNU_DIFFUSION1:
			case MNU_DIFFUSION2:
			case MNU_DIFFUSION3:
				diffusion = id - MNU_DIFFUSION0;
				SimpleVisualizerJni.glSetImmersiveCfg(diffusion, -1);
				break;
			case MNU_RISESPEED0:
			case MNU_RISESPEED1:
			case MNU_RISESPEED2:
			case MNU_RISESPEED3:
				riseSpeed = id - MNU_RISESPEED0;
				SimpleVisualizerJni.glSetImmersiveCfg(-1, riseSpeed);
				break;
			case MNU_CHOOSE_IMAGE:
				chooseImage();
				break;

			case MNU_CLEAR_IMAGE:
				clearImage();
				break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Object intent) {
		if (requestCode == ACTIVITY_CHOOSE_IMAGE) {
			browsing = false;
			if (resultCode == Activity.RESULT_OK&&activity!=null){
				selectedUri = ((Intent)intent).getData();
				if(selectedUri==null){
					//selectedUri= ImageUtils.geturi(activity,(Intent)intent);
				}
				LogUtils.d("返回图片URI为："+selectedUri);
				String path=FileUtils.getFilePathByUri(activity,selectedUri);
				if(type==TYPE_LIQUID){
					VisualizerManager.getInstance().setLiquidType1Url(path);
				}else if(type==TYPE_LIQUID_POWER_SAVER){
					VisualizerManager.getInstance().setLiquidType2Url(path);
				}
			}
		}
	}

	@Override
	public void onActivityPause() {
		if (sensorManager != null)
			sensorManager.unregister();
	}

	@Override
	public void onActivityResume() {
		if (sensorManager != null) {
			sensorManager.reset();
			sensorManager.register();
		}
	}

	@Override
	public void onCreateContextMenu(Object contextMenu) {
		final ContextMenu menu = (ContextMenu)contextMenu;
		Menu s;
		boolean itemsAdded = false;
		switch (type) {
			case TYPE_LIQUID:
			case TYPE_LIQUID_POWER_SAVER:
				itemsAdded = true;
				menu.add(1, MNU_CHOOSE_IMAGE, 1, R.string.choose_image)
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable(UI.ICON_PALETTE));

				menu.add(1, MNU_CLEAR_IMAGE, 1, R.string.clear_image)
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable(UI.ICON_DELETE));

				break;
			case TYPE_SPIN:
			case TYPE_PARTICLE:
			case TYPE_COLOR_WAVES:
				break;
			case TYPE_IMMERSIVE_PARTICLE:
			case TYPE_IMMERSIVE_PARTICLE_VR:
				itemsAdded = true;
				s = menu.addSubMenu(1, 0, 1, VisualizerManager.getInstance().getApplication().getText(R.string.diffusion) + "\u2026")
						.setIcon(new TextIconDrawable(UI.ICON_SETTINGS));
				UI.prepare(s);
				s.add(0, MNU_DIFFUSION0, 0, VisualizerManager.getInstance().getApplication().getText(R.string.diffusion) + UI.punctuationSpace(": 0"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((diffusion == 0) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s.add(0, MNU_DIFFUSION1, 1, VisualizerManager.getInstance().getApplication().getText(R.string.diffusion) + UI.punctuationSpace(": 1"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((diffusion != 0 && diffusion != 2 && diffusion != 3) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s.add(0, MNU_DIFFUSION2, 2, VisualizerManager.getInstance().getApplication().getText(R.string.diffusion) + UI.punctuationSpace(": 2"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((diffusion == 2) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s.add(0, MNU_DIFFUSION3, 3, VisualizerManager.getInstance().getApplication().getText(R.string.diffusion) + UI.punctuationSpace(": 3"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((diffusion == 3) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s = menu.addSubMenu(1, 0, 2, VisualizerManager.getInstance().getApplication().getText(R.string.speed) + "\u2026")
						.setIcon(new TextIconDrawable(UI.ICON_SETTINGS));
				UI.prepare(s);
				s.add(0, MNU_RISESPEED0, 0, VisualizerManager.getInstance().getApplication().getText(R.string.speed) + UI.punctuationSpace(": 0"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((riseSpeed == 0) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s.add(0, MNU_RISESPEED1, 1, VisualizerManager.getInstance().getApplication().getText(R.string.speed) + UI.punctuationSpace(": 1"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((riseSpeed != 0 && riseSpeed != 2 && riseSpeed != 3) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s.add(0, MNU_RISESPEED2, 2, VisualizerManager.getInstance().getApplication().getText(R.string.speed) + UI.punctuationSpace(": 2"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((riseSpeed == 2) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				s.add(0, MNU_RISESPEED3, 3, VisualizerManager.getInstance().getApplication().getText(R.string.speed) + UI.punctuationSpace(": 3"))
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable((riseSpeed == 3) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
				break;
			default:
				itemsAdded = true;
//			menu.add(1, MNU_COLOR, 1, (colorIndex == 0) ? R.string.green : R.string.blue)
//				.setOnMenuItemClickListener(this)
//				.setIcon(new TextIconDrawable(UI.ICON_PALETTE));

				menu.add(1, MNU_COLOR, 1, R.string.change_color)
						.setOnMenuItemClickListener(this)
						.setIcon(new TextIconDrawable(UI.ICON_PALETTE));

				break;
		}
		if (itemsAdded)
			UI.separator(menu, 2, 0);
		menu.add(2, MNU_SPEED0, 1, VisualizerManager.getInstance().getApplication().getText(R.string.sustain) + " 3")
				.setOnMenuItemClickListener(this)
				.setIcon(new TextIconDrawable((speed != 1 && speed != 2 && speed != 99) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
		menu.add(2, MNU_SPEED1, 2, VisualizerManager.getInstance().getApplication().getText(R.string.sustain) + " 2")
				.setOnMenuItemClickListener(this)
				.setIcon(new TextIconDrawable((speed == 1) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
		menu.add(2, MNU_SPEED2, 3, VisualizerManager.getInstance().getApplication().getText(R.string.sustain) + " 1")
				.setOnMenuItemClickListener(this)
				.setIcon(new TextIconDrawable((speed == 2) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
		menu.add(2, MNU_SPEED_FASTEST, 3, VisualizerManager.getInstance().getApplication().getText(R.string.sustain) + " " + VisualizerManager.getInstance().getApplication().getText(R.string.none))
				.setOnMenuItemClickListener(this)
				.setIcon(new TextIconDrawable((speed == 99) ? UI.ICON_RADIOCHK24 : UI.ICON_RADIOUNCHK24));
	}

	//Runs on the MAIN thread
	@Override
	public void configurationChanged(boolean landscape) {
	}

	//Runs on a SECONDARY thread (B)
	@Override
	public void processFrame(boolean playing, byte[] waveform) {
		if (okToRender) {
			//We use ignoreInput because taking 1024 samples, 60 times a seconds,
			//is useless, as there are only 44100 or 48000 samples in one second
			if (ignoreInput == 0 && !playing)
				Arrays.fill(waveform, (byte)0x80);
			SimpleVisualizerJni.commonProcess(waveform, ignoreInput | DATA_FFT);
			if (speed == 99)
				ignoreInput = 0;
			else
				ignoreInput ^= IGNORE_INPUT;
			//requestRender();
		}
	}

	//Runs on a SECONDARY thread (B)
	@Override
	public void release() {
		synchronized (this) {
			releaseCamera();
			releaseSensor();
		}
	}

	//Runs on the MAIN thread (AFTER Visualizer.release())
	@Override
	public void releaseView() {
		windowManager = null;
		if (sensorManager != null) {
			sensorManager.release();
			sensorManager = null;
		}
		activity = null;
		SimpleVisualizerJni.glReleaseView();
	}

	@Override
	public void releaseSensor() {
		if(sensorManager!=null){
			LogUtils.d("","#releaseSensor#"+"sensorManager is released.");
			sensorManager.release();
			sensorManager=null;
		}
	}

	@Override
	public void onClick() {
	}

	@Override
	public void onPlayerChanged(SongInfo currentSongInfo, boolean songHasChanged, Throwable ex) {
	}

	@Override
	public boolean isFullscreen() {
		return true;
	}

	@Override
	public Object getDesiredSize(int availableWidth, int availableHeight) {
		return new Point(availableWidth, availableHeight);
	}

	@Override
	public boolean requiresHiddenControls() {
		return true;
	}

	//Runs on ANY thread
	@Override
	public int requiredDataType() {
		return DATA_FFT;
	}

	//Runs on ANY thread
	@Override
	public int requiredOrientation() {
		return (type == TYPE_IMMERSIVE_PARTICLE_VR ? ORIENTATION_PORTRAIT : ORIENTATION_NONE);
//		return ORIENTATION_NONE;
	}

	//Runs on a SECONDARY thread (B)
	@Override
	public void load() {
	}

	//Runs on ANY thread
	@Override
	public boolean isLoading() {
		return false;
	}

	//Runs on ANY thread
	@Override
	public void cancelLoading() {
	}

	@Override
	public void changeImageUri(Uri selectedUri1) {
		if(selectedUri1!=null){
			selectedUri=selectedUri1;
			String path=FileUtils.getFilePathByUri(activity,selectedUri);
			if(type==TYPE_LIQUID){
				VisualizerManager.getInstance().setLiquidType1Url(path);
			}else if(type==TYPE_LIQUID_POWER_SAVER){
				VisualizerManager.getInstance().setLiquidType2Url(path);
			}
		}else{
			//这里用于自己清楚图片
			selectedUri=null;
			LogUtils.d("用户清除了图片");
			if(type==TYPE_LIQUID){
				VisualizerManager.getInstance().setLiquidType1Url("");
			}else if(type==TYPE_LIQUID_POWER_SAVER){
				VisualizerManager.getInstance().setLiquidType2Url("");
			}
			if(VisualizerManager.getInstance().getControlVisualizer()!=null){
				VisualizerManager.getInstance().getControlVisualizer().
						someVisualizer(VisualizerManager.getInstance().visualizerIndex);
			}

		}
		//loadBitmap();
	}

	@Override
	public void changeImagePath(String path) {
		if(path!=null){
			selectedUri=FileUtils.getMediaUriFromPath(activity,path);
			if(type==TYPE_LIQUID){
				VisualizerManager.getInstance().setLiquidType1Url(path);
			}else if(type==TYPE_LIQUID_POWER_SAVER){
				VisualizerManager.getInstance().setLiquidType2Url(path);
			}
		}else{
			//这里用于自己清除图片
			selectedUri=null;
			LogUtils.d("用户清除了图片");
			if(type==TYPE_LIQUID){
				VisualizerManager.getInstance().setLiquidType1Url("");
			}else if(type==TYPE_LIQUID_POWER_SAVER){
				VisualizerManager.getInstance().setLiquidType2Url("");
			}
			if(VisualizerManager.getInstance().getControlVisualizer()!=null){
				VisualizerManager.getInstance().getControlVisualizer().
						someVisualizer(VisualizerManager.getInstance().visualizerIndex);
			}

		}
	}

	@Override
	public void changeColor() {
//		colorIndex = ((colorIndex == 0) ? 257 : 0);
//		colorIndex=colorIndex+10;

		CustomColorPickDialogUtils.showColorPickDialog(activity,getColorByColorIndex(colorIndex),new CustomColorPickDialogUtils.DialogOKOrCancel() {
			@Override
			public void onClickOK(int colorPosition) {
				colorIndex=Constants.colorsIndex[colorPosition];
				SimpleVisualizerJni.commonSetColorIndex(colorIndex);
			}
		});
	}

	private int createTextureID() {
		int[] texture = new int[1];
		GLES20.glGenTextures(1, texture, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		return texture[0];
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setPreserveEGLContext() {
		setPreserveEGLContextOnPause(false);
	}

	private void releaseCamera() {
		if (camera != null) {
			try {
				camera.stopPreview();
			} catch (Throwable ex2) {
				ex2.printStackTrace();
			}
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					camera.setPreviewTexture(null);
			} catch (Throwable ex2) {
				ex2.printStackTrace();
			}
			try {
				camera.release();
			} catch (Throwable ex2) {
				ex2.printStackTrace();
			}
			camera = null;
			cameraOK = false;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (cameraTexture != null) {
				cameraTexture.setOnFrameAvailableListener(null);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					cameraTexture.release();
				cameraTexture = null;
			}
		}
	}

	public int getDisplayRotation() {
		int i = windowManager.getDefaultDisplay().getRotation();
		switch (i) {
			case Surface.ROTATION_0:
				return 0;
			case Surface.ROTATION_90:
				return 90;
			case Surface.ROTATION_180:
				return 180;
			case Surface.ROTATION_270:
				return 270;
		}
		return 0;
	}

	public int getDisplayOrientation(int degrees, int cameraId) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}
	
	//Runs on the MAIN thread

	@SuppressWarnings("deprecation")
	private void loadBitmap() {
		if (selectedUri == null || activity==null)
			return;

		/*String path = null;
		int orientation = 1;

		//Try to fetch the image's rotation from EXIF (this process needs the exact path)
		//Based on: http://stackoverflow.com/q/2169649/3569421
		try {
			final String[] projection = {MediaStore.Images.Media.DATA};
			final Cursor cursor = activity.managedQuery(selectedUri, projection, null, null, null);
			if (cursor != null) {
				final int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
				if (column_index >= 0) {
					cursor.moveToFirst();
					path = cursor.getString(column_index);
				}
			}
		} catch (Throwable ex) {
		}
		try {
			//OI FILE Manager
			if (path == null)
				path = selectedUri.getPath();
			orientation = (new ExifInterface(path)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		} catch (Throwable ex) {
		}*/
//content://com.android.providers.media.documents/document/image%3A240219
		InputStream input = null;
		Bitmap bitmap = null;
		try {
			input = activity.getContentResolver().openInputStream(selectedUri);
			if (input == null){
				return;
			}

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(input, null, opts);
			input.close();
			input = null;

			final int maxDim = Math.max(320, Math.min(1024, Math.max(viewWidth, viewHeight)));

			opts.inSampleSize = 1;
			int largest = ((opts.outWidth >= opts.outHeight) ? opts.outWidth : opts.outHeight);
			while (largest > maxDim) {
				opts.inSampleSize <<= 1;
				largest >>= 1;
			}
			input =  activity.getContentResolver().openInputStream(selectedUri);
			opts.inJustDecodeBounds = false;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inDither = true;
			bitmap = BitmapFactory.decodeStream(input, null, opts);

			if (bitmap != null) {
//				if (opts.outWidth != opts.outHeight && ((opts.outWidth > opts.outHeight) != (viewWidth > viewHeight))) {
					//rotate the image 90 degress
					final Matrix matrix = new Matrix();
					//matrix.postRotate(-90);
					final Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
					if (bitmap != newBitmap && newBitmap != null) {
						bitmap.recycle();
						bitmap = newBitmap;
					}
					System.gc();
//				}
				SimpleVisualizerJni.glLoadBitmapFromJava(bitmap);
			}
		} catch (Throwable ex) {
		    Log.e("nsc","Throwable ex="+ex.getMessage());
			ex.printStackTrace();
			LogUtils.d("绘制图片异常"+ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			if (bitmap != null)
				bitmap.recycle();
			System.gc();
		}
	}
	
	@Override
	@ExportedProperty(category = "drawing")
	public final boolean isOpaque() {
		return true;
	}

	private void chooseImage() {
		//Based on: http://stackoverflow.com/a/20177611/3569421
		//Based on: http://stackoverflow.com/a/4105966/3569421

		if(activity!=null){
			if(PermissionUtils.hasWriteAndReadPermission(activity)){
				if (activity != null && selectedUri == null && !browsing && okToRender) {
					browsing = true;
					final Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					//intent.setAction(Intent.ACTION_PICK);
					LogUtils.d("即将选择图片~");
					activity.startActivityForResult(intent, ACTIVITY_CHOOSE_IMAGE);

				}
			}else{
				PermissionUtils.requestWriteAndReadPermissionInActivity(activity,READ_AND_WEITE_PERMISSION_CODE);
			}
		}
	}

	/**
	 * 清除图片
	 */
	private void clearImage() {
		//Based on: http://stackoverflow.com/a/20177611/3569421
		//Based on: http://stackoverflow.com/a/4105966/3569421
		//LogUtils.d("用户清除了图片");
		if(type==TYPE_LIQUID){
			VisualizerManager.getInstance().setLiquidType1Url("");
		}else if(type==TYPE_LIQUID_POWER_SAVER){
			VisualizerManager.getInstance().setLiquidType2Url("");
		}
		if(VisualizerManager.getInstance().getControlVisualizer()!=null){
			VisualizerManager.getInstance().getControlVisualizer().
					someVisualizer(VisualizerManager.getInstance().visualizerIndex);
		}
	}

	/**
	 * @param camera   相机对象
	 */
	public void setCameraDisplayOrientation(Camera.CameraInfo info, Camera camera) {
		if(activity==null) return;

		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {
			// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(180);
	}

    /**
     * 通过颜色值获取colorIndex
     * @return
     */
	public int getColorIndexByColors(int color){

        switch (color){
            case Color.BLUE:
                return Constants.colorsIndex[0];

            case Color.RED:
                return Constants.colorsIndex[1];

            case Color.YELLOW:
                return Constants.colorsIndex[2];

            case Color.GREEN:
                return Constants.colorsIndex[3];

            case Color.GRAY:
                return Constants.colorsIndex[4];
        }

        return Constants.colorsIndex[0];
    }

    /**
     * 通过索引获取颜色值
     * @return
     */
    public int getColorByColorIndex(int colorIndex){

        switch (colorIndex){
            case 0:
                return Constants.colors[0];

            case 70:
                return Constants.colors[1];

            case 190:
                return Constants.colors[2];

            case 257:
                return Constants.colors[3];

            case 600:
                return Constants.colors[4];
        }

        return Constants.colors[0];
    }
}
