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
package com.coocent.visualizerlib.core;

import android.os.Build;

import com.coocent.visualizerlib.common.Timer;
import com.coocent.visualizerlib.inter.IVisualizer;
import com.coocent.visualizerlib.inter.IVisualizerService;
import com.coocent.visualizerlib.utils.LogUtils;


public final class VisualizerService implements IVisualizerService, Runnable, Timer.TimerHandler {
	private IVisualizer visualizer;
	private Observer observer;
	private android.media.audiofx.Visualizer fxVisualizer;
	private boolean hasEverBeenAlive;
	private volatile boolean alive, paused, reset, playing, failed, visualizerReady;
	private int audioSessionId;
	private byte[] waveform;
	private Timer timer;

	public VisualizerService(IVisualizer visualizer, Observer observer) {
		this.visualizer = visualizer;
		this.observer = observer;
		audioSessionId = -1;
		alive = true;
		reset = true;
//		playing = Player.localPlaying;
		playing = true;
		waveform = new byte[IVisualizer.CAPTURE_SIZE];
		timer = new Timer(this, "Visualizer Thread", false, false, true);
		timer.start(50);//16
	}

	@Override
	public void playingChanged() {
		playing = true;
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		if (timer != null) {
			paused = false;
			timer.resume();
		}
	}

	@Override
	public void resetAndResume() {
		if (timer != null) {
			reset = true;
			paused = false;
			timer.resume();
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			alive = false;
			if (visualizer != null)
				visualizer.cancelLoading();
			paused = false;
			timer.resume();
			timer = null;
		}
	}

	private boolean initialize() {
		try {

			//TODO 这里需要Session，在自己项目中设置SessionId，默认为0
			int g=VisualizerManager.getInstance().getSessionId();

			//LogUtils.d("当前歌曲的sessionId="+g);

			if (g < 0)
				return true;
			if (fxVisualizer != null) {
				if (audioSessionId == g) {
					try {
						fxVisualizer.setEnabled(true);
						return true;
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
				try {
					fxVisualizer.release();
				} catch (Throwable ex) {
					fxVisualizer = null;
					ex.printStackTrace();
				}
			}
			fxVisualizer = new android.media.audiofx.Visualizer(g);
			audioSessionId = g;
		} catch (Throwable ex) {
			failed = true;
			fxVisualizer = null;
			audioSessionId = -1;
			return false;
		}
		try {
			fxVisualizer.setEnabled(false);
			fxVisualizer.setCaptureSize(IVisualizer.CAPTURE_SIZE);
			fxVisualizer.setEnabled(true);
		} catch (Throwable ex) {

			failed = true;
			fxVisualizer.release();
			fxVisualizer = null;
			audioSessionId = -1;
		}
		if (fxVisualizer != null && visualizer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			try {
				fxVisualizer.setScalingMode(android.media.audiofx.Visualizer.SCALING_MODE_AS_PLAYED);
				fxVisualizer.setScalingMode(android.media.audiofx.Visualizer.SCALING_MODE_NORMALIZED);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		//LogUtils.e("nsc"," handleTimer run======");
		if (failed) {
			failed = false;
			if (observer != null)
				observer.onFailure();
		}
		if (observer != null) {
			observer.onFinalCleanup();
			observer = null;
		}
		waveform = null;
		timer = null;
		visualizer = null;
	}

	@Override
	public void handleTimer(Timer timer, Object param) {
		//LogUtils.e("nsc"," handleTimer paused======"+paused + " reset="+reset + " alive ="+alive);
		if (alive) {
			if (paused) {
				try {
					if (fxVisualizer != null)
						fxVisualizer.setEnabled(false);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				timer.pause();
				return;
			}
			if (reset || fxVisualizer == null) {
				reset = false;
				if (!initialize()) {
					if (hasEverBeenAlive) {
						//the player may be undergoing an unstable condition, such as successive
						//fast track changes... try again later
						failed = false;
						paused = true;
						timer.pause();
					} else {
						alive = false;
					}
				} else if (!visualizerReady && alive && visualizer != null) {
					hasEverBeenAlive = true;
					visualizer.load();
					visualizerReady = true;
				}
			}
			if (visualizer != null) {
				//WE MUST NEVER call any method from visualizer
				//while the player is not actually playing
				if (playing) {
					fxVisualizer.getWaveForm(waveform);
					//LogUtils.e("nsc"," handleTimer paused======"+paused + " reset="+reset + " alive ="+alive);
				}
				//LogUtils.e("nsc"," handleTimer paused2======"+paused + " reset="+reset + " alive ="+alive);
				visualizer.processFrame(playing, waveform);
			}
		}
		if (!alive) {
			timer.release();
			if (visualizer != null)
				visualizer.release();
			if (fxVisualizer != null) {
				try {
					fxVisualizer.setEnabled(false);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				try {
					fxVisualizer.release();
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				fxVisualizer = null;
			}
			MainHandler.postToMainThread(this);
			System.gc();
		}
	}
}
