# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#获取方法属性的so库
add_library(
        native-lib
        SHARED
        native-lib.cpp
        ${native_srcs})

#数组操作的so库
add_library(
        array_operation
        SHARED
        array_operation.cpp)


include_directories(src/main/cpp/)

find_library(
        log-lib
        log )


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
set(CMAKE_VERBOSE_MAKEFILE on)
set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../app)
set_target_properties(native-lib PROPERTIES
                      LIBRARY_OUTPUT_DIRECTORY
                      ${distribution_DIR}/libs/${ANDROID_ABI})

target_link_libraries(
       native-lib
       ${log-lib} )

target_link_libraries(
        array_operation
        ${log-lib})