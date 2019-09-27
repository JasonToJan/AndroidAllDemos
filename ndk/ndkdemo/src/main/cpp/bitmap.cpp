#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <cstring>
#include "LogUtils.h"


extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_JniBitmapDemo_passBitmap(JNIEnv *env, jobject instance, jobject bitmap) {

    if (nullptr == bitmap) {
        LOGE("bitmap is null");
    }

    AndroidBitmapInfo info;
    int result;

    //获取图片信息
    result = AndroidBitmap_getInfo(env, bitmap, &info);

    if (result != ANDROID_BITMAP_RESUT_SUCCESS) {
        LOGE("AndroidBitmap_getInfo failed, result: %d", result);
        return;
    }

    LOGD("bitmap width: %d, height: %d, format: %d, stride: %d", info.width, info.height,
         info.format, info.stride);

    unsigned char *addrPtr;//理解为字符串，或字符指针

    // 获取像素信息,这里理解为将字符串强制变成字符串数组，或将字符指针强制转为字符串指针
    result = AndroidBitmap_lockPixels(env, bitmap, reinterpret_cast<void **>(&addrPtr));

    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGE("AndroidBitmap_lockPixels failed, result: %d", result);
        return;
    }

    // 执行图片操作的逻辑
    int length = info.stride * info.height; //4x1=4
    for (int i = 0; i < length; ++i) {
        LOGD("value: %x", addrPtr[i]);//33 66 99 ff
    }

    // 像素信息不再使用后需要解除锁定
    result = AndroidBitmap_unlockPixels(env, bitmap);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGE("AndroidBitmap_unlockPixels failed, result: %d", result);
    }

}