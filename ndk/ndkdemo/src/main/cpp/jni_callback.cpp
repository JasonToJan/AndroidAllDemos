#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <pthread.h>
#include <cassert>
#include <cinttypes>
#include "LogUtils.h"

typedef struct jni_callback {
    JavaVM * javaVM;
    jclass jniHandlerClz;
    jobject jniHandlerObj;
    jclass jniCallbackDemoClz;
    jobject jniCallbackDemoObj;
    pthread_mutex_t lock;
    int done;
} JniCallback;
JniCallback jniCallback;

/**
 * 这个方法仅仅调用了Java中的两个方法而已
 * @param env
 */
void queryRuntimeInfo(JNIEnv *env) {

    //获取getBuildVersion的方法id
    jmethodID staticMethodId = env->GetStaticMethodID(jniCallback.jniHandlerClz, "getBuildVersion",
                                                      "()Ljava/lang/String;");
    if (!staticMethodId) {
        LOGE("Failed to retrieve getBuildVersion() methodID");
        return;
    }
    //执行静态方法获取getBuildVersion的方法id
    jstring releaseVersion = static_cast<jstring>(env->CallStaticObjectMethod(
            jniCallback.jniHandlerClz, staticMethodId));

    //获取字符串的地址
    const char *version = env->GetStringUTFChars(releaseVersion, nullptr);
    if (!version) {
        LOGE("Unable to get version string");
        return;
    }
    LOGD("releaseVersion = %s", version);
    //释放字符串的内存
    env->ReleaseStringUTFChars(releaseVersion, version);
    //删除引用
    env->DeleteLocalRef(releaseVersion);

    //获取非静态方法 getRuntimeMemorySize 的id
    jmethodID methodId = env->GetMethodID(jniCallback.jniHandlerClz, "getRuntimeMemorySize", "()J");
    if (!methodId) {
        LOGE("Failed to retrieve getRuntimeMemorySize() methodID");
        return;
    }
    //调用 getRuntimeMemorySize
    jlong freeMemorySize = env->CallLongMethod(jniCallback.jniHandlerObj, methodId);

    //打印可用内存
    LOGD("Runtime free memory size: %" PRId64, freeMemorySize);
}

/**
 * JNI最开始会执行，说白了，这个函数就是为了初始化结构体
 * @param vm
 * @param reserved
 * @return
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {

    LOGD("JNI_OnLoad");
    JNIEnv *env;
    //给jniCallback初始化地址 结构体 初始化地址
    memset(&jniCallback, 0, sizeof(jniCallback));

    jniCallback.javaVM = vm;//初始化Java虚拟机环境

    //判断环境是否准备好
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    //获取JniCallbackDemo.JniHandler 类 静态内部类
    jclass clz = env->FindClass("jan/jason/ndkdemo/JniCallbackDemo$JniHandler");
    //赋值到结构体，全局引用只能由一个JNI函数创建（NewGlobalRef）
    jniCallback.jniHandlerClz = static_cast<jclass>(env->NewGlobalRef(clz));
    if (!clz) {
        LOGE("FindClass JniCallbackDemo$JniHandler error");
        return JNI_ERR;
    }
    //获取构造函数
    jmethodID initMethodId = env->GetMethodID(jniCallback.jniHandlerClz, "<init>", "()V");
    //构建类，通过构造函数，获取其中一个实例，但这个不是全局的
    jobject instance = env->NewObject(jniCallback.jniHandlerClz, initMethodId);
    if (!instance) {
        LOGE("NewObject jniHandler error");
        return JNI_ERR;
    }
    //赋值到结构体，实例，通过虚拟机创建一个全局实例
    jniCallback.jniHandlerObj = env->NewGlobalRef(instance);

    //调用 JniHandler 的相关方法
    queryRuntimeInfo(env);

    jniCallback.done = 0;
    jniCallback.jniCallbackDemoObj = nullptr;
    return JNI_VERSION_1_6;
}

/**
 * Jni 发送消息给 Java
 * 调用 类 instance 的 void方法 methodId
 */
void sendJavaMsg(JNIEnv *env, jobject instance, jmethodID methodId, const char *msg) {
    LOGD("jni sendJavaMsg");
    //获取字符串
    jstring javaMsg = env->NewStringUTF(msg);
    //调用对应方法
    env->CallVoidMethod(instance, methodId, javaMsg);
    //删除本地引用
    env->DeleteLocalRef(javaMsg);
}

/**
 * 开始计时 局部细节实现
 * @param context
 * @return
 */
