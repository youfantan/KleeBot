package glous.kleebot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import glous.kleebot.async.AsyncTaskQueue;
import glous.kleebot.async.Timer;
import glous.kleebot.cache.CacheFactory;
import glous.kleebot.commands.CommandRegistry;
import glous.kleebot.commands.impl.BroadcastCommand;
import glous.kleebot.commands.impl.StopCommand;
import glous.kleebot.config.Configuration;
import glous.kleebot.http.ChromeInstance;
import glous.kleebot.http.HttpServer;
import glous.kleebot.http.services.GetWhisperService;
import glous.kleebot.http.services.HardwareInfoService;
import glous.kleebot.http.services.StoreWhisperService;
import glous.kleebot.plugin.Plugin;
import glous.kleebot.services.ServiceRegistry;
import glous.kleebot.services.api.HardwareInfo;
import glous.kleebot.utils.FileUtils;
import glous.kleebot.services.impl.*;
import glous.kleebot.utils.ZipUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import glous.kleebot.log.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class KleeBot {
    public static final long startTime=System.currentTimeMillis();
    private static Logger logger;
    public static BotConfig config;
    public static AsyncTaskQueue queue;
    private static boolean RUNNING_FLAG=true;
    public static boolean getRunningFlag(){
        return RUNNING_FLAG;
    }
    public static void stop(){
        RUNNING_FLAG=false;
    }
    static HttpServer serverInstance;
    public static Configuration configurationInstance;
    public static Bot botInstance;
    public static final String ip=(getLocalhost()==null?"127.0.0.1":getLocalhost());
    public static List<Plugin> plugins=new ArrayList<>();
    public static boolean ENBALE_DEBUG=null == null ? false : true;
    static String getLocalhost(){
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
        return "%s(%s)\n%s".formatted(FileUtils.readStreamString(KleeBot.class.getResourceAsStream("/RELEASE")),FileUtils.readStreamString(KleeBot.class.getResourceAsStream("/SIGN")),FileUtils.readStreamString(KleeBot.class.getResourceAsStream("/BUILD")));
    }
    public static int GET_OS(){
        String OSName=System.getProperty("os.name");
        if (OSName.contains("Windows")){
            return 0;
        } else if (OSName.contains("Linux")){
            return 1;
        } else {
            return -1;
        }
    }
    public static void main(String[] args) throws IOException {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        File bin=new File("bin");
        if (!bin.exists()){
            bin.mkdir();
        }
        if (GET_OS()==0){
            File libHardwareInfoNative=new File("bin/libHardwareInfo.dll");
            if (!libHardwareInfoNative.exists()){
                FileUtils.writeFile("bin/libHardwareInfo.dll", Objects.requireNonNull(FileUtils.readStreamBytes(KleeBot.class.getResourceAsStream("/libHardwareInfo.dll"))));
            }
            System.load(libHardwareInfoNative.getAbsolutePath());
            File chromeDriverNative=new File("bin/chromedriver.exe");
            if (!chromeDriverNative.exists()){
                System.out.println("Missing chromeDriver.Start to download binaries from server.");
                FileUtils.writeFile("bin/chromedriver_win32.zip",FileUtils.download("https://registry.npmmirror.com/-/binary/chromedriver/99.0.4844.51/chromedriver_win32.zip"));
                ZipUtils.extractZipFile("bin/chromedriver_win32.zip","bin");
            }
            System.setProperty("webdriver.chrome.driver",new File("bin/chromedriver.exe").getAbsolutePath());
        } else if (GET_OS()==1){
            File libHardwareInfoNative=new File("bin/libHardwareInfo.so");
            if (!libHardwareInfoNative.exists()){
                FileUtils.writeFile("bin/libHardwareInfo.so", Objects.requireNonNull(FileUtils.readStreamBytes(KleeBot.class.getResourceAsStream("/libHardwareInfo.dll"))));
            }
            System.load(libHardwareInfoNative.getAbsolutePath());
            File chromeDriverNative=new File("bin/chromedriver");
            if (!chromeDriverNative.exists()){
                System.out.println("Missing chromeDriver.Start to download binaries from server.");
                FileUtils.writeFile("bin/chromedriver_linux64.zip",FileUtils.download("https://registry.npmmirror.com/-/binary/chromedriver/99.0.4844.51/chromedriver_linux64.zip"));
                ZipUtils.extractZipFile("bin/chromedriver_linux64.zip","bin");
            }
            System.setProperty("webdriver.chrome.driver",new File("bin/chromedriver").getAbsolutePath());
        } else{
            System.out.println("NOT SUPPORTED SYSTEM");
            System.exit(-1);
        }

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
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("kleebot.configuration"),StandardCharsets.UTF_8));
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
        System.out.println("配置文件: "+ configuration);
        config=configuration.serializeToClass(BotConfig.class);
        //change debug status
        KleeBot.ENBALE_DEBUG=configuration.getBoolean("EnableDebug");
        File cacheDir=new File(KleeBot.config.getCacheDir());
        if (!cacheDir.exists()){
            cacheDir.mkdir();
        }
        queue=new AsyncTaskQueue(config.getQueueSize());
        new Thread(()->{
            queue.start();
        }).start();
        GlobalVars.setQueue(queue);
        HardwareInfo.init();
        //start web http server
        HttpServer server=new HttpServer(KleeBot.config.getServicePort());
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
        logger.trace("Http服务器初始化完成");
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
            ServiceRegistry.register(SyncService.class);
            ServiceRegistry.register(MCPingService.class);
            //start to load plugins
            File pluginDir=new File("plugins");
            if (!pluginDir.exists()){
                pluginDir.mkdir();
            }
            File[] plugins=pluginDir.listFiles();
            for (File plugin :
                    plugins) {
                logger.info("开始加载插件 "+plugin.getName());
                JarFile jar=new JarFile(plugin);
                JarEntry entry=jar.getJarEntry("plugin.configuration");
                BufferedReader reader=new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));
                String line;
                StringBuilder builder=new StringBuilder();
                while ((line=reader.readLine())!=null){
                    builder.append(line).append("\n");
                }
                String configurationText=builder.toString();
                Configuration pluginConfig=new Configuration();
                pluginConfig.load(configurationText);
                String MainClass=pluginConfig.getString("MainClass");
                String Description=pluginConfig.getString("Description");
                String Version=pluginConfig.getString("Version");
                String Home=pluginConfig.getString("Home");
                logger.debug("\n插件主类: %s\n插件描述: %s\n插件版本: %s\n插件主页: %s".formatted(MainClass,Description,Version,Home));
                URLClassLoader loader=new URLClassLoader(new URL[]{plugin.toURI().toURL()});
                Class clz=loader.loadClass(MainClass);
                Plugin pluginInstance=(Plugin) clz.getConstructor().newInstance();
                pluginInstance.onInit(configurationInstance,config,serverInstance);
                KleeBot.plugins.add(pluginInstance);
                reader.close();
                jar.close();
            }
            logger.trace("插件加载完成");
            String[] services=ServiceRegistry.getAllRegisteredServices();
            if (mode==1){
                for (String service:
                        services) {
                    configuration.setValue(service,true,"是否启用 %s 服务".formatted(service));
                }
                configuration.saveToFile();
                logger.info("配置文件生成完毕，请修改配置文件以继续。");
                System.exit(0);
            }
            ServiceRegistry.init();
            System.out.println(Arrays.toString(ServiceRegistry.getAllEnabledService()));
            ChromeInstance.initialize();
            logger.trace("Chrome Driver初始化完成");
            Timer.init();
            Timer.start();
            logger.trace("Timer初始化完成");
            String[] enabledServices=ServiceRegistry.getAllEnabledService();
            for (String serviceName :
                    enabledServices) {
                logger.trace("已启用的服务: " + serviceName);
            }
            logger.trace("所有服务已完成初始化");
            Bot bot = BotFactory.INSTANCE.newBot(config.getBotAccount(), config.getBotPassword(), botConfiguration -> {
                botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
                botConfiguration.fileBasedDeviceInfo();
                botConfiguration.noBotLog();
                botConfiguration.noNetworkLog();
            });
            bot.login();
            botInstance=bot;
            logger.info("Bot登录完成");
            bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
                if (!config.isSilentMode()){
                    logger.info("GROUPID:"+event.getGroup().getId()+" ID:"+event.getSender().getId()+"|NAME:"+event.getSenderName()+" :"+ event.getMessage());
                }
                ServiceRegistry.processGroupMessage(event);
            });
            CommandRegistry.register("StopCommand",new StopCommand());
            CommandRegistry.register("BroadcastCommand",new BroadcastCommand());
            CommandRegistry.start();
        } catch (Exception e){
            queue.stop();
            e.printStackTrace();
        }
    }
}
