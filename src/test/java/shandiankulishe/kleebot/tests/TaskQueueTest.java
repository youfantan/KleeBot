package shandiankulishe.kleebot.tests;

import org.junit.jupiter.api.Test;
import shandiankulishe.kleebot.async.AsyncTaskQueue;
import shandiankulishe.kleebot.services.api.HardwareInfo;
import java.util.List;

public class TaskQueueTest {
    synchronized void add(){
        flag++;
    }
    int flag=0;
    @Test
    public void testAsyncTaskQueue() {
        AsyncTaskQueue queue=new AsyncTaskQueue(64);
        for (int i=0;i<100;i++){
            queue.addTask(()->{
                add();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"taskTest");
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(flag);
        System.out.println("Current running tasks:");
        List<String> names=queue.getAllRunningTasksName();
        for (int i = 0; i < names.size(); i++) {
            System.out.println(" "+i+". "+names.get(i));
        }
        System.out.println("ready to interrupt");
        queue.stop();
    }
    public static void TestGetInfo(){
        String info=
                """
                CPU Model: %s
                CPU Clock Cycle: %d MHZ
                CPU Usage: %.2f %%
                CPU Cores: %d
                Memory Total: %d
                Memory Available: %d
                ProcessID: %d
                """.formatted(HardwareInfo.getInstance().getCpuModelInfo(),HardwareInfo.getInstance().getCpuClockCycleInfo(),HardwareInfo.getInstance().getCpuUsageInfo()*100,HardwareInfo.getInstance().getCpuAvailableCoresInfo(),HardwareInfo.getInstance().getTotalMemoryInfo(),HardwareInfo.getInstance().getAvailableMemoryInfo(),HardwareInfo.getInstance().getProcessIDInfo());
        System.out.println(info);
    }
}
