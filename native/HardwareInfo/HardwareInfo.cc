#include "shandiankulishe_kleebot_services_api_HardwareInfo.h"
#ifdef WIN32
#include <intrin.h>
#include <windows.h>
#endif
#ifdef __linux__
#include "unistd.h"
#include "sys/sysinfo.h"
#include "sys/config.h"
#endif
JNIEXPORT jstring JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getCpuModel
        (JNIEnv *env, jobject){
#ifdef WIN32
    int cpuInfo[4] = {-1};
    char cpu_type[32]={0};
    char cpu_name[32]={0};
    char cpu_freq[32]={0};
    char cpu_full_name[64]={0};
    __cpuid(cpuInfo, 0x80000003);
    memcpy(cpu_type, cpuInfo, sizeof(cpuInfo));
    __cpuid(cpuInfo, 0x80000002);
    memcpy(cpu_name, cpuInfo, sizeof(cpuInfo));
    __cpuid(cpuInfo, 0x80000004);
    memcpy(cpu_freq, cpuInfo, sizeof(cpuInfo));
    strcat(cpu_full_name,cpu_name);
    strcat(cpu_full_name,cpu_type);
    strcat(cpu_full_name,cpu_freq);
    return env->NewStringUTF(cpu_full_name);
#endif
#ifdef __linux__

#endif
}

/*
 * Class:     shandiankulishe_kleebot_services_api_HardwareInfo
 * Method:    getCpuClockCycle
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getCpuClockCycle
        (JNIEnv *, jobject){
    unsigned __int64 t1,t2;
    t1 = __rdtsc();
    Sleep(1000);
    t2 = __rdtsc();
    unsigned __int64 freq=(t2 - t1) / 1000000;
    return freq;
}

/*
 * Class:     shandiankulishe_kleebot_services_api_HardwareInfo
 * Method:    getCpuUsage
 * Signature: ()D
 */

__int64 Filetime2Int64(const FILETIME &ftime)
{
    LARGE_INTEGER li;
    li.LowPart = ftime.dwLowDateTime;
    li.HighPart = ftime.dwHighDateTime;
    return li.QuadPart;
}

__int64 CompareFileTime2(const FILETIME &preTime, const FILETIME &nowTime)
{
    return Filetime2Int64(nowTime) - Filetime2Int64(preTime);
}
JNIEXPORT jdouble JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getCpuUsage
        (JNIEnv *env, jobject){
    FILETIME preIdleTime;
    FILETIME preKernelTime;
    FILETIME preUserTime;
    GetSystemTimes(&preIdleTime, &preKernelTime, &preUserTime);
    Sleep(1000);
    FILETIME idleTime;
    FILETIME kernelTime;
    FILETIME userTime;
    GetSystemTimes(&idleTime, &kernelTime, &userTime);
    auto idle = CompareFileTime2(preIdleTime, idleTime);
    auto kernel = CompareFileTime2(preKernelTime, kernelTime);
    auto user = CompareFileTime2(preUserTime, userTime);
    if (kernel + user == 0)
        return 0;
    return 1.0 * (kernel + user - idle) / (kernel + user);

}

/*
 * Class:     shandiankulishe_kleebot_services_api_HardwareInfo
 * Method:    getCpuAvailableCores
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jint JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getCpuAvailableCores
        (JNIEnv *env, jobject){
#ifdef WIN32
    SYSTEM_INFO info;
    GetSystemInfo(&info);
    return info.dwNumberOfProcessors;
#endif
#ifdef __linux__
#endif
}

/*
 * Class:     shandiankulishe_kleebot_services_api_HardwareInfo
 * Method:    getTotalMemory
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jlong JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getTotalMemory
        (JNIEnv *, jobject){
#ifdef WIN32
    MEMORYSTATUS ms;
    GlobalMemoryStatus(&ms);
    return ms.dwTotalPhys;
#endif
#ifdef __linux__
#endif
}

/*
 * Class:     shandiankulishe_kleebot_services_api_HardwareInfo
 * Method:    getAvailableMemory
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jlong JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getAvailableMemory
        (JNIEnv *, jobject){
#ifdef WIN32
    MEMORYSTATUS ms;
    GlobalMemoryStatus(&ms);
    return ms.dwAvailPhys;
#endif
#ifdef __linux__
#endif
}

/*
 * Class:     shandiankulishe_kleebot_services_api_HardwareInfo
 * Method:    getProcessID
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_shandiankulishe_kleebot_services_api_HardwareInfo_getProcessID
        (JNIEnv *, jobject){
#ifdef WIN32
    return GetCurrentProcessId();
#endif
#ifdef __linux__
#endif
}