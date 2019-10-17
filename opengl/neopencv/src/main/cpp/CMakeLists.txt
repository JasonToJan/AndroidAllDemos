# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

##官方标准配置
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

add_library(
       neopencv-lib
        SHARED
        # graphics
        open-lib.cpp
        ${native_srcs})

include_directories(src/main/cpp/)
find_library(
        log-lib
        log )

# 如果minSdkVersion 小于 18 则需要修改为下面注释代码
#target_link_libraries(native-gles3
#        android
#        EGL
#        GLESv3
#        log)

target_link_libraries(
        neopencv-lib
        android
        ${OPENGL_LIB}
        log)



