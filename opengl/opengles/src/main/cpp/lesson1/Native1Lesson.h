
//防止Native1Lession.h被重复引用
#ifndef OPENGLLESSON_NATIVE1LESSON_H
#define OPENGLLESSON_NATIVE1LESSON_H

        #include <GLES2/gl2.h>
        #include "../graphics/Matrix.h"   //引入了自己创建的Matrix函数

        class Native1Lesson {

        public:

            Native1Lesson();//构造函数，new的时候立马执行

            ~Native1Lesson();//析构函数，当对象结束其生命周期，如对象所在的函数已调用完毕时，系统自动执行析构函数

            /**
             * 创建
             */
            void create();

            /**
             * 改变
             * @param width
             * @param height
             */
            void change(int width, int height);

            /**
             * 绘制
             */
            void draw();

            /**
             * 画三角形
             * @param verticesData
             */
            void drawTriangle(GLfloat *verticesData);

        private:

            Matrix * mViewMatrix;    //矩阵指针
            Matrix * mModelMatrix;   //modle 矩阵指针
            Matrix * mProjectionMatrix;
            Matrix * mMVPMatrix;

            GLuint mProgram;

            GLuint mMVPMatrixHandle;
            GLuint mPositionHandle;
            GLuint mColorHandle;


        };

#endif //OPENGLLESSON_NATIVE1LESSON_H
