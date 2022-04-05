package shandiankulishe.kleebot.services.api;

import com.google.gson.stream.JsonWriter;
import shandiankulishe.kleebot.GlobalVars;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.async.Task;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.services.impl.CoreService;

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
    private native long getCpuClockCycle();
    private native int getCpuAvailableCores();
    private native long getTotalMemory();
    private native long getAvailableMemory();
    private native long getProcessID();

    public String getCpuModelInfo() {
        return cpuModelInfo;
    }

    public double getCpuUsageInfo() {
        return cpuUsageInfo;
    }

    public long getCpuClockCycleInfo() {
        return cpuClockCycleInfo;
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
    public String getJsonFormattedInfo(){
        StringWriter out=new StringWriter();
        JsonWriter writer=new JsonWriter(out);
        try {
            writer.beginObject();
            writer.name("CpuModelInfo").value(cpuModelInfo);
            BigDecimal d1=BigDecimal.valueOf(cpuUsageInfo);
            d1=d1.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            writer.name("CpuUsageInfo").value(d1.toString());
            writer.name("CpuClockCycleInfo").value(String.valueOf(cpuClockCycleInfo));
            writer.name("CpuAvailableCoresInfo").value(String.valueOf(cpuAvailableCoresInfo));
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
    public static void updateInfo(){
        instance.cpuModelInfo= instance.getCpuModel();
        instance.cpuUsageInfo= instance.getCpuUsage();
        instance.cpuClockCycleInfo= instance.getCpuClockCycle();
        instance.cpuAvailableCoresInfo= instance.getCpuAvailableCores();
        instance.totalMemoryInfo= instance.getTotalMemory();
        instance.availableMemoryInfo= instance.getAvailableMemory();
        instance.processIDInfo= instance.getProcessID();
    }
}
