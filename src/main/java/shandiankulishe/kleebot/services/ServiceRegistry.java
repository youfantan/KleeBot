package shandiankulishe.kleebot.services;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import okhttp3.internal.concurrent.TaskQueue;
import shandiankulishe.kleebot.KleeBot;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ServiceRegistry {
    private static final HashMap<String,Service> serviceMap=new HashMap<>();
    @SuppressWarnings("unchecked")
    public static void register(Class service) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Service svc=(Service)service.getDeclaredConstructor().newInstance();
        serviceMap.put(service.getName(),(Service)service.getDeclaredConstructor().newInstance());
    }
    public static void init(){
        for (Service service:serviceMap.values()){
            service.initialize();
        }
    }
    public static void processGroupMessage(GroupMessageEvent rawMessage){
        for (Service service:serviceMap.values()){
            if (service instanceof GroupService){
                if (((GroupService) service).process(rawMessage)){
                    KleeBot.queue.addTask(()->{
                        ((GroupService) service).execute(rawMessage);
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
