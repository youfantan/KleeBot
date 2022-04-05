package shandiankulishe.kleebot.tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import shandiankulishe.kleebot.BotConfig;
import shandiankulishe.kleebot.GlobalVars;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.async.AsyncTaskQueue;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.http.HttpServer;
import shandiankulishe.kleebot.http.services.HardwareInfoService;
import shandiankulishe.kleebot.services.api.HardwareInfo;
import shandiankulishe.kleebot.utils.StringUtils;

import java.io.*;

public class WebServiceTest {
    @Test
    public void TestHttpServer() throws InterruptedException, FileNotFoundException {
        System.load(new File("bin\\libHardwareInfo.dll").getAbsolutePath());
        AsyncTaskQueue queue=new AsyncTaskQueue(32);
        GlobalVars.setQueue(queue);
        new Thread(queue::start).start();
        Timer.start();
        HardwareInfo.init();
        KleeBot.config=new Gson().fromJson(new FileReader("kleebot.json"), BotConfig.class);
        HttpServer server=new HttpServer(KleeBot.config.getServicePort());
        server.register("BotStatus.zip");
        server.register("Base.zip");
        server.register("/services/getHardwareInfo",new HardwareInfoService());
        new Thread(()->{
            server.start();
        }).start();
        Thread.sleep(10000);
        System.exit(0);
    }
}
