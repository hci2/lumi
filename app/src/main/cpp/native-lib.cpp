#include <jni.h>
#include <string>



extern "C"
JNIEXPORT jstring JNICALL
Java_at_ac_univie_lumi_view_MapActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
