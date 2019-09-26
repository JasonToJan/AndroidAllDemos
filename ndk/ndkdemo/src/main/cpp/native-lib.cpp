#include <string>
#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_stringFromJNI(JNIEnv* env,jobject) {

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}

/**
 * 修改成员变量
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_accessField(JNIEnv *env, jobject instance) {

    //获取类
    jclass jcla=env->GetObjectClass(instance);
    //获取类的成员变量showText的id,
    jfieldID jfieldId=env->GetFieldID(jcla,"showText","Ljava/lang/String;");
    jstring after=env->NewStringUTF("Hello NDK");
    env->SetObjectField(instance,jfieldId,after);
}

/**
 * 修改静态变量
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_accessStaticField(JNIEnv *env, jobject instance) {
    //获取类
    jclass jcla=env->GetObjectClass(instance);
    //获取静态变量ID
    jfieldID staticId=env->GetStaticFieldID(jcla,"staticString","Ljava/lang/String;");
    //重新设置静态变量
    jstring after=env->NewStringUTF("change the static string in JNI successfully.");
    env->SetStaticObjectField(jcla,staticId,after);
    //流程： 先获取类实例，再获取类中的静态变量id,再根据id 设置一个新的值给它。
}

/**
 * 访问Java方法
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_accessMethod(JNIEnv *env, jobject thiz) {
    jclass jclass1=env->GetObjectClass(thiz);
    //Class实例获取methodId
    jmethodID methodId=env->GetMethodID(jclass1,"normalFunction","(Ljava/lang/String;)Ljava/lang/String;");
    jstring myString=env->NewStringUTF("the wold is from JNI");
    //调用方法还是需要jobject实例才行，区别于jclass,这里必须为thiz
    jobject  jobject1=env->CallObjectMethod(thiz,methodId,myString);

    return static_cast<jstring>(jobject1);//JNI层的强制将object转换为string
}

/**
 * 访问Java静态方法
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_accessStaticMethod(JNIEnv *env, jobject thiz) {

    jclass jclass1=env->GetObjectClass(thiz);
    jmethodID jmethodId=env->GetStaticMethodID(jclass1,"staticFunction","(Ljava/lang/String;)Ljava/lang/String;");
    jstring myString=env->NewStringUTF("this sentence is from JNI");
    //注意，这里需要传一个class对象，上面非静态方法要传jobject实例
    jobject jobject1=env->CallStaticObjectMethod(jclass1,jmethodId,myString);
    return static_cast<jstring>(jobject1);

}

/**
 * 返回一个Java中的Date类型 举一反三：访问Java的构造方法
 */
extern "C"
JNIEXPORT jobject JNICALL
Java_jan_jason_ndkdemo_NdkDemoActivity_accessConstructor(JNIEnv *env, jobject thiz) {

    //拿到一个jclass的实例
    //jclass jclass1=env->GetObjectClass(thiz);获取执行该native方法的类中的class对象
    jclass jcla=env->FindClass("java/util/Date");
    jmethodID jmethodId=env->GetMethodID(jcla,"<init>","()V");
    jobject jobject1=env->NewObject(jcla,jmethodId);

    return jobject1;
}


