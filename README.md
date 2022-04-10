# KleeBot
## 简介
KleeBot是一个使用Java及C++混合编写的，适用于QQ的机器人。  
机器人提供了如下功能:
+ Pixiv榜单查询/IllustId查询
+ Bilibili视频链接提取与解析
+ 原神角色信息查询(使用miHoYo Takumi API)
+ 原神深渊信息查询(使用miHoYo Takumi API)
+ Minecraft Wiki查询
+ 游戏版本更新推送(现支持Minecraft，采用MojiraAPI与VersionManifest.json)  
同时，机器人附加了许多如加密图片(Whisper)、Bot状态查询等功能。
即将支持的功能:
+ 原神圣遗物评分(采用OpenCV+TesseractOCR)
+ 支持加载插件/热更新插件  
机器人以Apache2.0协议开源。

### 轻量
所有组件都采用插件式设计，拓展及更新更加方便。每一个功能都被封装成API类，服务与功能分离。  
### 高效率
机器人采用多线程队列的方案来处理消息，不使用时队列休眠，根据分配线程数可以异步执行多个任务(默认32个线程，可在配置文件中更改)。即将面世的圣遗物评分功能将采用分布式设计。  
### 稳定
所有的功能均已通过单元测试。自项目立项至今已测试60余天。所有服务均在JVM平台运行，C++部分非常少。

## 构建与下载
KleeBot使用Github Action作为其CI。项目使用Gradle与CMake构建工具，在Windows平台使用OpenJDK与MingwW64编译，在Linux平台使用OpenJDK与GCC编译。
我们暂未开放下载通道，预计将在2022年6月末上传release。请前往[KleeBot交流群](https://jq.qq.com/?_wv=1027&k=IaAvtYDB)获取内测版本。

## 支持我们
点击[此链接](https://afdian.net/@shandiankulishe)前往爱发电捐助我(所有捐助费用将用作升级服务器)。

## 文档
文档未完成。

## 链接
点击[此链接](https://jq.qq.com/?_wv=1027&k=IaAvtYDB)前往KleeBot交流群。
点击[此链接](https://kleebot.glous.xyz)前往KleeBot简介。