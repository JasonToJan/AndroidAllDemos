

#include "Native1Lesson.h"
#include "../graphics/GLUtils.h"
#include <android/log.h>

#define  LOG_TAG    "lesson1"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/**
 * 静态函数，打印字符串
 * @param name
 * @param s
 */
static void printGLString(const char *name, GLenum s) {
    const char *v = (const char *) glGetString(s);
    LOGI("GL %s = %s \n", name, v);
}

/**
 * 静态函数，检查GL的错误
 * @param op
 */
static void checkGlError(const char *op) {
    for (GLint error = glGetError(); error; error = glGetError()) {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
}

/**
 * opencv 顶点着色源码
 */
const char *VERTEX_SHADER =
        "uniform mat4 u_MVPMatrix;        \n"     // A constant representing the combined model/view/projection matrix.
                "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
                "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
                "void main()                    \n"     // The entry point for our vertex shader.
                "{                              \n"
                "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
                "   gl_Position = u_MVPMatrix * a_Position; \n"     // gl_Position is a special variable used to store the final position.
                "}                              \n";    // normalized screen coordinates.

/**
 * 片段着色源码
 */
const char *FRAGMENT_SHADER = "precision mediump float;         \n"     // Set the default precision to medium. We don't need as high of a
        "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
        "void main()                    \n"     // The entry point for our fragment shader.
        "{                              \n"
        "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
        "}                              \n";

//########################################定义常量 开始###############################################
// This triangle is red, green, and blue.
GLfloat triangle1VerticesData[] = {
        // X, Y, Z,
        // R, G, B, A
        -0.5f, -0.25f, 0.0f,
        1.0f, 0.0f, 0.0f, 1.0f,

        0.5f, -0.25f, 0.0f,
        0.0f, 0.0f, 1.0f, 1.0f,

        0.0f, 0.559016994f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f};

// This triangle is yellow, cyan, and magenta.
GLfloat triangle2VerticesData[] = {
        // X, Y, Z,
        // R, G, B, A
        -0.5f, -0.25f, 0.0f,
        1.0f, 1.0f, 0.0f, 1.0f,

        0.5f, -0.25f, 0.0f,
        0.0f, 1.0f, 1.0f, 1.0f,

        0.0f, 0.559016994f, 0.0f,
        1.0f, 0.0f, 1.0f, 1.0f};

// This triangle is white, gray, and black.
GLfloat triangle3VerticesData[] = {
        // X, Y, Z,
        // R, G, B, A
        -0.5f, -0.25f, 0.0f,
        1.0f, 1.0f, 1.0f, 1.0f,

        0.5f, -0.25f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f,

        0.0f, 0.559016994f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f};
//########################################定义常量 end###############################################

/**
 * 构造函数
 */
Native1Lesson::Native1Lesson() {
    mModelMatrix = NULL;
    mMVPMatrix = NULL;
    mProjectionMatrix = NULL;
    mViewMatrix = NULL;
}

/**
 * 虚构函数
 */
Native1Lesson::~Native1Lesson() {
    delete mModelMatrix;
    mModelMatrix = NULL;
    delete mMVPMatrix;
    mMVPMatrix = NULL;
    delete mProjectionMatrix;
    mProjectionMatrix = NULL;
    delete mViewMatrix;
    mViewMatrix = NULL;
}

/**
 * 创建
 */
void Native1Lesson::create() {

    printGLString("Version", GL_VERSION);
    printGLString("Vendor", GL_VENDOR);
    printGLString("Renderer", GL_RENDERER);
    printGLString("Extensions", GL_EXTENSIONS);

    //这里需要传入地址，字符串的地址即可
    mProgram = GLUtils::createProgram(&VERTEX_SHADER, &FRAGMENT_SHADER);
    if (!mProgram) {
        LOGD("Could not create program");
        return;
    }

    mModelMatrix = new Matrix();//modle矩阵，干什么的：
    mMVPMatrix = new Matrix();//mvp矩阵，干什么的：

    // Position the eye in front of the origin.
    float eyeX = 0.0f;
    float eyeY = 0.0f;
    float eyeZ = 1.5f;

    // We are looking at the origin
    float centerX = 0.0f;
    float centerY = 0.0f;
    float centerZ = 0.0f;

    // Set our up vector.
    float upX = 0.0f;
    float upY = 1.0f;
    float upZ = 0.0f;

    // Set the view matrix. 调用自己的方法 这个Matrix是自己创建的 静态函数
    mViewMatrix = Matrix::newLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
}

/**
 * 改变
 * @param width
 * @param height
 */
void Native1Lesson::change(int width, int height) {

    /**
     * X，Y————以像素为单位，指定了视口的左下角（在第一象限内，以（0，0）为原点的）位置。
     * width，height————表示这个视口矩形的宽度和高度，根据窗口的实时变化重绘窗口。
     */
    glViewport(0, 0, width, height);

    // Create a new perspective projection matrix. The height will stay the same
    // while the width will vary as per aspect ratio.
    float ratio = (float) width / height;
    float left = -ratio;
    float right = ratio;
    float bottom = -1.0f;
    float top = 1.0f;
    float near = 1.0f;
    float far = 2.0f;

    /**
     * 调用静态函数
     */
    mProjectionMatrix = Matrix::newFrustum(left, right, bottom, top, near, far);
}

/**
 * 绘制
 */
void Native1Lesson::draw() {
    //下面两句是改变颜色的
    glClearColor(0.5F, 0.5F, 0.5F, 0.5F);
    glClear(GL_COLOR_BUFFER_BIT);
    checkGlError("glClear");

    glUseProgram(mProgram);

    mMVPMatrixHandle = (GLuint) glGetUniformLocation(mProgram, "u_MVPMatrix");
    mPositionHandle = (GLuint) glGetAttribLocation(mProgram, "a_Position");
    mColorHandle = (GLuint) glGetAttribLocation(mProgram, "a_Color");

    long time = GLUtils::currentTimeMillis() % 10000L;
    float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

    // Draw the triangle facing straight on.
    mModelMatrix->identity();
    mModelMatrix->rotate(angleInDegrees, 0.0f, 0.0f, 1.0f);
    drawTriangle(triangle1VerticesData);

    // Draw one translated a bit down and rotated to be flat on the ground.
    mModelMatrix->identity();
    mModelMatrix->translate(0.0f, -1.0f, 0.0f);
    mModelMatrix->rotate(90.0f, 1.0f, 0.0f, 0.0f);
    mModelMatrix->rotate(angleInDegrees, 0.0f, 0.0f, 1.0f);
    drawTriangle(triangle2VerticesData);

    // Draw one translated a bit to the right and rotated to be facing to the left.
    mModelMatrix->identity();
    mModelMatrix->translate(1.0f, 0.0f, 0.0f);
    mModelMatrix->rotate(90.0f, 0.0f, 1.0f, 0.0f);
    mModelMatrix->rotate(angleInDegrees, 0.0f, 0.0f, 1.0f);
    drawTriangle(triangle3VerticesData);
}

/**
 * 绘制三角形
 * @param verticesData
 */
void Native1Lesson::drawTriangle(GLfloat *verticesData) {

    /**
     * 解析顶点数据
     * 第一个参数指定从索引0开始取数据，与顶点着色器中layout(location=0)对应。
     * 第二个参数指定顶点属性大小。
     * 第三个参数指定数据类型。
     * 第四个参数定义是否希望数据被标准化（归一化），只表示方向不表示大小。
     * 第五个参数是步长（Stride），指定在连续的顶点属性之间的间隔。上面传0和传4效果相同，如果传1取值方式为0123、1234、2345……
     * 第六个参数表示我们的位置数据在缓冲区起始位置的偏移量
     */
    glVertexAttribPointer(
            (GLuint) mPositionHandle,
            3,
            GL_FLOAT,
            GL_FALSE,
            4 * 7,
            verticesData
    );

    //启用index指定的通用顶点属性数组
    glEnableVertexAttribArray((GLuint) mPositionHandle);

    //另一个顶点
    glVertexAttribPointer(
            (GLuint) mColorHandle,
            4,
            GL_FLOAT,
            GL_FALSE,
            4 * 7,
            verticesData + 3
    );
    //启用index指定的通用顶点属性数组
    glEnableVertexAttribArray((GLuint) mColorHandle);

    // model * view
    mMVPMatrix->multiply(*mViewMatrix, *mModelMatrix);

    // model * view * projection
    mMVPMatrix->multiply(*mProjectionMatrix, *mMVPMatrix);

    /**
     * 通过一致变量（uniform修饰的变量）引用将一致变量值传入渲染管线。
     *  location : uniform的位置。
     *  count : 需要加载数据的数组元素的数量或者需要修改的矩阵的数量。
     *  transpose : 指明矩阵是列优先(column major)矩阵（GL_FALSE）还是行优先(row major)矩阵（GL_TRUE）。
     *  value : 指向由count个元素的数组的指针。
     */
    glUniformMatrix4fv(mMVPMatrixHandle, 1, GL_FALSE, mMVPMatrix->mData);

    //提供绘制功能。当采用顶点数组方式绘制图形时，使用该函数。
    // 该函数根据顶点数组中的坐标数据和指定的模式，进行绘制。
    glDrawArrays(GL_TRIANGLES, 0, 3);
    checkGlError("glDrawArrays");
}


/// =======================================================
// 静态指针
static Native1Lesson * native1Lesson;

extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_opencv_lessons_lesson1_LessonOneNativeRenderer_nativeSurfaceCreate(
        JNIEnv *env,
        jclass type) {

    native1Lesson = new Native1Lesson();
    if (native1Lesson != nullptr) {
        native1Lesson->create();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_opencv_lessons_lesson1_LessonOneNativeRenderer_nativeSurfaceChange(
        JNIEnv *env,
        jclass type,
        jint width,
        jint height) {
    if (native1Lesson != nullptr) {
        native1Lesson->change(width, height);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_jan_jason_opencv_lessons_lesson1_LessonOneNativeRenderer_nativeDrawFrame(
        JNIEnv *env,
        jclass type) {

    if (native1Lesson != nullptr) {
        native1Lesson->draw();
    }
}
