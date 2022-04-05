package shandiankulishe.kleebot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import shandiankulishe.kleebot.async.AsyncTaskQueue;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.cache.CacheFactory;
import shandiankulishe.kleebot.commands.CommandRegistry;
import shandiankulishe.kleebot.commands.impl.StopCommand;
import shandiankulishe.kleebot.config.Configuration;
import shandiankulishe.kleebot.http.ChromeInstance;
import shandiankulishe.kleebot.http.HttpServer;
import shandiankulishe.kleebot.http.services.GetWhisperService;
import shandiankulishe.kleebot.http.services.HardwareInfoService;
import shandiankulishe.kleebot.http.services.StoreWhisperService;
import shandiankulishe.kleebot.log.Logger;
import shandiankulishe.kleebot.services.ServiceRegistry;
import shandiankulishe.kleebot.services.api.HardwareInfo;
import shandiankulishe.kleebot.services.impl.*;
import shandiankulishe.kleebot.utils.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public static Configuration configurationInstance;
    public static Bot botInstance;
    public static final String ip=(getLocalhost()==null?getLocalhost():"127.0.0.1");
    private static String getLocalhost(){
        try {

            byte[] bytes= FileUtils.download(References.IP_SEARCH_URL);
            String utfStr=new String(bytes,StandardCharsets.UTF_8);
            String jsonStr=utfStr.substring(utfStr.indexOf("{"),utfStr.length()-1);
            JsonObject object= JsonParser.parseString(jsonStr).getAsJsonObject();
            String cip=object.get("cip").getAsString();
            return cip;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static HttpServer getServerInstance(){
        return serverInstance;
    }
    public static String GET_VERSION(){
        return "KleeBot dev@(dev-000000) build at 2022-4-4";
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
    public static void main(String[] args) throws IOException {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty("webdriver.chrome.driver",new File("bin/chromedriver.exe").getAbsolutePath());
        //load all jni and native libraries
        System.load(new File("bin/libHardwareInfo.dll").getAbsolutePath());
        Logger.init();
        logger= Logger.getLogger(KleeBot.class);
        logger.info("Logger已完成初始化");
        logger.info("当前服务器外网IP: "+ip);
        File file=new File("kleebot.configuration");
        int mode=0;
        if (!file.exists()){
            mode=1;
            InputStream stream=Objects.requireNonNull(KleeBot.class.getResourceAsStream("/default.configuration"));
            BufferedReader reader=new BufferedReader(new InputStreamReader(stream,StandardCharsets.UTF_8));
            String line;
            BufferedWriter writer=new BufferedWriter(new FileWriter("kleebot.configuration"));
            while ((line=reader.readLine())!=null){
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
            reader.close();
        }
        Configuration configuration=new Configuration();
        KleeBot.configurationInstance=configuration;
        configuration.load(new File("kleebot.configuration"));
        config=configuration.serializeToClass(BotConfig.class);
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
            ServiceRegistry.register(PixivService.class);
            ServiceRegistry.register(BilibiliService.class);
            ServiceRegistry.register(GenshinPlayerService.class);
            ServiceRegistry.register(GenshinAbyssService.class);
            ServiceRegistry.register(MCWikiSearchService.class);
            ServiceRegistry.register(AutoPostService.class);
            ServiceRegistry.register(GithubSpeedService.class);
            String[] services=ServiceRegistry.getAllRegisteredServices();
            Map<String,Object> configMap=new HashMap<>();
            if (mode==1){
                for (String service:
                        services) {
                    configuration.setValue(service,true);
                }
                configuration.saveToFile();
                logger.info("配置文件生成完毕，请修改配置文件以继续。");
                System.exit(0);
            }
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
            ChromeInstance.initialize();
            logger.trace("Chrome Driver初始化完成");
            logger.trace("所有服务已完成初始化");
            Bot bot = BotFactory.INSTANCE.newBot(config.getBotAccount(), config.getBotPassword(), botConfiguration -> {
                botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
                botConfiguration.fileBasedDeviceInfo();
                botConfiguration.noBotLog();
                botConfiguration.noNetworkLog();
            });
            bot.login();
            botInstance=bot;
            logger.info("Bot登录完成");
            CommandRegistry.register("StopCommand",new StopCommand());
            CommandRegistry.start();
            bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
                logger.info("GROUPID:"+event.getGroup().getId()+" ID:"+event.getSender().getId()+"|NAME:"+event.getSenderName()+" :"+ event.getMessage());
                ServiceRegistry.processGroupMessage(event);
            });
        } catch (Exception e){
            queue.stop();
            e.printStackTrace();
        }
    }
}
