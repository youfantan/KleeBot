package shandiankulishe.kleebot.async;

import shandiankulishe.kleebot.KleeBot;
import java.util.HashMap;

public class Timer {
    private static final HashMap<Task,Long> setInterval = new HashMap<>();
    private static final HashMap<Task,Long> runningInterval=new HashMap<>();
    public static final long SECOND=1000L;
    public static final long MINUTE=60000L;
    public static final long HOUR=3600000L;
    public static final long DAY=86400000L;
    public static final long NO_LIMIT =-1L;
    private static AsyncTaskQueue queue;
    public static void registerScheduledTask(Task task,long interval){
        setInterval.put(task,interval);
        runningInterval.put(task,0L);
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
                        task.getFunc().execute();
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
