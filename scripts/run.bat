@echo off
java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true -Dmirai.no-desktop -cp .;KleeBot.jar;./dependencies/* glous.kleebot.KleeBot
pause