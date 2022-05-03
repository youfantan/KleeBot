package shandiankulishe.kleebot.tests;

import glous.kleebot.config.ConfigNode;
import org.junit.jupiter.api.Test;
import glous.kleebot.BotConfig;
import glous.kleebot.config.Configuration;

import java.io.IOException;
import java.util.Map;

public class ConfigTest {
    public static Configuration getTestConfig() throws IOException {
        String defaultConfig= """
#   Copyright 2022 shandiankulishe@gmail.com
ProxyHost : 127.0.0.1 # 设置代理IP
ProxyPort : 7890 # 设置代理端口
QueueSize : 64 # 设置最大并行任务数
BotAccount : 3479060093 # 设置Bot账号
BotPassword : Andy20061108 #设置Bot密码
CacheDir : cache #设置缓存文件夹
ServicePort : 80 #设置Http服务开放端口
CookieFile : cookie.dat #设置米游社Cookie文件
ResourcePackDir : pages #设置Http静态服务资源包路径
ResourcePackFileDir : resourcePacks #设置Http静态服务资源包释放路径
SilentMode : true #设置是否输出群内消息
EnableDebug : true #设置是否启用调试
                """;
        Configuration configuration=new Configuration();
        configuration.load(defaultConfig);
        return configuration;
    }
    @Test
    public void testConfig() throws IOException {

        Configuration configuration=getTestConfig();
        Map<String, ConfigNode> map=configuration.getConfigNodes();
        for (Map.Entry<String, ConfigNode> entry :
                map.entrySet()) {
            System.out.printf("Key: %s Value: %s ValueType: %s Comment: %s\n",entry.getValue().getKey(),entry.getValue().getValue(),entry.getValue().getValue().getClass().getName(),entry.getValue().getComment());
        }
        System.out.println((String) configuration.get("ProxyHost"));
        System.out.println(configuration.getString("ProxyHost"));
        BotConfig config=configuration.serializeToClass(BotConfig.class);
        config.setQueueSize(128);
        configuration.mergeClass(config);
        System.out.println(configuration.save());
        System.out.println(configuration.getBoolean("SilentMode"));
        System.out.println(config.isSilentMode());
    }
}
