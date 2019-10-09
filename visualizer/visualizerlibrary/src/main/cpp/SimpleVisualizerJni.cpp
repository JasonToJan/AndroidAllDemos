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
#include <jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>
#include <android/log.h>
#include <string.h>
#include <math.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include "LogUtils.h"
#include <android/log.h>

//http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/functions.html
//http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/design.html

#define MAX(A,B) (((A) > (B)) ? (A) : (B))

#include "Common.h"
#include "OpenGLVisualizerJni.h"

static float invBarW;
static int32_t barW, barH, barBins, barWidthInPixels, recreateVoice, lerp;
static uint16_t bgColor;
static uint16_t* voice, *alignedVoice;

/**
 * 设置一个线性插值，要么为1，要么为0
 * @param env
 * @param clazz
 * @param jlerp
 */
void JNICALL setLerp(JNIEnv* env, jclass clazz, jboolean jlerp) {
	lerp = (jlerp ? 1 : 0);
}

/**
 * 初始化背景颜色 得到RGB 3个颜色值
 * @param env
 * @param clazz
 * @param jbgColor
 */
void JNICALL init(JNIEnv* env, jclass clazz, int32_t jbgColor) {
	voice = 0;
	recreateVoice = 0;
	commonColorIndex = 0;
	commonColorIndexApplied = 0;
	const uint32_t r = ((jbgColor >> 16) & 0xff) >> 3;
	const uint32_t g = ((jbgColor >> 8) & 0xff) >> 2;
	const uint32_t b = (jbgColor & 0xff) >> 3;
	bgColor = (uint16_t)((r << 11) | (g << 5) | b);
	commonUpdateMultiplier(env, clazz, 0, 0);
}

/**
 * 是否终止 删除静态声音指针
 * @param env
 * @param clazz
 */
void JNICALL terminate(JNIEnv* env, jclass clazz) {
	if (voice) {
		delete voice;
		voice = 0;
	}
}

/**
 * 准备开始渲染
 * 在Android中 GLSurfaceView中的surfaceChanged方法中执行了
 * 关键逻辑： 只是为了调用一个 ANativeWindow_setBuffersGeometry
 *
 * @param env
 * @param clazz
 * @param surface
 * @return
 */
int32_t JNICALL prepareSurface(JNIEnv* env, jclass clazz, jobject surface) {
	ANativeWindow* wnd = ANativeWindow_fromSurface(env, surface);//通过Android中Surface获取一个Window窗口指针
	if (!wnd)
		return -1;
	int32_t ret = -2;
	int32_t w = ANativeWindow_getWidth(wnd),h = ANativeWindow_getHeight(wnd);
	if (w > 0 && h > 0) {//渲染的宽度和高度都大于0
		barW = w >> 8; //宽度转换为2进制后，右移8位 可能会减小
		barH = h & (~1); //make the height always an even number 1取反 再和高度与运算
		if (barW < 1)
			barW = 1;
		const int32_t size = barW << 8;  //这里可能会增大 恢复为窗口宽度
		invBarW = 1.0f / (float) barW;
		barBins = ((size > w) ? ((w < 256) ? (w & ~7) : 256) : 256);
		barWidthInPixels = barBins * barW;
		recreateVoice = 1;
		//默认格式为565
		ret = ANativeWindow_setBuffersGeometry(wnd, barWidthInPixels, barH, WINDOW_FORMAT_RGB_565);
	}
	if (ret < 0) {
		invBarW = 1;
		barBins = 0;
		barWidthInPixels = 0;
		recreateVoice = 0;
	}
	ANativeWindow_release(wnd);//释放原生窗口
	return ret;
}

/**
 * 没有声音的时候进行绘制
 * 根据 int数组 （频谱传出来的） 然后进行绘制过程
 *
 * 这里具体作用不详，因为注释掉整个函数，好像没什么影响
 * @param env
 * @param clazz
 * @param jwaveform
 * @param surface
 * @param opt
 */
