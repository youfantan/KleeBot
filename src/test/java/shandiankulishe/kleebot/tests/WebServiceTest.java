package shandiankulishe.kleebot.tests;

import com.google.gson.Gson;
import glous.kleebot.utils.FileUtils;
import org.junit.jupiter.api.Test;
import glous.kleebot.BotConfig;
import glous.kleebot.GlobalVars;
import glous.kleebot.KleeBot;
import glous.kleebot.async.AsyncTaskQueue;
import glous.kleebot.async.Timer;
import glous.kleebot.http.HttpServer;
import glous.kleebot.http.services.HardwareInfoService;
import glous.kleebot.services.api.HardwareInfo;
import org.xbill.DNS.*;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Scanner;

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

    public static void main(String[] args) throws IOException {
        Scanner sc=new Scanner(System.in);
        while (sc.hasNext()){
            System.out.println(sc.nextLine());
        }
    }
}
