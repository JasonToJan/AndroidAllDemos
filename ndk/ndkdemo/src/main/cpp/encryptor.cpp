#include <jni.h>

#include <android/log.h>
#include <cstdio>
#include <cstring>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"Encryptor",FORMAT,##__VA_ARGS__);

/**
 * 全局字符串
 */
char password[] = "JasonJan";


/**
 * JNI 创建文件
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_Encryptor_createFile(JNIEnv *env, jobject instance,
                                                    jstring normalPath_) {
    //将Java中的字符串 转换为c++ 可以识别的 东西 比如char指针
    const char *normalPath = env->GetStringUTFChars(normalPath_, nullptr);
    //w表示：以“写入”方式打开文件。如果文件不存在，那么创建一个新文件；如果文件存在，那么清空文件内容（相当于删除原文件，再创建一个新文件）。
    //b表示二进制文件
    FILE *fp = fopen(normalPath, "wb");
    //把字符串写入到指定的流 stream 中，但不包括空字符。
    fputs("Hi, this file is created by JNI.", fp);
    //关闭流 fp。刷新所有的缓冲区
    fclose(fp);
    //释放JVM保存的字符串的内存
    env->ReleaseStringUTFChars(normalPath_, normalPath);//这里猜测GetStringUTFChars也是通过new的，所以要释放内存
}

/**
 * JNI 加密方法
 * normalPath:源文件名
 * encryptPath:加密文件名
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_Encryptor_encryption(JNIEnv *env, jclass type, jstring normalPath_,
                                                    jstring encryptPath_) {
    //获取字符串保存在JVM中内存中，都是通过new新建的一个字符指针，所以后面要释放
    const char *normalPath = env->GetStringUTFChars(normalPath_, nullptr);
    const char *encryptPath = env->GetStringUTFChars(encryptPath_, nullptr);

    LOGE("normalPath = %s, encryptPath = %s", normalPath, encryptPath);

    //rb:只读打开一个二进制文件，允许读数据。
    //wb:只写打开或新建一个二进制文件；只允许写数据
    FILE *normal_fp = fopen(normalPath, "rb");//读源文件，获取一个文件指针
    FILE *encrypt_fp = fopen(encryptPath, "wb");//写加密文件，获取一个文件指针

    if (normal_fp == nullptr) {
        LOGE("%s", "文件打开失败");
        return;
    }

    int ch = 0;
    int i = 0;
    size_t pwd_length = strlen(password);//size_t是一种特有类型，一般用来存放长度类型的长度单位
    while ((ch = fgetc(normal_fp)) != EOF) { //fgetc：获取文件中字符，读完后光标自动+1
        fputc(ch ^ password[i % pwd_length], encrypt_fp);// 每一个字符都和秘钥中的一个字符进行异或运算进行加密
        i++;
    }

    //关闭流 normal_fp和encrypt_fp。刷新所有的缓冲区
    fclose(normal_fp);
    fclose(encrypt_fp);

    //释放JVM保存的字符串的内存
    env->ReleaseStringUTFChars(normalPath_, normalPath);
    env->ReleaseStringUTFChars(encryptPath_, encryptPath);
}

/**
 * 解密文件
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_Encryptor_decryption(JNIEnv *env, jclass type, jstring encryptPath_,
                                                    jstring decryptPath_) {
    const char *encryptPath = env->GetStringUTFChars(encryptPath_, nullptr);
    const char *decryptPath = env->GetStringUTFChars(decryptPath_, nullptr);

    LOGE("encryptPath = %s, decryptPath = %s", encryptPath, decryptPath);

    //rb:只读打开一个二进制文件，允许读数据。
    //wb:只写打开或新建一个二进制文件；只允许写数据
    FILE *encrypt_fp = fopen(encryptPath, "rb");//加密后的文件指针
    FILE *decrypt_fp = fopen(decryptPath, "wb");//解密后的文件，没有的话，会自己创建一个文件

    if (encrypt_fp == nullptr) {
        LOGE("%s", "加密文件打开失败");
        return;
    }

    int ch;
    int i = 0;
    size_t pwd_length = strlen(password);
    while ((ch = fgetc(encrypt_fp)) != EOF) {
        fputc(ch ^ password[i % pwd_length], decrypt_fp);//同加密，这里再异或一次，自然就是解密了
        i++;
    }

    //关闭流 encrypt_fp 和 decrypt_fp。刷新所有的缓冲区
    fclose(encrypt_fp);
    fclose(decrypt_fp);

    //释放JVM保存的字符串的内存
    env->ReleaseStringUTFChars(encryptPath_, encryptPath);
    env->ReleaseStringUTFChars(decryptPath_, decryptPath);
}