void JNICALL process(JNIEnv* env, jclass clazz, jbyteArray jwaveform, jobject surface, int32_t opt) {
    LOGD("TEST##first go to process ");
	ANativeWindow* wnd = ANativeWindow_fromSurface(env, surface);
	if (!wnd)
		return;
	ANativeWindow_Buffer inf;
	if (ANativeWindow_lock(wnd, &inf, 0) < 0) {
		ANativeWindow_release(wnd);
		return;
	}
	if (inf.width != barWidthInPixels ||
		inf.height != barH) {
		ANativeWindow_unlockAndPost(wnd);
		ANativeWindow_release(wnd);
		return;
	}
	inf.stride <<= 1; //convert from pixels to uint16_t

	//fft format:
	//index  0   1    2  3  4  5  ..... n-2        n-1
	//       Rdc Rnyq R1 I1 R2 I2       R(n-1)/2  I(n-1)/2
	uint8_t* fftI;
	if (!(opt & IGNORE_INPUT)) {
		uint8_t* const waveform = (uint8_t*)env->GetPrimitiveArrayCritical(jwaveform, 0);
		if (!waveform) {
			ANativeWindow_unlockAndPost(wnd);
			ANativeWindow_release(wnd);
			return;
		}

		fftI = _fftI;
		doFft(waveform, fftI, DATA_FFT);
		//*** we are not drawing/analyzing the last bin (Nyquist) ;) ***
		fftI[1] = 0;

		env->ReleasePrimitiveArrayCritical(jwaveform, waveform, JNI_ABORT);
	} else {
		fftI = 0;
	}

	float* const fft = _fft;
	const float* const multiplier = _multiplier;
	float* const previousM = _previousM;

	float coefNew = COEF_SPEED_DEF * (float)commonUptimeDeltaMillis(&commonLastTime);
	if (coefNew > 1.0f)
		coefNew = 1.0f;
	const float coefOld = 1.0f - coefNew;

	float previous = 0;
	for (int32_t i = 0; i < barBins; i++) {
		float m;
		if (fftI) {
			//fftI[i] stores values from 0 to 255 (inclusive)
			const int32_t re = (uint32_t)fftI[i << 1];
			const int32_t im = (uint32_t)fftI[(i << 1) + 1];
			const int32_t amplSq = (re * re) + (im * im);
			m = ((amplSq < 8) ? 0.0f : (multiplier[i] * sqrtf((float)(amplSq))));
			previousM[i] = m;
		} else {
			m = previousM[i];
		}
		const float old = fft[i];
		if (m < old)
			m = (coefNew * m) + (coefOld * old);
		fft[i] = m;

		if (barW == 1 || !lerp) {
			//m goes from 0 to 32768+ (inclusive)
			int32_t v = (int32_t)m;
			if (v > 32768)
				v = 32768;

			const uint16_t color = COLORS[commonColorIndex + (v >> 7)];
			v = ((v * barH) >> 15);
			int32_t v2 = v;
			v = (barH - v) >> 1;
			v2 += v;
			uint16_t* currentBar = (uint16_t*)inf.bits;
			inf.bits = (void*)((uint16_t*)inf.bits + barW);

			int32_t y = 0;
			switch (barW) {
			case 1:
				for (; y < v; y++) {
					*currentBar = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < v2; y++) {
					*currentBar = color;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < barH; y++) {
					*currentBar = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				break;
			case 2:
				for (; y < v; y++) {
					*currentBar = bgColor;
					currentBar[1] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < v2; y++) {
					*currentBar = color;
					currentBar[1] = color;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < barH; y++) {
					*currentBar = bgColor;
					currentBar[1] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				break;
			case 3:
				for (; y < v; y++) {
					*currentBar = bgColor;
					currentBar[1] = bgColor;
					currentBar[2] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < v2; y++) {
					*currentBar = color;
					currentBar[1] = color;
					currentBar[2] = color;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < barH; y++) {
					*currentBar = bgColor;
					currentBar[1] = bgColor;
					currentBar[2] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				break;
			case 4:
				for (; y < v; y++) {
					*currentBar = bgColor;
					currentBar[1] = bgColor;
					currentBar[2] = bgColor;
					currentBar[3] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < v2; y++) {
					*currentBar = color;
					currentBar[1] = color;
					currentBar[2] = color;
					currentBar[3] = color;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < barH; y++) {
					*currentBar = bgColor;
					currentBar[1] = bgColor;
					currentBar[2] = bgColor;
					currentBar[3] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				break;
			default:
				for (; y < v; y++) {
					for (int32_t b = barW - 1; b >= 0; b--)
						currentBar[b] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < v2; y++) {
					for (int32_t b = barW - 1; b >= 0; b--)
						currentBar[b] = color;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < barH; y++) {
					for (int32_t b = barW - 1; b >= 0; b--)
						currentBar[b] = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				break;
			}
		} else {
			//m goes from 0 to 32768+ (inclusive)
			const float delta = (int32_t)(m - previous) * invBarW;
			for (int32_t i = 0; i < barW; i++) {
				previous += delta;

				int32_t v = (int32_t)previous;
				if (v < 0)
					v = 0;
				else if (v > 32768)
					v = 32768;

				const uint16_t color = COLORS[commonColorIndex + (v >> 7)];
				v = ((v * barH) >> 15);
				int32_t v2 = v;
				v = (barH - v) >> 1;
				v2 += v;
				uint16_t* currentBar = (uint16_t*)inf.bits;
				inf.bits = (void*)((uint16_t*)inf.bits + 1);

				int32_t y = 0;
				for (; y < v; y++) {
					*currentBar = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < v2; y++) {
					*currentBar = color;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
				for (; y < barH; y++) {
					*currentBar = bgColor;
					currentBar = (uint16_t*)((uint8_t*)currentBar + inf.stride);
				}
			}
		}
	}

	ANativeWindow_unlockAndPost(wnd);
	ANativeWindow_release(wnd);

    LOGE("%s", "TEST##finish process ");
}

/**
 * 有声音的时候进行绘制
 * 根据频谱传出来的byte[] 数据
 *
 * 这里作用不详，因为注释掉整个函数，对绘制没有任何影响
 * @param env
 * @param clazz
 * @param jwaveform
 * @param surface
 * @param opt
 */
void JNICALL processVoice(JNIEnv* env, jclass clazz, jbyteArray jwaveform, jobject surface, int32_t opt) {
	ANativeWindow* wnd = ANativeWindow_fromSurface(env, surface);
	if (!wnd)
		return;
	ANativeWindow_Buffer inf;
	if (ANativeWindow_lock(wnd, &inf, 0) < 0) {
		ANativeWindow_release(wnd);
		return;
	}
	if (inf.width != barWidthInPixels ||
		inf.height != barH) {
		ANativeWindow_unlockAndPost(wnd);
		ANativeWindow_release(wnd);
		return;
	}
	if (recreateVoice) {
		if (voice)
			delete voice;
		voice = (uint16_t*)(new uint8_t[((inf.stride * inf.height) << 1) + 16]);
		uint8_t* al = (uint8_t*)voice;
		while ((((size_t)al) & 15))
			al++;
		alignedVoice = (uint16_t*)al;
		recreateVoice = 0;
	}
	if (!voice) {
		ANativeWindow_unlockAndPost(wnd);
		ANativeWindow_release(wnd);
		return;
	}
	inf.stride <<= 1; //convert from pixels to uint16_t

	int32_t v = inf.stride * (inf.height - 1);
	memcpy(alignedVoice, (uint8_t*)alignedVoice + inf.stride, v);
	uint16_t* currentBar = (uint16_t*)((uint8_t*)alignedVoice + v);

//	float* const fft = _fft;
	const float* const multiplier = _multiplier;
	float* const previousM = _previousM;
	//fft format:
	//index  0   1    2  3  4  5  ..... n-2        n-1
	//       Rdc Rnyq R1 I1 R2 I2       R(n-1)/2  I(n-1)/2
	uint8_t* fftI;
	if (!(opt & IGNORE_INPUT)) {
		uint8_t* const waveform = (uint8_t*)env->GetPrimitiveArrayCritical(jwaveform, 0);
		if (!waveform) {
			ANativeWindow_unlockAndPost(wnd);
			ANativeWindow_release(wnd);
			return;
		}

		fftI = _fftI;
		doFft(waveform, fftI, DATA_FFT);
		//*** we are not drawing/analyzing the last bin (Nyquist) ;) ***
		fftI[1] = 0;

		env->ReleasePrimitiveArrayCritical(jwaveform, waveform, JNI_ABORT);
	} else {
		fftI = 0;
	}

	float previous = 0;

	for (int32_t i = 0; i < barBins; i++) {
		//fftI[i] stores values from 0 to 255 (inclusive)
		float m;
		if (fftI) {
			const int32_t re = (uint32_t)fftI[i << 1];
			const int32_t im = (uint32_t)fftI[(i << 1) + 1];
			const int32_t amplSq = (re * re) + (im * im);
			m = ((amplSq < 8) ? 0.0f : (multiplier[i] * sqrtf((float)(amplSq))));
			previousM[i] = m;
		} else {
			m = previousM[i];
		}
		if (barW == 1) {
			//m goes from 0 to 256+ (inclusive)
			const int32_t v = (int32_t)m;
			*currentBar = COLORS[commonColorIndex + ((v >= 256) ? 256 : v)];
			currentBar++;
		} else {
			const float delta = (m - previous) * invBarW;
			for (int32_t i = 0; i < barW; i++) {
				previous += delta;
				const int32_t v = (int32_t)previous;
				*currentBar = COLORS[commonColorIndex + ((v >= 256) ? 256 : v)];
				currentBar++;
			}
		}
	}

	memcpy(inf.bits, alignedVoice, inf.stride * inf.height);
	ANativeWindow_unlockAndPost(wnd);
	ANativeWindow_release(wnd);
}

/**
 * 检查“霓虹灯”模式
 */
#ifdef FPLAY_ARM //如果定义了FPLAY_ARM 再执行这个函数
void checkNeonMode() {
    #ifdef FPLAY_64_BITS //如果定义了FPLAY_64_BITS 再执行
        //can we safely assume this???
        neonMode = 1;
    #else
        //based on
        //http://code.google.com/p/webrtc/source/browse/trunk/src/system_wrappers/source/android/cpu-features.c?r=2195
        //http://code.google.com/p/webrtc/source/browse/trunk/src/system_wrappers/source/android/cpu-features.h?r=2195
        neonMode = 0;
        char cpuinfo[4096];
        int32_t cpuinfo_len = -1;
        int32_t fd = open("/proc/cpuinfo", O_RDONLY);
        if (fd >= 0) {
            do {
                cpuinfo_len = read(fd, cpuinfo, 4096);
            } while (cpuinfo_len < 0 && errno == EINTR);
            close(fd);
            if (cpuinfo_len > 0) {
                cpuinfo[cpuinfo_len] = 0;
                //look for the "\nFeatures: " line
                for (int32_t i = cpuinfo_len - 9; i >= 0; i--) {
                    if (memcmp(cpuinfo + i, "\nFeatures", 9) == 0) {
                        i += 9;
                        while (i < cpuinfo_len && (cpuinfo[i] == ' ' || cpuinfo[i] == '\t' || cpuinfo[i] == ':'))
                            i++;
                        cpuinfo_len -= 5;
                        //now look for the " neon" feature
                        while (i <= cpuinfo_len && cpuinfo[i] != '\n') {
                            if (memcmp(cpuinfo + i, " neon", 5) == 0 ||
                                memcmp(cpuinfo + i, "\tneon", 5) == 0) {
                                neonMode = 1;
                                break;
                            }
                            i++;
                        }
                        break;
                    }
                }
            }
        }
    #endif
}
#endif

/**
 * JNI
 * 最开始会执行的地方
 */
extern "C" {

    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
        #ifdef FPLAY_ARM
        checkNeonMode();
        #endif

        LOGD("go to JNI_OnLoad");

        voice = 0;
        recreateVoice = 0;
        glResetState(); //初始化状态 下面基本上都是一些常量定义
        commonTime = 0;
        commonTimeLimit = 0xffffffff;
        commonColorIndex = 0;
        commonColorIndexApplied = 0;
        commonCoefNew = 0.0f;
        rootMeanSquare = 0.0f;
        vuMeter = 0.0f;
        vuMeterUnfiltered = 0.f;
        vuMeterFilterState[0] = 0.0f;
        vuMeterFilterState[1] = 0.0f;
        vuMeterFilterState[2] = 0.0f;
        vuMeterFilterState[3] = 0.0f;
        beatCounter = 0;
        beatState = BEAT_STATE_VALLEY;
        beatPeakOrValley = 0;
        beatThreshold = BEAT_MIN_RISE_THRESHOLD;
        beatDeltaMillis = 0;
        beatSilenceDeltaMillis = 0;
        beatSpeedBPM = 0;
        beatFilteredInput = 0;

        //供外部调用的方法集合
        JNINativeMethod methodTable[] = {
                {"commonSetSpeed",         "(I)V",                         (void *) commonSetSpeed}, //设置速度
                {"commonSetColorIndex",    "(I)V",                         (void *) commonSetColorIndex}, //设置颜色索引
                {"commonUpdateMultiplier", "(ZZ)V",                        (void *) commonUpdateMultiplier}, //更新多路复用器
                {"commonProcess",          "([BI)I",                       (void *) commonProcess}, //共同过程

                {"setLerp",                "(Z)V",                         (void *) setLerp},       //设置线性插值
                {"init",                   "(I)V",                         (void *) init},          //初始化 设置背景颜色
                {"terminate",              "()V",                          (void *) terminate},     //终结进程
                {"prepareSurface",         "(Landroid/view/Surface;)I",    (void *) prepareSurface},//准备渲染
                {"process",                "([BLandroid/view/Surface;I)V", (void *) process},       //过程
                {"processVoice",           "([BLandroid/view/Surface;I)V", (void *) processVoice},  //声音

                {"glGetOESTexture",        "()I",                          (void *) glGetOESTexture}, //文本相关
                {"glOnSurfaceCreated",     "(IIIIII)I",                    (void *) glOnSurfaceCreated}, //onSurfaceCreated
                {"glOnSurfaceChanged",     "(IIIIII)V",                    (void *) glOnSurfaceChanged}, //onSurfaceChanged
                {"glLoadBitmapFromJava",   "(Landroid/graphics/Bitmap;)I", (void *) glLoadBitmapFromJava}, //从Java中加载图片资源
                {"glDrawFrame",            "()V",                          (void *) glDrawFrame},   //最为关键的绘制过程
                {"glOnSensorReset",        "()V",                          (void *) glOnSensorReset},  //重力感应重置
                {"glOnSensorData",         "(JI[F)V",                      (void *) glOnSensorData},   //重力数据
                {"glSetImmersiveCfg",      "(II)V",                        (void *) glSetImmersiveCfg}, //设置沉浸式配置
                {"glReleaseView",          "()V",                          (void *) glReleaseView}      //释放视图
        };
        JNIEnv *env;
        if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK)
            return -1;
        jclass clazz = env->FindClass("br/com/carlosrafaelgn/fplay/visualizer/SimpleVisualizerJni");//获取调用者的一个class对象
        if (!clazz)
            return -1;
        env->RegisterNatives(clazz, methodTable, sizeof(methodTable) / sizeof(methodTable[0]));     //动态注册原生方法
        return JNI_VERSION_1_6;
    }

    JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
        if (glSoundParticle) {
            delete glSoundParticle;
            glSoundParticle = 0;
        }

        LOGD("go to JNI_OnUnload");
    }

}
