#include <jni.h>
#include <string>

#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>

#include <android/log.h>
#include "LogUtils.h"

/**
 * 顶点着色器源码
 * auto：用来声明自动变量。它是存储类型标识符，
 * 表明变量(自动)具有本地范围，块范围的变量声明(如for循环体内的变量声明)默认为auto存储类型。
 * 其实大多普通声明方式声明的变量都是auto变量,他们不需要明确指定auto关键字，默认就是auto的了。
 * auto变量在离开作用域是会变程序自动释放，不会发生内存溢出情况(除了包含指针的类)。
 * 使用auto变量的优势是不需要考虑去变量是否被释放，比较安全。
 */
auto gl_vertexShader_source =
        "#version 300 es\n"
        "layout(location = 0) in vec4 vPosition;\n"
        "void main() {\n"
        "   gl_Position = vPosition;\n"
        "}\n";

/**
 * 片段着色器源码，openGl 源码
 */
auto gl_fragmentShader_source =
        "#version 300 es\n"
        "precision mediump float;\n"
        "out vec4 fragColor;\n"
        "void main() {\n"
        "   fragColor = vec4(1.0,1.0,0.0,1.0);\n"
        "}\n";

static float r;
static float g;
static float b;

/**
 * 着色器程序
 */
GLuint program;

/**
 * 顶点属性索引
 */
GLuint vertexIndex = 0;

/**
 * 顶点坐标
 */
const GLfloat vVertex[] = {
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
};


/**
 * 输出GL的属性值
 */
static void printGLString(const char *name, GLenum s) {
    const char *glName = reinterpret_cast<const char *>(glGetString(s));
    LOGE("GL %s = %s", name, glName);
}

static void checkGlError(const char *op) {
    for (GLint error = glGetError(); error; error = glGetError()) {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
}

/**
 * 修改背景颜色
 */
void changeBg() {
    r += 0.01f;
    if (r > 1.0f) {
        g += 0.01f;
        if (g > 1.0f) {
            b += 0.01f;
            if (b > 1.0f) {
                r = 0.01f;
                g = 0.01f;
                b = 0.01f;
            }
        }
    }
}

/**
 * 编译着色器源码
 * @param shaderType 着色器类型
 * @param shaderSource  源码
 * @return
 */
GLuint compileShader(GLenum shaderType, const char *shaderSource) {
    //创建着色器对象
    GLuint shader = glCreateShader(shaderType);
    if (!shader) {
        return 0;
    }
    //加载着色器源程序
    glShaderSource(shader, 1, &shaderSource, nullptr);
    //编译着色器程序
    glCompileShader(shader);

    //获取编译状态
    GLint compileRes;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compileRes);

    if (!compileRes) {
        //获取日志长度
        GLint infoLen = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);

        if (infoLen > 0) {
            char *infoLog = static_cast<char *>(malloc(sizeof(char) * infoLen));
            //获取日志信息
            glGetShaderInfoLog(shader, infoLen, nullptr, infoLog);
            LOGE("compile shader error : %s", infoLog);
            free(infoLog);
        }
        //删除着色器
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}


/**
 * 链接着色器程序
 */
GLuint linkProgram(GLuint vertexShader, GLuint fragmentShader) {
    //创建程序
    GLuint programObj = glCreateProgram();
    if (programObj == 0) {
        LOGE("create program error");
        return 0;
    }
    //加载着色器载入程序
    glAttachShader(programObj, vertexShader);//着色1
    checkGlError("glAttachShader");
    glAttachShader(programObj, fragmentShader);//着色2
    checkGlError("glAttachShader");

    //链接着色器程序
    glLinkProgram(programObj);

    //检查程序链接状态
    GLint linkRes;
    glGetProgramiv(programObj, GL_LINK_STATUS, &linkRes);

    if (!linkRes) {//链接失败
        //获取日志长度
        GLint infoLen;
        glGetProgramiv(programObj, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            //获取并输出日志
            char *infoLog = static_cast<char *>(malloc(sizeof(char) * infoLen));
            glGetProgramInfoLog(programObj, infoLen, nullptr, infoLog);
            LOGE("Error link program : %s", infoLog);
            free(infoLog);
        }
        //删除着色器程序
        glDeleteProgram(programObj);
        return 0;
    }
    return programObj;
}

/**
 * 供 Android 中的GLSurfaceView.Renderer 的onSurfaceChanged 调用。
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_OpenGL_GLES3Render_surfaceChanged(JNIEnv *env, jobject thiz, jint w, jint h) {

    printGLString("Version", GL_VERSION);
    printGLString("Vendor", GL_VENDOR);
    printGLString("Renderer", GL_RENDERER);
    printGLString("Extension", GL_EXTENSIONS);

    LOGD("surfaceChange(%d,%d)", w, h);//仅仅传递一次 w为设备的宽，h为设备的高度

    //编译着色器源码
    GLuint vertexShader = compileShader(GL_VERTEX_SHADER, gl_vertexShader_source);
    GLuint fragmentShader = compileShader(GL_FRAGMENT_SHADER, gl_fragmentShader_source);

    //链接着色器程序，返回一个着色代码号
    program = linkProgram(vertexShader, fragmentShader);

    if (!program) {
        LOGE("linkProgram error");
        return;
    }

    //设置程序窗口
    glViewport(0, 0, w, h);//设置画布大小
    checkGlError("glViewport");
}

/**
 * 供Android中的 GLSurfaceView.Renderer 的onDrawFrame 调用。
 * 这里才会执行真正
 */
extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_ndkdemo_OpenGL_GLES3Render_drawFrame(JNIEnv *env, jobject thiz) {

    //改变颜色值
    changeBg();

    glClearColor(r, g, b, 1.0f);
    checkGlError("glClearColor");
    //清空颜色缓冲区
    glClear(GL_COLOR_BUFFER_BIT);
    checkGlError("glClear");

    //设置为活动程序
    glUseProgram(program);
    checkGlError("glUseProgram");


    //加载顶点坐标
    glVertexAttribPointer(vertexIndex, 3, GL_FLOAT, GL_FALSE, 0, vVertex);
    checkGlError("glVertexAttribPointer");

    //启用通用顶点属性数组
    glEnableVertexAttribArray(vertexIndex);
    checkGlError("glEnableVertexAttribArray");

    //绘制三角形
    glDrawArrays(GL_TRIANGLES, 0, 3);
    checkGlError("glDrawArrays");

    //禁用通用顶点属性数组
    glDisableVertexAttribArray(vertexIndex);
    checkGlError("glDisableVertexAttribArray");
}