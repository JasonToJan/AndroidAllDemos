#include <jan_jason_ndkdemo_NdkDemoActivity.h>
#include <string>
#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL

Java_jan_jason_ndkdemo_NdkDemoActivity_stringFromJNI(JNIEnv* env,jobject) {

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}

extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_accessField(JNIEnv *env, jobject instance) {

    //获取类
    jclass jcla=env->GetObjectClass(instance);

    //获取类的成员变量showText的id
    jfieldID jfieldId=env->GetFieldID(jcla,"showText","Ljava/lang/string;");

    jstring after=env->NewStringUTF("Hello NDK");

    env->SetObjectField(instance,jfieldId,after);
}

