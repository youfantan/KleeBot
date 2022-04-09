package glous.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import glous.kleebot.KleeBot;
import glous.kleebot.async.Task;
import glous.kleebot.async.Timer;
import glous.kleebot.services.GroupService;
import glous.kleebot.services.Service;
import glous.kleebot.services.ServiceRegistry;
import glous.kleebot.services.api.HardwareInfo;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CoreService extends GroupService {
    @Override
    public void initialize(){
        getDelay();
        Timer.registerScheduledTask(new Task(CoreService::getDelay,CoreService.class.getName()),Timer.SECOND*10);
    }
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " status");
    }
    private static long pixiv_delay=0;
    private static long bilibili_delay=0;

    public static long getBilibili_delay() {
        return bilibili_delay;
    }

    public static long getPixiv_delay() {
        return pixiv_delay;
    }

    private static void getDelay(){
        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(KleeBot.config.getProxyHost(),KleeBot.config.getProxyPort()));
        try {
            Socket skt=new Socket(proxy);
            long start=System.currentTimeMillis();
            skt.connect(new InetSocketAddress("www.pixiv.net",80));
            long end=System.currentTimeMillis();
            skt.close();
            pixiv_delay=end-start;
            skt=new Socket();
            start=System.currentTimeMillis();
            skt.connect(new InetSocketAddress("www.bilibili.com",80));
            end=System.currentTimeMillis();
            skt.close();
            bilibili_delay=end-start;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean execute(GroupMessageEvent event) throws IOException, InterruptedException {
        if (event.getMessage().serializeToMiraiCode().equals(new At(KleeBot.config.getBotAccount())+" status img")){
            ChromeOptions options=new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=2160,1380");
            ChromeDriver chrome=new ChromeDriver(options);
            chrome.manage().window().maximize();
            chrome.get("http://localhost:%s/BotStatus/".formatted(KleeBot.config.getServicePort()));
            chrome.executeScript("document.body.style.zoom='1.5'");
            Thread.sleep(1000);
            byte[] bytes=chrome.getScreenshotAs(OutputType.BYTES);
            chrome.close();
            MessageChainBuilder builder=new MessageChainBuilder();
            builder.append(new At(event.getSender().getId()));
            ExternalResource resource=ExternalResource.create(bytes);
            Image image=event.getSubject().uploadImage(resource);
            builder.append(image);
            event.getGroup().sendMessage(builder.build());
            resource.close();
        } else if (event.getMessage().serializeToMiraiCode().equals(new At(KleeBot.config.getBotAccount())+" status")){
            MessageChainBuilder builder=new MessageChainBuilder();
            //get current time
            long currentTime=System.currentTimeMillis();
            long running=currentTime-KleeBot.startTime;
            running=running/1000;
            Date date=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            List<String> names=KleeBot.queue.getAllRunningTasksName();
            builder.append(
                    """
                            KleeBot初号机
                            Web页面: http://%s/BotStatus/
                            版本: %s
                            Github仓库: https://www.github.com/youfantan/Kleebot
                            KleeBot主页: https://kleebot.glous.xyz
                            OpenCV版本: %s
                            Tesseract-OCR版本: %s
                            Java版本: %s
                            运行系统: %s
                            任务队列: %d 个任务正在运行
                            """.formatted(KleeBot.ip,KleeBot.GET_VERSION(),"4.5.5","4.1",System.getProperty("java.version"),System.getProperty("os.name"),names.size())
            );
            for (int i = 0; i < names.size(); i++) {
                builder.append("\t"+i+": "+names.get(i)+"\n");
            }
            double memUse= (double) (HardwareInfo.getInstance().getTotalMemoryInfo()-HardwareInfo.getInstance().getAvailableMemoryInfo())/HardwareInfo.getInstance().getTotalMemoryInfo();
            builder.append(
                    """
                            CPU: %s
                            CPU时钟频率: %d Mhz
                            CPU占用率: %.2f %%
                            内存占用率: %.2f %%
                            校准延迟时间: %s
                            已经运行: %d 秒
                            与pixiv的延迟: %d 毫秒
                            与bilbili的延迟: %d 毫秒
                            进程ID: %d
                            已安装服务:
                            """.formatted(HardwareInfo.getInstance().getCpuModelInfo(), HardwareInfo.getInstance().getCpuClockCycleInfo(), HardwareInfo.getInstance().getCpuUsageInfo() * 100, memUse * 100, formatter.format(date), running, pixiv_delay, bilibili_delay, HardwareInfo.getInstance().getProcessIDInfo())
            );
            String[] services= ServiceRegistry.getAllRegisteredServices();
            HashMap<String, Service> enabledServiceMap=ServiceRegistry.getEnabledServiceMap();
            for (String service:services){
                builder.append("\t").append(service).append(enabledServiceMap.containsKey(service)?"(启用)":"(未启用)").append("\n");
            }
            event.getGroup().sendMessage(builder.build());
        } else {
            return false;
        }
        return true;
    }
}
