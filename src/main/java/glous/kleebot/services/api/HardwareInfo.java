package glous.kleebot.services.api;

import com.google.gson.stream.JsonWriter;
import glous.kleebot.services.impl.CoreService;
import glous.kleebot.GlobalVars;
import glous.kleebot.KleeBot;
import glous.kleebot.async.Task;
import glous.kleebot.async.Timer;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class HardwareInfo {
    private static HardwareInfo instance=new HardwareInfo();

    public static HardwareInfo getInstance() {
        return instance;
    }
    public static void init(){
        updateInfo();
        Timer.registerScheduledTask(new Task(HardwareInfo::updateInfo,HardwareInfo.class.getName()),Timer.SECOND);
    }
    private native String getCpuModel();
    private native double getCpuUsage();
    private native int getCpuAvailableCores();
    private native long getCpuClockCycle();
    private native long getTotalMemory();
    private native long getAvailableMemory();
    private native long getProcessID();

    public String getCpuModelInfo() {
        return cpuModelInfo;
    }

    public double getCpuUsageInfo() {
        return cpuUsageInfo;
    }

    public int getCpuAvailableCoresInfo() {
        return cpuAvailableCoresInfo;
    }

    public long getTotalMemoryInfo() {
        return totalMemoryInfo;
    }

    public long getAvailableMemoryInfo() {
        return availableMemoryInfo;
    }

    public long getProcessIDInfo() {
        return processIDInfo;
    }

    public long getCpuClockCycleInfo() {
        return cpuClockCycleInfo;
    }

    public String getJsonFormattedInfo(){
        StringWriter out=new StringWriter();
        JsonWriter writer=new JsonWriter(out);
        try {
            writer.beginObject();
            writer.name("CpuModelInfo").value(cpuModelInfo);
            BigDecimal d1=BigDecimal.valueOf(cpuUsageInfo);
            d1=d1.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            writer.name("CpuUsageInfo").value(d1.toString());
            writer.name("CpuAvailableCoresInfo").value(String.valueOf(cpuAvailableCoresInfo));
            writer.name("CpuClockCycleInfo").value(cpuClockCycleInfo);
            BigDecimal d2=BigDecimal.valueOf(totalMemoryInfo);
            String d4=d2.divide(BigDecimal.valueOf(1048576),2,RoundingMode.HALF_UP).toString();
            BigDecimal d3=BigDecimal.valueOf(availableMemoryInfo);
            String d5=d3.divide(BigDecimal.valueOf(1048576),2,RoundingMode.HALF_UP).toString();
            BigDecimal d6;
            d6=d3.divide(d2,4,RoundingMode.HALF_UP);
            String d7=d6.multiply(BigDecimal.valueOf(100)).setScale(2,RoundingMode.HALF_UP).toString();
            BigDecimal d8=d2.subtract(d3);
            BigDecimal d9;
            d9=d8.divide(d2,4,RoundingMode.HALF_UP);
            String d10=d8.divide(BigDecimal.valueOf(1048576),2,RoundingMode.HALF_UP).toString();
            String d11=d9.multiply(BigDecimal.valueOf(100)).setScale(2,RoundingMode.HALF_UP).toString();
            writer.name("TotalMemoryInfo").value(d4);
            writer.name("AvailableMemoryInfo").value(d5);
            writer.name("UsedMemoryInfo").value(d10);
            writer.name("FreePercentInfo").value(d7);
            writer.name("UsedPercentInfo").value(d11);
            writer.name("ProcessIDInfo").value(String.valueOf(processIDInfo));
            writer.name("OS").value(System.getProperty("os.name"));
            writer.name("JreVersion").value(System.getProperty("java.version"));
            writer.name("OpenCVVersion").value("4.5.5");
            writer.name("TesseractVersion").value("4.1");
            writer.name("PixivDelay").value(String.valueOf(CoreService.getPixiv_delay()));
            writer.name("BiliBiliDelay").value(String.valueOf(CoreService.getBilibili_delay()));
            writer.name("KleeBotVersion").value(KleeBot.GET_VERSION());
            writer.name("Tasks").beginArray();
            List<String> names= GlobalVars.getQueue().getAllRunningTasksName();
            for (int i = 0; i < names.size(); i++) {
                writer.value(names.get(i));
            }
            writer.endArray();
            writer.endObject();
            writer.flush();
            writer.close();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String cpuModelInfo;
    private double cpuUsageInfo;
    private long cpuClockCycleInfo;
    private int cpuAvailableCoresInfo;
    private long totalMemoryInfo;
    private long availableMemoryInfo;
    private long processIDInfo;
    public static void updateInfo() {
        try {
            if (KleeBot.GET_OS()==0){
                instance.cpuModelInfo= instance.getCpuModel();
                instance.cpuUsageInfo= instance.getCpuUsage();
                instance.cpuAvailableCoresInfo= instance.getCpuAvailableCores();
                instance.cpuClockCycleInfo= instance.getCpuClockCycle();
                instance.totalMemoryInfo= instance.getTotalMemory();
                instance.availableMemoryInfo= instance.getAvailableMemory();
                instance.processIDInfo= instance.getProcessID();
            } else if (KleeBot.GET_OS()==1) {
                SystemInfo si=new SystemInfo();
                HardwareAbstractionLayer layer=si.getHardware();
                CentralProcessor processor = layer.getProcessor();
                long[] prevTicks = processor.getSystemCpuLoadTicks();
                Thread.sleep(1000);
                long[] ticks = processor.getSystemCpuLoadTicks();
                long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
                long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
                long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
                long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
                long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
                long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
                long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
                long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
                long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
                int count=processor.getLogicalProcessorCount();
                double cpuUsage=(1.0 - (idle * 1.0 / totalCpu));
                String model=processor.getProcessorIdentifier().getModel();
                GlobalMemory memory=si.getHardware().getMemory();
                long totalMemory=memory.getTotal();
                long availableMemory=memory.getAvailable();
                instance.cpuAvailableCoresInfo=count;
                instance.cpuModelInfo=model;
                instance.cpuUsageInfo=cpuUsage;
                instance.totalMemoryInfo=totalMemory;
                instance.availableMemoryInfo=availableMemory;
                instance.cpuClockCycleInfo=instance.getCpuClockCycle();
                instance.processIDInfo= instance.getProcessID();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
