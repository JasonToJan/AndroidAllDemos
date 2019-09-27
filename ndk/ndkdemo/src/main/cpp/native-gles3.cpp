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
        "   fragColor = vec4(1.00,0.15,0.14,1.0);\n" //三角形颜色
        "}\n";

static float color_r;
static float color_g;
static float color_b;

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
    color_r += 0.01f;//红色值+0.01
    if (color_r > 1.0f) {
        color_g += 0.01f;//红色值加满后加绿色值
        if (color_g > 1.0f) {
            color_b += 0.01f;//绿色值加满后加蓝色值
            if (color_b > 1.0f) {
                color_r = 0.01f;//都加满后，还原为白色
                color_g = 0.01f;
                color_b = 0.01f;
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
    glLinkProgram(programObj);//不调用，就没东西

    //检查程序链接状态
    GLint linkRes;
    glGetProgramiv(programObj, GL_LINK_STATUS, &linkRes);//不调用也是可以的

    if (!linkRes) {//链接失败
        //获取日志长度
        GLint infoLen;
        glGetProgramiv(programObj, GL_INFO_LOG_LENGTH, &infoLen);//错误日志
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
 * 仅仅在第一次打开或视图发生变化是执行 并不是一直绘制的
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

    changeBg(); //改变颜色值，仅仅是一个值而已

    glClearColor(color_r, color_g, color_b, 1.0f);//设置 清除颜色
    checkGlError("glClearColor");

    glClear(GL_COLOR_BUFFER_BIT); //清空颜色缓冲区，把窗口清除为当前颜色
    checkGlError("glClear");

    glUseProgram(program); //设置为活动程序，可以理解成openGL的钥匙，没有的话就打不开openGl的大门
    checkGlError("glUseProgram");

    /**
     * 第一个参数指定从索引0开始取数据，与顶点着色器中layout(location=0)对应。
     * 第二个参数指定顶点属性大小。
     * 第三个参数指定数据类型。
     * 四个参数定义是否希望数据被标准化（归一化），只表示方向不表示大小。
     * 第五个参数是步长（Stride），指定在连续的顶点属性之间的间隔。
     */
    glVertexAttribPointer(vertexIndex, 3, GL_FLOAT, GL_FALSE, 0, vVertex); //加载三角形顶点坐标
    checkGlError("glVertexAttribPointer");

    glEnableVertexAttribArray(vertexIndex); //启用通用顶点属性数组
    checkGlError("glEnableVertexAttribArray");

    //当采用顶点数组方式绘制图形时，使用该函数
    glDrawArrays(GL_TRIANGLES, 0, 3); //绘制三角形
    checkGlError("glDrawArrays");

    //禁用通用顶点属性数组
    glDisableVertexAttribArray(vertexIndex);
    checkGlError("glDisableVertexAttribArray");
}