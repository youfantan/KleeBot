package shandiankulishe.kleebot.tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import shandiankulishe.kleebot.BotConfig;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.cache.Cache;
import shandiankulishe.kleebot.cache.CacheFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CacheTest {
    @Test
    public void testCache() throws IOException {
        KleeBot.config=new Gson().fromJson(new FileReader("kleebot.json"), BotConfig.class);
        CacheFactory.restoreCache("Test1","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.restoreCache("Test2","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.restoreCache("Test3","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.restoreCache("Test4","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.restoreCache("Test5","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.restoreCache("Test6","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.restoreCache("Test7","Test2".getBytes(StandardCharsets.UTF_8), Timer.NO_LIMIT);
        CacheFactory.serializeCaches();
        CacheFactory.deserializeCaches();
    }
}
