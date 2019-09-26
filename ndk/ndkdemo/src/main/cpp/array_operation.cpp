#include <jni.h>
#include <random>

//数组元素最大值
const jint max = 100;

extern "C"
JNIEXPORT jintArray JNICALL
Java_jan_jason_ndkdemo_JniArraryOperation_getIntArray(JNIEnv *env, jobject instance,jint length) {

    jintArray array = env->NewIntArray(length); //创建一个指定大小的数组
    jint *elementsP = env->GetIntArrayElements(array, nullptr);//获取到刚创建的整型数组的指针

    jint *startP = elementsP;//一个指针变量，为了遍历数组用
    for (; startP < elementsP + length; startP++) {
        *startP = static_cast<jint>(random() % max);//将随机值强制转换为int类型 给到当前指针变量指向的值
    }
    env->ReleaseIntArrayElements(array, elementsP, 0);//因为刚刚new了，所以要释放指针数组以及头指针，
    //注意：这里的释放并不是把这个数组完全抹杀掉，只是因为刚才在堆中是动态申请的数组，这里才释放的，但array依旧可以返回给别人用。
    return array;

}

/**
 * 比较函数
 * @param a
 * @param b
 * @return
 */
int compare(const void *a, const void *b) {
    return *(int *) a - *(int *) b;
}

extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_JniArraryOperation_sortIntArray(JNIEnv *env, jobject instance,jintArray arr_) {

    jint *arr = env->GetIntArrayElements(arr_, nullptr);//获取数组起始元素的指针

    jint len = env->GetArrayLength(arr_);//获取数组长度

    qsort(arr, len, sizeof(jint), compare); //排序

    //第三个参数 同步
    //0：Java数组进行更新，并且释放C/C++数组
    //JNI_ABORT：Java数组不进行更新，但是释放C/C++数组
    //JNI_COMMIT：Java数组进行更新，不释放C/C++数组(函数执行完后，数组还是会释放的)
    env->ReleaseIntArrayElements(arr_, arr, 0);//这里改变的是实际的数组值，实际值也是在JNI中new的
}


