cmake_minimum_required(VERSION 3.4.1)

# Creates the project's shared lib: libnative-lib.so.
# The lib is loaded by this project's Java code in MainActivity.java:
#     System.loadLibrary("native-lib");
# The lib name in both places must match.

include_directories(${CMAKE_SOURCE_DIR}/ncnn-20230517-android-vulkan/include/ncnn)
include_directories(${pathToProject}/src/main/cpp/includes)

set(ncnn_DIR ${CMAKE_SOURCE_DIR}\\ncnn-20230517-android-vulkan\\${ANDROID_ABI}\\lib\\cmake\\ncnn)
find_package(ncnn REQUIRED)

#include_directories(${OpenCV_DIR}/jni/include)
#add_library( lib_opencv SHARED IMPORTED )
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${OpenCV_DIR}/libs/${ANDROID_ABI}/libopencv_java4.so)

add_library( native-lib
        SHARED

        native-lib.cpp
        ndkcamera.cpp
        ndkcamera.h
       )

find_library(log-lib
        log )
find_library( android-lib android )
find_library( gl-lib GLESv2 )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

set(build_DIR ../../../../app/build)
set(BUILD_SUBDIR ${ANDROID_ABI})

target_link_libraries( # Specifies the target library.
        native-lib

        ncnn

#        lib_opencv
        camera2ndk
        mediandk

        -landroid # Add this.
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ${android-lib}
        ${gl-lib}
        )