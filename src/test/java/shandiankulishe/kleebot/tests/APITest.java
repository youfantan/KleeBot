package shandiankulishe.kleebot.tests;

import com.google.gson.*;
import glous.kleebot.features.builtin.MCPacket;
import glous.kleebot.features.builtin.VariableInt;
import glous.kleebot.features.genshin.GenshinAPI;
import glous.kleebot.features.minecraft.PingAPI;
import org.junit.jupiter.api.Test;
import glous.kleebot.features.bilibili.BilibiliAPI;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class APITest {
    @Test
    public void testPingAPI() throws IOException {
        PingAPI api=new PingAPI("mc.hypixel.net",25565);
        api.ping();
    }
    @Test
    public void testBilibiliAPI() throws IOException {
        BilibiliAPI api=new BilibiliAPI();
        System.out.println(api.getShortUrl("https://space.bilibili.com/36700106/video"));
        System.out.println(api.getShortUrl("https://www.bilibili.com/video/BV1e44y147nb"));
        System.out.println(api.getShortUrl("https://t.bilibili.com/599449444500820929"));
        System.out.println(api.getShortUrl("https://kleebot.glous.xyz"));
    }
}
