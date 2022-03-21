cmake_minimum_required(VERSION 3.16.3)
project(HardwareInfo)

set(CMAKE_CXX_STANDARD 14)
include_directories(include)
add_library(HardwareInfo SHARED HardwareInfo.cc)
