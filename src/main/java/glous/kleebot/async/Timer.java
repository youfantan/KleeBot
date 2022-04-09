package glous.kleebot.async;

import glous.kleebot.KleeBot;
import glous.kleebot.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Timer {
    private static final HashMap<Task,Long> setInterval = new HashMap<>();
    private static final HashMap<Task,Long> runningInterval=new HashMap<>();
    private static final List<Task> tasks=new ArrayList<>();
    private static final Logger logger=Logger.getLogger(Timer.class);
    public static final long SECOND=1000L;
    public static final long MINUTE=60000L;
    public static final long HOUR=3600000L;
    public static final long DAY=86400000L;
    public static final long NO_LIMIT =-1L;
    public static void registerScheduledTask(Task task,long interval){
        setInterval.put(task,interval);
        runningInterval.put(task,0L);
        tasks.add(task);
    }
    public static void init() throws Exception {
        //init start run timer
        logger.info("开始运行初始化任务");
        for (Task value : tasks) {
            logger.info("任务: %s 开始执行".formatted(value.getFullName()));
            value.getFunc().execute();
        }
    }
    public static void start(){
        new Thread(()->{
            long start;
            long last=System.currentTimeMillis();
            while (KleeBot.getRunningFlag()){
                start=System.currentTimeMillis();
                for (Task task :
                        setInterval.keySet()) {
                    long set=setInterval.get(task);
                    long running=runningInterval.get(task);
                    if (running>set){
                        try {
                            task.getFunc().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runningInterval.put(task,0L);
                    } else {
                        runningInterval.put(task,running+(start-last));
                    }
                }
                last=System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
