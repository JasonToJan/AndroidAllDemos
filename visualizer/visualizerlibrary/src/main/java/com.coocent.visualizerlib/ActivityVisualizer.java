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
package com.coocent.visualizerlib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.carlosrafaelgn.fplay.visualizer.OpenGLVisualizerJni;


/**
 * 频谱主页
 */
public final class ActivityVisualizer extends Activity implements
		VisualizerService.Observer,
		MainHandler.Callback,
		View.OnClickListener,
		MenuItem.OnMenuItemClickListener,
		OnCreateContextMenuListener,
		View.OnTouchListener,
		Timer.TimerHandler {

	//基本数据
	private boolean visualizerViewFullscreen,
			visualizerRequiresHiddenControls,
			isWindowFocused,
			panelTopWasVisibleOk,
			visualizerPaused;
	private boolean isFinishChange=true;//是否已经完成了改变，重复调用会猜测会异常

	private int version, panelTopLastTime, panelTopHiding, requiredOrientation;
	private static final int MSG_HIDE = 0x0400;
	private static final int MSG_SYSTEM_UI_CHANGED = 0x0401;
	private static final int MNU_ORIENTATION = 100;
	private static final int MNU_DUMMY = 101;
	private float panelTopAlpha;

	//自定义视图相关
	private BgButton btnGoBack, btnMenu;
	private ImageView btnPlay, btnNext;
	private TextView titleTv,artistTv;
	private ImageButton nextVisualizerIb,prevVisualizerIb;
	private InterceptableLayout panelControls;
	private RelativeLayout panelTop;
	private BgColorStateList buttonColor, lblColor;
	private ColorDrawable panelTopBackground;

	//关键类
	private UI.DisplayInfo info;
	private IVisualizer visualizer;
	private VisualizerService visualizerService;
	private Object systemUIObserver;
	private Timer uiAnimTimer;

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		LogUtils.d("打开了频谱页~");

		//初始化相关
		init();
		setContentView(R.layout.activity_visualizer);

		initView();
		addVisualizerView();
		setViewColor();

		//开启一个定时器
		startTimer();
		hideAllUIDelayed();
	}
	
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.clear();
	}

	@Override
	protected void onResume() {
		if (visualizerService != null){
			visualizerService.resetAndResume();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			prepareSystemUIObserver();
		}
		if (visualizer != null && visualizerPaused) {
			visualizerPaused = false;
			visualizer.onActivityResume();
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		//changed from onPaused() to onStop()
		//https://developer.android.com/guide/topics/ui/multi-window.html#lifecycle
		//In multi-window mode, an app can be in the paused state and still be visible to the user.
		if (visualizer != null && !visualizerPaused) {
			visualizerPaused = true;
			visualizer.onActivityPause();
		}
		if (visualizerService != null){
			visualizerService.pause();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		finalCleanup();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		final IVisualizer v = visualizer;
		if (v != null){
			v.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (info == null)
			return;
		if (UI.forcedLocale != UI.LOCALE_NONE){
			UI.reapplyForcedLocale(this);
		}
		UI.prepare(menu);
		boolean firstItem = false;
		if (!UI.isChromebook && requiredOrientation == IVisualizer.ORIENTATION_NONE) {
			menu.add(0, MNU_ORIENTATION, 0, UI.visualizerPortrait ?
					R.string.landscape : R.string.portrait)
					.setOnMenuItemClickListener(this)
					.setIcon(new TextIconDrawable(UI.ICON_ORIENTATION));
			firstItem = true;
		}
		final IVisualizer v = visualizer;
		if (v != null){
			v.onCreateContextMenu(menu);
		}
		if (firstItem && menu.size() > 1){
			UI.separator(menu, 1, 0);
		}
		if (menu.size() < 1){
			menu.add(0, MNU_DUMMY, 0, R.string.empty_list).setOnMenuItemClickListener(this);
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (info == null)
			return true;
		switch (item.getItemId()) {
			case MNU_ORIENTATION:
				UI.visualizerPortrait = !UI.visualizerPortrait;
				setRequestedOrientation(UI.visualizerPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (btnMenu != null){
			CustomContextMenu.openContextMenu(btnMenu, this);
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (info == null)
			return;
		final boolean i = info.isLandscape;
		final int w = info.usableScreenWidth, h = info.usableScreenHeight;
		updateInfoWithConfiguration(newConfig);
		if (i != info.isLandscape || w != info.usableScreenWidth || h != info.usableScreenHeight) {
			final IVisualizer v = visualizer;
			if (v != null)
				v.configurationChanged(info.isLandscape);
			prepareViews(false);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				prepareSystemUIObserver();
			hideAllUIDelayed();
			System.gc();
		}
	}

	//replace onKeyDown with dispatchKeyEvent + event.getAction() + event.getKeyCode()?!?!?!
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		//
		//Allowing applications to play nice(r) with each other: Handling remote control buttons
		//http://android-developers.blogspot.com.br/2010/06/allowing-applications-to-play-nicer.html
		//
		//...In a media playback application, this is used to react to headset button
		//presses when your activity doesn’t have the focus. For when it does, we override
		//the Activity.onKeyDown() or onKeyUp() methods for the user interface to trap the
		//headset button-related events...
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_VOLUME_UP:
//			if (Player.volumeControlType == Player.VOLUME_CONTROL_STREAM) {
//				Player.handleMediaButton(keyCode);
//				return true;
//			}
				break;
			default:
//			if ((event.getRepeatCount() == 0) && Player.handleMediaButton(keyCode))
//				return true;
				break;
		}
		if (panelTop != null && !(panelTopWasVisibleOk = (panelTopHiding == 0 && panelTop.getVisibility() == View.VISIBLE)))
			showPanelTop(true);
		hideAllUIDelayed();
		return super.onKeyDown(keyCode, event);
	}

	//	@Override
//	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//		//return (Player.isMediaButton(keyCode) || super.onKeyLongPress(keyCode, event));
//
//		return false;
//	}
//
//	@Override
//	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
//		//return (Player.isMediaButton(keyCode) || super.onKeyUp(keyCode, event));
//
//		return false;
//	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if ((isWindowFocused = hasFocus))
			hideAllUIDelayed();
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				if (panelTop != null && !(panelTopWasVisibleOk = (panelTopHiding == 0 && panelTop.getVisibility() == View.VISIBLE)))
					showPanelTop(true);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					showSystemUI();
				hideAllUIDelayed();
				break;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		if (view == btnGoBack) {
			finish();
		}  else if (view == btnPlay) {
			//Player.playPause();
		} else if (view == btnNext) {
			//Player.next();
			if (visualizerService != null)
				visualizerService.resetAndResume();
		} else if (view == btnMenu) {
			onPrepareOptionsMenu(null);
		} else if (view == visualizer || view == panelControls) {
			if (visualizer != null && panelTopWasVisibleOk)
				visualizer.onClick();
		} else if(view==nextVisualizerIb){

			if(ApplicationProxy.getInstance().visualizerIndex
					==ApplicationProxy.getInstance().visualizerDataType.length-1){
				ApplicationProxy.getInstance().visualizerIndex=0;
			}else{
				ApplicationProxy.getInstance().visualizerIndex++;
			}

			changeVisualizer(ApplicationProxy.getInstance().visualizerIndex);

		} else if(view==prevVisualizerIb){

			if(ApplicationProxy.getInstance().visualizerIndex ==0){
				ApplicationProxy.getInstance().visualizerIndex=
						ApplicationProxy.getInstance().visualizerDataType.length-1;
			}else{
				ApplicationProxy.getInstance().visualizerIndex--;
			}
			changeVisualizer(ApplicationProxy.getInstance().visualizerIndex);
		}
	}

	@Override
	public void onFailure() {
		UI.toast(R.string.visualizer_not_supported);
	}

	@Override
	public void onFinalCleanup() {
		if (visualizer != null) {
			if (!visualizerPaused) {
				visualizerPaused = true;
				visualizer.onActivityPause();
			}
			visualizer.releaseView();
			visualizer = null;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case MSG_HIDE:
				if (msg.arg1 != version || !isWindowFocused)
					break;
				showPanelTop(false);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					hideSystemUI();
				break;
			case MSG_SYSTEM_UI_CHANGED:
				final boolean show = (msg.arg1 == 0);
				showPanelTop(show);
				if (show)
					hideAllUIDelayed();
				break;
		}
		return true;
	}

	@Override
	public void handleTimer(Timer timer, Object param) {
		if (panelTop == null || uiAnimTimer == null || info == null)
			return;
		final int now = (int)SystemClock.uptimeMillis();
		final float delta = (float)(now - panelTopLastTime) * 0.001953125f;
		panelTopLastTime = now;
		if (panelTopHiding < 0) {
			panelTopAlpha -= delta;
			if (panelTopAlpha <= 0.0f) {
				panelTopHiding = 0;
				panelTopAlpha = 0.0f;
				uiAnimTimer.stop();
				panelTop.setVisibility(View.GONE);
			}
		} else {
			panelTopAlpha += delta;
			if (panelTopAlpha >= 1.0f) {
				panelTopHiding = 0;
				panelTopAlpha = 1.0f;
				uiAnimTimer.stop();
			}
		}

		if (panelTopAlpha != 0.0f) {
			final int c = (int)(255.0f * panelTopAlpha);
			if (panelTopBackground != null)
				panelTopBackground.setAlpha(c >> 1);
			buttonColor.setNormalColorAlpha(c);
			lblColor.setNormalColorAlpha(c);
			if (btnGoBack != null)
				btnGoBack.setTextColor(buttonColor);
			if (btnMenu != null)
				btnMenu.setTextColor(buttonColor);
		}
	}

	private void finalCleanup() {
//		Player.removeDestroyedObserver(this);
		if (visualizerService != null) {
			visualizerService.destroy();
			visualizerService = null;
		} else if (visualizer != null) {
			visualizer.cancelLoading();
			visualizer.release();
			onFinalCleanup();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			cleanupSystemUIObserver();
		if (uiAnimTimer != null)
			uiAnimTimer.stop();
		info = null;
		panelControls = null;
		panelTop = null;
		btnGoBack = null;
		btnPlay = null;
		btnNext = null;
		btnMenu = null;
		uiAnimTimer = null;
		buttonColor = null;
		lblColor = null;
		panelTopBackground = null;
		//songInfo = null;
	}

	private void showPanelTop(boolean show) {
		if (panelTop == null || uiAnimTimer == null)
			return;
		if (show) {
			if (panelTop.getVisibility() != View.VISIBLE){
				panelTop.setVisibility(View.VISIBLE);
			}
			if(nextVisualizerIb.getVisibility()!=View.VISIBLE
					&&prevVisualizerIb.getVisibility()!=View.VISIBLE){
				nextVisualizerIb.setVisibility(View.VISIBLE);
				prevVisualizerIb.setVisibility(View.VISIBLE);
				btnNext.setVisibility(View.VISIBLE);
				btnPlay.setVisibility(View.VISIBLE);
				nextVisualizerIb.bringToFront();
				prevVisualizerIb.bringToFront();
			}

			panelTopHiding = 1;
			if (!uiAnimTimer.isAlive()) {
				panelTopLastTime = (int)SystemClock.uptimeMillis();
				uiAnimTimer.start(16);
			}
		} else {
			nextVisualizerIb.setVisibility(View.GONE);
			prevVisualizerIb.setVisibility(View.GONE);
			btnNext.setVisibility(View.GONE);
			btnPlay.setVisibility(View.GONE);
			if (panelTop.getVisibility() == View.GONE)
				return;
			panelTopHiding = -1;
			if (!uiAnimTimer.isAlive()) {
				panelTopLastTime = (int)SystemClock.uptimeMillis();
				uiAnimTimer.start(16);
			}
		}
	}

	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private static final class SystemUIObserver implements View.OnSystemUiVisibilityChangeListener {
		private View decor;
		private MainHandler.Callback callback;

		public SystemUIObserver(View decor, MainHandler.Callback callback) {
			this.decor = decor;
			this.callback = callback;
		}

		@Override
		public void onSystemUiVisibilityChange(int visibility) {
			if (decor == null)
				return;
			if (callback != null)
				MainHandler.sendMessage(callback, MSG_SYSTEM_UI_CHANGED, visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION, 0);
		}

		public void hide() {
			if (decor == null)
				return;
			try {
				decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_FULLSCREEN |
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_LOW_PROFILE |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_IMMERSIVE);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		public void show() {
			if (decor == null)
				return;
			try {
				decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_FULLSCREEN |
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						//View.SYSTEM_UI_FLAG_LOW_PROFILE |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						//View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_IMMERSIVE);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		public void prepare() {
			show();
			if (decor == null)
				return;
			try {
				decor.setOnSystemUiVisibilityChangeListener(this);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		public void cleanup() {
			if (decor != null) {
				try {
					decor.setOnSystemUiVisibilityChangeListener(null);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				decor = null;
			}
			callback = null;
		}
	}



	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void prepareSystemUIObserver() {
		if (!visualizerRequiresHiddenControls)
			return;
		if (systemUIObserver == null)
			systemUIObserver = new SystemUIObserver(getWindow().getDecorView(), this);
		((SystemUIObserver)systemUIObserver).prepare();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void hideSystemUI() {
		if (visualizerRequiresHiddenControls && systemUIObserver != null)
			((SystemUIObserver)systemUIObserver).hide();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void showSystemUI() {
		if (visualizerRequiresHiddenControls && systemUIObserver != null)
			((SystemUIObserver)systemUIObserver).show();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void cleanupSystemUIObserver() {
		if (visualizerRequiresHiddenControls && systemUIObserver != null) {
			((SystemUIObserver)systemUIObserver).cleanup();
			systemUIObserver = null;
		}
	}

	private void updateTitle() {

//		if (Player.localSong == null) {
//			lblTitle.setText(getText(R.string.nothing_playing));
//		} else {
//			String txt = Player.getCurrentTitle(Player.isPreparing());
//			if (Player.localSong.extraInfo != null && Player.localSong.extraInfo.length() > 0 && (Player.localSong.extraInfo.length() > 1 || Player.localSong.extraInfo.charAt(0) != '-'))
//				txt += "\n" + Player.localSong.extraInfo;
//			lblTitle.setText(txt);
//		}
	}

	/**
	 * 设置频谱视图规则
	 * @param updateInfo
	 */
	private void prepareViews(boolean updateInfo) {
		if (info == null)
			return;

		if (updateInfo)
			updateInfoWithConfiguration(null);

		panelTopHiding = 0;
		panelTopAlpha = 1.0f;

		btnMenu.setIcon(UI.ICON_MENU_MORE);

		if (visualizer != null) {
			final InterceptableLayout.LayoutParams ip;
			if (visualizerViewFullscreen) {
				ip = new InterceptableLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			} else {
				if (visualizerRequiresHiddenControls) {
					final Point pt = (Point)visualizer.getDesiredSize(info.usableScreenWidth, info.usableScreenHeight);
					ip = new InterceptableLayout.LayoutParams(pt.x, pt.y);
					ip.addRule(InterceptableLayout.CENTER_IN_PARENT, InterceptableLayout.TRUE);
				} else {
					ip = new InterceptableLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
					ip.addRule(InterceptableLayout.BELOW, R.id.av_panelLayout);
					ip.addRule(InterceptableLayout.ALIGN_PARENT_BOTTOM, InterceptableLayout.TRUE);
				}
			}
			((View)visualizer).setLayoutParams(ip);
		}

		panelControls.requestLayout();
	}

	private void updateInfoWithConfiguration(Configuration configuration) {
		if (configuration == null)
			configuration = getResources().getConfiguration();
		if (configuration != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			info.getInfo(this, UI.dpToPxI(configuration.screenWidthDp), UI.dpToPxI(configuration.screenHeightDp));
		else
			info.getInfo(this, 0, 0);
	}

	/**
	 * 初始化所有
	 */
	private void init(){
		initApplication();
		initUI();
		initHandler();
		initLanguage();

		initData();
		initVisualizer();
		initStatus();
		initOther();
	}

	/**
	 * 初始化Application
	 */
	private void initApplication(){
		ApplicationProxy.getInstance().init(this.getApplication());
	}

	/**
	 * 初始化UI
	 */
	private void initUI(){
		UI.initialize(ActivityVisualizer.this, 1080,1920);
		UI.loadCommonColors(true);
		UI.initColorDefault();//默认的相关颜色值
	}

	/**
	 * 初始化Handler
	 */
	private void initHandler(){
		MainHandler.initialize();
	}

	/**
	 * 初始化语言
	 */
	private void initLanguage(){
		//语言相关
		if (UI.forcedLocale != UI.LOCALE_NONE){
			UI.reapplyForcedLocale(this);
		}
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		isWindowFocused = true;
		buttonColor = new BgColorStateList(UI.colorState_text_visualizer_reactive.getDefaultColor(), UI.color_text_selected);
		lblColor = new BgColorStateList(UI.colorState_text_visualizer_reactive.getDefaultColor(), UI.colorState_text_visualizer_reactive.getDefaultColor());


		info = new UI.DisplayInfo();
	}

	/**
	 * 使用反射 初始化Visualizer
	 */
	private void initVisualizer(){
		String name = null;
		final Intent si = getIntent();
		if (si != null && (name = si.getStringExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME)) != null) {
			if (!name.startsWith("br.com.carlosrafaelgn.fplay"))
				name = null;
		}
		if (name != null) {
			try {
				final Class<?> clazz = Class.forName(name);
				if (clazz != null) {
					try {
						updateInfoWithConfiguration(null);
						visualizer = (IVisualizer)clazz.getConstructor(Activity.class, boolean.class, Intent.class).newInstance(this, info.isLandscape, si);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		final boolean visualizerRequiresThread;
		if (visualizer != null) {
			requiredOrientation = visualizer.requiredOrientation();
			visualizerRequiresHiddenControls = visualizer.requiresHiddenControls();
			visualizerRequiresThread = (visualizer.requiredDataType() != IVisualizer.DATA_NONE);
		} else {
			requiredOrientation = IVisualizer.ORIENTATION_NONE;
			visualizerRequiresHiddenControls = false;
			visualizerRequiresThread = false;
		}

		visualizerService = null;
		if (visualizer != null) {
			visualizerPaused = false;
			visualizer.onActivityResume();
			if (!visualizerRequiresThread){
				visualizer.load();
			}else{
				visualizerService = new VisualizerService(visualizer, this);
			}
		}
	}

	/**
	 * 初始化状态栏相关
	 */
	private void initStatus(){
		setRequestedOrientation((requiredOrientation == IVisualizer.ORIENTATION_NONE) ?
				(UI.visualizerPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
						: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				: (requiredOrientation == IVisualizer.ORIENTATION_PORTRAIT
				? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

		getWindow().setBackgroundDrawable(new ColorDrawable(UI.color_visualizer565));

		if (visualizerRequiresHiddenControls) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			if (UI.allowPlayerAboveLockScreen){
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			}
			else{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

			}
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}
	}

	/**
	 * 初始化其它
	 */
	private void initOther(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			prepareSystemUIObserver();
		}
	}

	/**
	 * 初始化视图并且设置监听
	 */
	private void initView(){
		panelControls = findViewById(R.id.av_panelControls);
		panelTop = findViewById(R.id.av_panelLayout);
		btnGoBack = findViewById(R.id.av_backBtn);
		btnGoBack.setIcon(UI.ICON_GOBACK);
		btnPlay = findViewById(R.id.av_visualizer_play);
		btnNext = findViewById(R.id.av_visualizer_next);
		btnMenu = findViewById(R.id.av_moreBtn);
		titleTv = findViewById(R.id.av_title_tv);
		artistTv = findViewById(R.id.av_artist_tv);
		nextVisualizerIb=findViewById(R.id.av_rightBtn);
		prevVisualizerIb=findViewById(R.id.av_leftBtn);

		panelControls.setOnClickListener(this);
		panelControls.setInterceptedTouchEventListener(this);
		btnGoBack.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		nextVisualizerIb.setOnClickListener(this);
		prevVisualizerIb.setOnClickListener(this);

		updateTitle();
	}

	/**
	 * 添加频谱视图
	 */
	private void addVisualizerView(){
		if (visualizer != null) {
			ApplicationProxy.getInstance().visualizerIndex=0;//默认是水波纹
			visualizerViewFullscreen = visualizer.isFullscreen();
			((View)visualizer).setOnClickListener(this);
			panelControls.addView((View)visualizer);
			panelTop.bringToFront();

			nextVisualizerIb.bringToFront();
			prevVisualizerIb.bringToFront();
		}

		prepareViews(true);
	}

	/**
	 * 设置视图颜色相关
	 */
	private void setViewColor(){
		if (visualizerRequiresHiddenControls) {
			panelTopBackground = new ColorDrawable(UI.color_visualizer565);
			panelTopBackground.setAlpha(255 >> 1);
			panelTop.setBackgroundDrawable(panelTopBackground);
		}

		btnGoBack.setTextColor(buttonColor);
		btnMenu.setTextColor(buttonColor);

		if (!btnGoBack.isInTouchMode()){
			btnGoBack.requestFocus();
		}
	}

	/**
	 * 开启一个定时器
	 */
	private void startTimer(){
		uiAnimTimer = (visualizerRequiresHiddenControls ? new Timer(this,
				"UI Anim Timer", false, true, false) : null);
	}

	/**
	 * 延迟隐藏UI
	 */
	private void hideAllUIDelayed() {
		if (!visualizerRequiresHiddenControls)
			return;
		version++;
		MainHandler.removeMessages(this, MSG_HIDE);
		MainHandler.sendMessageAtTime(this, MSG_HIDE, version, 0, SystemClock.uptimeMillis() + 4000);
	}

	/**
	 * 改变频谱类型
	 * @param type
	 */
	private void changeVisualizer(int type){
		LogUtils.d("当前选择的频谱类型为："+type+" 名称为："+ApplicationProxy.getInstance().visualizerDataType[type]);
		if(!isFinishChange) return;
		isFinishChange=false;

		try{
			if (visualizer != null) {
				panelControls.removeView((View) visualizer);
				visualizer.release();
				visualizer = null;
			}
			if(visualizerService!=null){
				visualizerService.pause();
				visualizerService=null;
			}
		}catch (Throwable e){
		   LogUtils.d("release异常##"+e.getMessage());
		}


		Intent intent=setIntent(type);
		visualizer = new OpenGLVisualizerJni(this, true, intent);

		final boolean visualizerRequiresThread;
		if (visualizer != null) {
			requiredOrientation = visualizer.requiredOrientation();
			visualizerRequiresHiddenControls = visualizer.requiresHiddenControls();
			visualizerRequiresThread = (visualizer.requiredDataType() != IVisualizer.DATA_NONE);
		} else {
			requiredOrientation = IVisualizer.ORIENTATION_NONE;
			visualizerRequiresHiddenControls = false;
			visualizerRequiresThread = false;
		}

		visualizerService = null;
		if (visualizer != null) {
			visualizerPaused = false;
			visualizer.onActivityResume();
			if (!visualizerRequiresThread){
				visualizer.load();
			}else{
				visualizerService = new VisualizerService(visualizer, this);
			}
		}

		if (visualizer != null) {
			visualizerViewFullscreen = visualizer.isFullscreen();
			((View)visualizer).setOnClickListener(this);
			panelControls.addView((View)visualizer);
			panelTop.bringToFront();

			nextVisualizerIb.bringToFront();
			prevVisualizerIb.bringToFront();
		}

		prepareViews(true);

		isFinishChange=true;
		LogUtils.d("当前选择的频谱类型 切换成功结束！！！");
	}

	private Intent setIntent(int type){
		Intent intent=new Intent();
		switch (ApplicationProxy.getInstance().visualizerDataType[type]){
			case ApplicationProxy.LIQUID_TYPE:
				intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
				intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID);
				break;

			case ApplicationProxy.SPECTRUM2_TYPE:
				intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
				intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPECTRUM2);
				break;

			case ApplicationProxy.COLOR_WAVES_TYPE:
				intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
				intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_COLOR_WAVES);
				break;

			case ApplicationProxy.PARTICLE_TYPE:
				intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
				intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_PARTICLE);
				break;

			case ApplicationProxy.NORMAL_TYPE:
				intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
				break;

			case ApplicationProxy.SPIN_TYPE:
				intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
				intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPIN);
				break;
		}

		return intent;
	}

}
