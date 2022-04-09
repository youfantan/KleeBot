package glous.kleebot.services;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import glous.kleebot.KleeBot;
import glous.kleebot.log.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ServiceRegistry {
    private static final HashMap<String,Service> serviceMap=new HashMap<>();
    private static final HashMap<String,Service> enabledServiceMap=new HashMap<>();
    private static final Logger logger=Logger.getLogger(ServiceRegistry.class);
    @SuppressWarnings("unchecked")
    public static void register(Class service) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException {
        serviceMap.put(service.getName(),(Service)service.getDeclaredConstructor().newInstance());
    }
    public static void init() throws IOException {
        for (Service service:serviceMap.values()){
            if (KleeBot.configurationInstance.contains(service.getClass().getName())&&KleeBot.configurationInstance.getBoolean(service.getClass().getName())){
                enabledServiceMap.put(service.getClass().getName(),service);
            }
            service.initialize();
        }
    }
    public static String[] getAllEnabledService(){
        return enabledServiceMap.keySet().toArray(new String[0]);
    }
    public static String[] getAllRegisteredServices(){
        return serviceMap.keySet().toArray(new String[0]);
    }

    public static HashMap<String, Service> getEnabledServiceMap() {
        return enabledServiceMap;
    }

    public static HashMap<String, Service> getServiceMap() {
        return serviceMap;
    }

    public static void processGroupMessage(GroupMessageEvent rawMessage){
        for (Service service:enabledServiceMap.values()){
            if (service instanceof GroupService){
                if (((GroupService) service).process(rawMessage)){
                    KleeBot.queue.addTask(()->{
                        logger.trace("Post task %s to execute".formatted(service.getServiceName()));
                        try {
                            boolean success=((GroupService) service).execute(rawMessage);
                            if (!success){
                                MessageChainBuilder builder=new MessageChainBuilder();
                                builder.append(new At(rawMessage.getSender().getId()));
                                builder.append(" 未定义用法。请发送KleeBot Help以获取详细信息");
                                rawMessage.getGroup().sendMessage(builder.build());
                            }
                        } catch (Exception e) {
                            StringWriter writer=new StringWriter();
                            PrintWriter pw=new PrintWriter(writer);
                            e.printStackTrace(pw);
                            MessageChainBuilder builder=new MessageChainBuilder();
                            builder.append(new At(rawMessage.getSender().getId()));
                            builder.append(" 运行异常。错误信息：");
                            builder.append(writer.toString());
                            builder.append("\n若非用法错误，请上报ISSUES到https://www.github.com/youfantan/KleeBot/issues");
                            rawMessage.getGroup().sendMessage(builder.build());
                            try {
                                writer.close();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                            pw.close();
                        }
                    },service.getServiceName());
                }
            }
        }
    }
    public static void stop(){
        for (Service service:serviceMap.values()){
            service.stop();
        }
    }
}
