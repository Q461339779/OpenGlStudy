# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
# 声明变量：set(变量名 变量值)
#引用变量：${变量名}
set(jnilibs "${CMAKE_SOURCE_DIR}/../jniLibs") #生命jnilibs 变量 ， ${CMAKE_SOURCE_DIR} 为cmakelist.txt路径
message("变量名 = ${CMAKE_SOURCE_DIR}/../jniLibs")
#用于设定目录，这些设定的目录将被编译器用来查找 include 文件
include_directories(
        util
        render
        sample
        glm
        inc
        inc/assimp
        model
        opencv_3_0_0
        egl
        looper
        freetype
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#[[  指定文件保存到 src-files 变量中
G:/opencvProject/OpenGlStudy/app/src/main/cpp/JniImpl.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/Model3DAnimSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/EnglishTextSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/render/EGLRender.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/render/MyGLRenderContext.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/util/GLUtils.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/EnglishTextSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/Model3DAnimSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/Model3DSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/TextSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/sample/TriangleSample.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/egl/EglCore.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/egl/EglSurfaceBase.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/egl/OffscreenSurface.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/egl/WindowSurface.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/looper/GLRenderLooper.cpp;
G:/opencvProject/OpenGlStudy/app/src/main/cpp/looper/Looper.cpp ]]
file(GLOB src-files
        ${CMAKE_SOURCE_DIR}/*.cpp
        ${CMAKE_SOURCE_DIR}/sample/Model3DAnimSample.cpp
        ${CMAKE_SOURCE_DIR}/sample/EnglishTextSample.cpp
        ${CMAKE_SOURCE_DIR}/render/*.cpp
        ${CMAKE_SOURCE_DIR}/util/*.cpp
        ${CMAKE_SOURCE_DIR}/sample/*.cpp
        ${CMAKE_SOURCE_DIR}/egl/*.cpp
        ${CMAKE_SOURCE_DIR}/looper/*.cpp)

add_library( # Sets the name of the library.
             # 动态库的名字
             native-render

             # Sets the library as a shared library.
             # 设置编译为动态库
             SHARED

             # Provides a relative path to your source file(s).
             # 编译生成动态库的文件
             ${src-files}

        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )
#动态方式加载
add_library(assimp SHARED IMPORTED )
#引入so文件
set_target_properties(assimp PROPERTIES IMPORTED_LOCATION ${jnilibs}/${ANDROID_ABI}/libassimp.so)

add_library(opencv_java3 SHARED IMPORTED )
set_target_properties(opencv_java3 PROPERTIES IMPORTED_LOCATION ${jnilibs}/${ANDROID_ABI}/libopencv_java3.so)

add_library(freetype STATIC IMPORTED )
set_target_properties(freetype PROPERTIES IMPORTED_LOCATION ${jnilibs}/${ANDROID_ABI}/libfreetype.a)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#ASAN 需要 NDK20 编译，Android P 以上运行
#target_compile_options(native-render PUBLIC -fsanitize=address -fno-omit-frame-pointer)
#set_target_properties(native-render PROPERTIES LINK_FLAGS -fsanitize=address)

#SET(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -fno-rtti -fno-exceptions")
set(third-party-libs
        assimp
        opencv_java3
        freetype)
#用于指定target需要链接item1 item2
target_link_libraries( # Specifies the target library.
                       native-render

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       ${third-party-libs}
                       EGL
                       GLESv3
                       android
                       )

