#include <string>
#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_androidalldemo_jan_jason_neopencv_NeRecognizeActivity_stringFromJNI(JNIEnv* env, jobject) {

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}




