package glous.kleebot.services.impl;

import glous.kleebot.http.ChromeInstance;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
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
            byte[] bytes=ChromeInstance.getScreenShot("http://localhost:%s/BotStatus/".formatted(KleeBot.config.getServicePort()),1000,0);
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
                            KleeBot %s
                            Web??????: http://%s/BotStatus/
                            Github??????: https://www.github.com/youfantan/Kleebot
                            KleeBot??????: https://kleebot.glous.xyz
                            OpenCV??????: %s
                            Tesseract-OCR??????: %s
                            Java??????: %s
                            ????????????: %s
                            ????????????: %d ?????????????????????
                            """.formatted(KleeBot.GET_VERSION(),KleeBot.ip,"4.5.5","4.1",System.getProperty("java.version"),System.getProperty("os.name"),names.size())
            );
            for (int i = 0; i < names.size(); i++) {
                builder.append("\t"+i+": "+names.get(i)+"\n");
            }
            double memUse= (double) (HardwareInfo.getInstance().getTotalMemoryInfo()-HardwareInfo.getInstance().getAvailableMemoryInfo())/HardwareInfo.getInstance().getTotalMemoryInfo();
            builder.append(
                    """
                            CPU: %s
                            CPU????????????: %d Mhz
                            CPU?????????: %.2f %%
                            ???????????????: %.2f %%
                            ??????????????????: %s
                            ????????????: %d ???
                            ???pixiv?????????: %d ??????
                            ???bilbili?????????: %d ??????
                            ??????ID: %d
                            ???????????????:
                            """.formatted(HardwareInfo.getInstance().getCpuModelInfo(), HardwareInfo.getInstance().getCpuClockCycleInfo(), HardwareInfo.getInstance().getCpuUsageInfo() * 100, memUse * 100, formatter.format(date), running, pixiv_delay, bilibili_delay, HardwareInfo.getInstance().getProcessIDInfo())
            );
            String[] services= ServiceRegistry.getAllRegisteredServices();
            HashMap<String, Service> enabledServiceMap=ServiceRegistry.getEnabledServiceMap();
            for (String service:services){
                builder.append("\t").append(service).append(enabledServiceMap.containsKey(service)?"(??????)":"(?????????)").append("\n");
            }
            event.getGroup().sendMessage(builder.build());
        } else {
            return false;
        }
        return true;
    }
}
