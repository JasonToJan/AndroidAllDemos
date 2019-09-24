#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL

Java_jan_jason_ndkdemo_NdkDemoActivity_stringFromJNI(JNIEnv* env,jobject) {

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}

