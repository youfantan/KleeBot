package shandiankulishe.kleebot.tests;

import org.junit.jupiter.api.Test;
import glous.kleebot.features.bilibili.BilibiliAPI;

import java.io.IOException;

public class APITest {
    @Test
    public void testBilibiliAPI() throws IOException {
        BilibiliAPI api=new BilibiliAPI();
        System.out.println(api.getShortUrl("https://space.bilibili.com/36700106/video"));
        System.out.println(api.getShortUrl("https://www.bilibili.com/video/BV1e44y147nb"));
        System.out.println(api.getShortUrl("https://t.bilibili.com/599449444500820929"));
        System.out.println(api.getShortUrl("https://kleebot.glous.xyz"));
    }
}
