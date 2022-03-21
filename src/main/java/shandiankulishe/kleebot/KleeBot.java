package shandiankulishe.kleebot;
import com.google.gson.Gson;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoggerAdapters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;
import shandiankulishe.kleebot.async.AsyncTaskQueue;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.cache.CacheFactory;
import shandiankulishe.kleebot.commands.CommandRegistry;
import shandiankulishe.kleebot.commands.impl.StopCommand;
import shandiankulishe.kleebot.http.HttpServer;
import shandiankulishe.kleebot.http.services.GetWhisperService;
import shandiankulishe.kleebot.http.services.HardwareInfoService;
import shandiankulishe.kleebot.http.services.StoreWhisperService;
import shandiankulishe.kleebot.services.ServiceRegistry;
import shandiankulishe.kleebot.services.api.HardwareInfo;
import shandiankulishe.kleebot.services.impl.*;
import shandiankulishe.kleebot.utils.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class KleeBot {
    public static final long startTime=System.currentTimeMillis();
    private static Logger logger;
    public static BotConfig config;
    public static AsyncTaskQueue queue;
    private static boolean RUNNING_FLAG=true;
    public static  boolean getRunningFlag(){
        return RUNNING_FLAG;
    }
    public static void stop(){
        RUNNING_FLAG=false;
    }
    private static HttpServer serverInstance;
    public static HttpServer getServerInstance(){
        return serverInstance;
    }
    public static String GET_VERSION(){
        return "KleeBot dev@(dev-000000) build at 2022-3-13";
    }
    public static int GET_OS(){
        String OSName=System.getProperty("os.name");
        if (OSName.contains("windows")){
            return 0;
        } else if (OSName.contains("linux")){
            return 1;
        } else {
            return -1;
        }
    }
    public static void main(String[] args) throws URISyntaxException {
        //load all jni and native libraries
        System.load(new File("bin/zlib1.dll").getAbsolutePath());
        System.load(new File("bin/lzma.dll").getAbsolutePath());
        System.load(new File("bin/jpeg62.dll").getAbsolutePath());
        System.load(new File("bin/tiff.dll").getAbsolutePath());
        System.load(new File("bin/webp.dll").getAbsolutePath());
        System.load(new File("bin/gif.dll").getAbsolutePath());
        System.load(new File("bin/libpng16.dll").getAbsolutePath());
        System.load(new File("bin/libwebpmux.dll").getAbsolutePath());
        System.load(new File("bin/opencv_world455.dll").getAbsolutePath());
        System.load(new File("bin/leptonica-1.81.1.dll").getAbsolutePath());
        System.load(new File("bin/tesseract41.dll").getAbsolutePath());
        System.load(new File("bin/TesseractForASOCR.dll").getAbsolutePath());
        System.load(new File("bin/libHardwareInfo.dll").getAbsolutePath());
        LoggerContext context=(LoggerContext)LogManager.getContext(false);
        context.setConfigLocation(ClassLoader.getSystemResource("kleebot.log4j2.config.xml").toURI());
        logger= LogManager.getLogger(KleeBot.class);
        logger.trace("Logger已完成初始化");
        config=new Gson().fromJson(FileUtils.readFile("kleebot.json",StandardCharsets.UTF_8),BotConfig.class);
        File cacheDir=new File(KleeBot.config.getCacheDir());
        if (!cacheDir.exists()){
            cacheDir.mkdir();
        }
        queue=new AsyncTaskQueue(config.getQueueSize());
        new Thread(()->{
            queue.start();
        }).start();
        GlobalVars.setQueue(queue);
        Timer.start();
        try {
            CacheFactory.deserializeCaches();
            //register all services
            ServiceRegistry.register(CoreService.class);
            ServiceRegistry.register(HelpService.class);
            ServiceRegistry.register(GenshinArtifactScoreService.class);
            ServiceRegistry.register(PixivService.class);
            ServiceRegistry.register(BilibiliService.class);
            ServiceRegistry.register(GenshinPlayerService.class);
            ServiceRegistry.init();
            HardwareInfo.init();
            //register all scheduled tasks
            //start web http server
            shandiankulishe.kleebot.http.HttpServer server=new HttpServer(KleeBot.config.getServicePort());
            File resourcePackDir=new File(KleeBot.config.getResourcePackFileDir());
            if (resourcePackDir.exists()){
                File[] resourcePacks=resourcePackDir.listFiles();
                if (resourcePacks!=null){
                    for (File f :
                            resourcePacks) {
                        server.register(f.getAbsolutePath());
                    }
                }
            }
            server.register("/services/getHardwareInfo",new HardwareInfoService());
            server.register("/services/storeWhisper",new StoreWhisperService());
            server.register("/services/getWhisper",new GetWhisperService());
            new Thread(()->{
                server.start();
            }).start();
            KleeBot.serverInstance=server;
            logger.trace("所有服务已完成初始化");
            LoggerAdapters.useLog4j2();
            Bot bot = BotFactory.INSTANCE.newBot(config.getBotAccount(), config.getBotPassword(), botConfiguration -> {
                botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
                botConfiguration.fileBasedDeviceInfo();
                botConfiguration.noBotLog();
                botConfiguration.noNetworkLog();
            });
            bot.login();
            logger.info("Bot登录完成");
            CommandRegistry.register("StopCommand",new StopCommand());
            CommandRegistry.start();
            bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
                logger.info("GROUPID:"+event.getGroup().getId()+" ID:"+event.getSender().getId()+"|NAME:"+event.getSenderName()+" :"+ event.getMessage());
                ServiceRegistry.processGroupMessage(event);
            });
        } catch (Exception e){
            queue.stop();
            logger.error(e);
        }
    }
}
