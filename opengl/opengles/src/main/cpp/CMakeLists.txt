# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

##官方标准配置
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

add_library(
        lesson-lib
        SHARED
        # graphics
        graphics/GLUtils.h
        graphics/GLUtils.cpp
        graphics/Matrix.h
        graphics/Matrix.cpp

        # Provides a relative path to your source file(s).
        # lesson1
        lesson1/Native1Lesson.cpp
        lesson1/Native1Lesson.h

        open-lib.cpp
        ${native_srcs})

include_directories(src/main/cpp/)
find_library(
        log-lib
        log )


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
set(CMAKE_VERBOSE_MAKEFILE on)
set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../app)
set_target_properties(lesson-lib PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY
        ${distribution_DIR}/libs/${ANDROID_ABI})

if (${ANDROID_PLATFORM_LEVEL} LESS 11)
    message(FATAL_ERROR "OpenGL 2 is not supported before API level 11 (currently using ${ANDROID_PLATFORM_LEVEL}).")
    return()
elseif (${ANDROID_PLATFORM_LEVEL} LESS 18)
    add_definitions("-DDYNAMIC_ES3")
    set(OPENGL_LIB GLESv2)
else ()
    set(OPENGL_LIB GLESv3)
endif (${ANDROID_PLATFORM_LEVEL} LESS 11)


# 如果minSdkVersion 小于 18 则需要修改为下面注释代码
#target_link_libraries(native-gles3
#        android
#        EGL
#        GLESv3
#        log)

target_link_libraries(
        lesson-lib
        android
        ${OPENGL_LIB}
        log)



