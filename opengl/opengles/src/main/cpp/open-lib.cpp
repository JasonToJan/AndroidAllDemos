#include <string>
#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_jan_jason_opencv_OpenGlActivity_stringFromJNI(JNIEnv* env, jobject) {

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}