void * StartTiming(void * ptr) {
    //获取参数 将jin_callback 强制转换为指针类型
    JniCallback * myJniCallback = static_cast<jni_callback *>(ptr);//强制转换
    JavaVM * javaVm = myJniCallback->javaVM;
    JNIEnv * env;

    jint res = javaVm->GetEnv((void **) (&env), JNI_VERSION_1_6);//根据虚拟机获取执行环境
    LOGD("javaVm->GetEnv() res = %d", res);
    if (res != JNI_OK) {
        //链接到虚拟机，关联当前线程，看是否OK
        res = javaVm->AttachCurrentThread(&env, nullptr);
        if (JNI_OK != res) {
            LOGD("Failed to AttachCurrentThread, ErrorCode = %d", res);
            return nullptr;
        }else{
            LOGD("Successed to AttachCurrentThread, ErrorCode = %d", res);
        }
    } else {
        LOGD("javaVm GetEnv JNI_OK");
    }

    //获取 JniHandler 的 updateStatus 函数
    jmethodID statusId = env->GetMethodID(myJniCallback->jniHandlerClz, "updateStatus","(Ljava/lang/String;)V");

    sendJavaMsg(env, myJniCallback->jniHandlerObj, statusId,
            "TimeThread status: initializing...");

    //获取 JniCallbackDemo 的 printTime 函数 但还没调用
    jmethodID timerId = env->GetMethodID(myJniCallback->jniCallbackDemoClz, "printTime", "()V");

    //声明时间变量
    struct timeval beginTime, curTime, usedTime, leftTime;
    const struct timeval myTimeval = {
            (__kernel_time_t) 1,
            (__kernel_suseconds_t) 0
    };

    sendJavaMsg(env, myJniCallback->jniHandlerObj, statusId,
                "TimeThread status: prepare startTiming ...");

    //死循环
    while (true) {
        //获取当前的时间 赋值给 beginTime
        gettimeofday(&beginTime, nullptr);

        //锁住myJniCallback->done对象
        pthread_mutex_lock(&myJniCallback->lock);
        int done = myJniCallback->done;
        if (myJniCallback->done) {
            myJniCallback->done = 0;
        }
        pthread_mutex_unlock(&myJniCallback->lock);

        if (done) {
            LOGD("JniCallback done");
            break;//退出循环化
        }

        //调用 printTime 函数 打印一次时间
        env->CallVoidMethod(myJniCallback->jniCallbackDemoObj, timerId);

        //获取当前的时间 赋值给 curTime
        gettimeofday(&curTime, nullptr);

        //计算函数运行消耗的时间usedTime = curTime - beginTime
        timersub(&curTime, &beginTime, &usedTime);

        //计算需要等待的时间 leftTime = myTimeval(1秒钟) - usedTime
        timersub(&myTimeval, &usedTime, &leftTime);

        //构建等待的时间
        struct timespec sleepTime;
        sleepTime.tv_sec = leftTime.tv_sec;//秒
        sleepTime.tv_nsec = leftTime.tv_usec * 1000;//纳秒级别

        if (sleepTime.tv_sec <= 1) {
            //睡眠对应纳秒的时间 短延迟
            nanosleep(&sleepTime, nullptr);
        } else {//一般不会走下面
            sendJavaMsg(env, myJniCallback->jniHandlerObj, statusId,
                        "TimeThread error: processing too long!");
        }
    }

    //循环打破 走这里
    sendJavaMsg(env, myJniCallback->jniHandlerObj, statusId,
                "TimeThread status: ticking stopped");
    //释放线程
    javaVm->DetachCurrentThread();
    return ptr;
}

/**
 * Java中直接调用的开始计时方法
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_JniCallbackDemo_startTiming(JNIEnv *env, jobject instance) {

    LOGD("jni startTiming");
    //线程ID
    pthread_t threadInfo;
    //线程属性
    pthread_attr_t threadAttr;
    //初始化线程属性
    pthread_attr_init(&threadAttr);//调用线程初始化方法，已经定义好
    //设置脱离状态的属性
    pthread_attr_setdetachstate(&threadAttr, PTHREAD_CREATE_DETACHED);//设置线程属性，已经定义好

    //互斥锁
    pthread_mutex_t lock;
    //初始化互斥锁
    pthread_mutex_init(&lock, nullptr);//初始化线程互斥锁，已经定义好

    //获取当前类
    jclass clz = env->GetObjectClass(instance);
    //保存类和 实例 到 结构体中
    jniCallback.jniCallbackDemoClz = static_cast<jclass>(env->NewGlobalRef(clz));
    jniCallback.jniCallbackDemoObj = env->NewGlobalRef(instance);

    // StartTiming ：在线程中运行的函数  jniCallback 运行函数的参数 可以直接传函数进去（其实是函数的返回值）
    int result = pthread_create(&threadInfo, &threadAttr, StartTiming, &jniCallback);//创建一个子线程
    assert(result == 0);

    //删除线程属性
    pthread_attr_destroy(&threadAttr);
}

extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_JniCallbackDemo_stopTiming(JNIEnv *env, jobject instance) {
    LOGD("jni stopTiming");

    //加锁修改done值 因为上面计时的线程共用这个done属性，所以上面的线程会break出循环
    pthread_mutex_lock(&jniCallback.lock);
    jniCallback.done = 1;
    pthread_mutex_unlock(&jniCallback.lock);

    //初始化等待时间
    struct timespec sleepTime;
    memset(&sleepTime, 0, sizeof(sleepTime));
    sleepTime.tv_nsec = 100000000;

    while (jniCallback.done) {
        nanosleep(&sleepTime, nullptr);
    }

    //删除引用
    env->DeleteGlobalRef(jniCallback.jniCallbackDemoClz);
    env->DeleteGlobalRef(jniCallback.jniCallbackDemoObj);
    jniCallback.jniCallbackDemoObj = nullptr;
    jniCallback.jniCallbackDemoClz = nullptr;

    //删除互斥锁
    //注意：如果这时候再点击start会崩，因为这个lock已经被销毁了，如果想实现暂停效果，就注释掉这句
    pthread_mutex_destroy(&jniCallback.lock);
}