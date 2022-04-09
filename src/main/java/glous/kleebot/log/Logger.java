package glous.kleebot.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class Logger {
    private static final StringWriter strOut=new StringWriter();
    private static File logFile;
    public static void init(){
        Date date=new Date();
        String fileTimeFormat="yyyy-MM-dd-HH.mm.ss";
        SimpleDateFormat formatter=new SimpleDateFormat(fileTimeFormat);
        String fileTime=formatter.format(date);
        File logDir=new File("logs");
        if (!logDir.exists()){
            boolean ret=logDir.mkdir();
            if (!ret){
                System.out.println("logs目录创建失败，请检查权限。");
                System.exit(-1);
            }
        }
        logFile=new File("logs"+File.separatorChar+fileTime+".log.gz");
        if (!logFile.exists()){
            try {
                boolean ret=logFile.createNewFile();
                if (!ret){
                    System.out.println("log文件创建失败，请检查权限。");
                    System.exit(-1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writer=new BufferedWriter(strOut);
    }
    public static Logger getLogger(Class clz){
        return new Logger(clz);
    }
    private Class clz;
    private static BufferedWriter writer;
    private Logger(Class clz) {
        this.clz=clz;
    }
    private String getFormattedMessage(String level,String message){
        String consoleTimeFormat="yyyy-MM-dd-HH:mm:ss";
        Date date=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat(consoleTimeFormat);
        String consoleTime=formatter.format(date);
        String threadName=Thread.currentThread().getName();
        String className=clz.getSimpleName();
        return """
                %s [%s/%s/%s]: %s
                """.formatted(consoleTime,level,className,threadName,message);
    }
    public void info(String message){
        String formattedMessage=getFormattedMessage("INFO",message);
        System.out.print(formattedMessage);
        try {
            writer.write(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void trace(String message){
        String formattedMessage=getFormattedMessage("TRACE",message);
        try {
            writer.write(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void debug(String message){
        String formattedMessage=getFormattedMessage("DEBUG",message);
        System.out.print(formattedMessage);
        try {
            writer.write(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void error(String message){
        String formattedMessage=getFormattedMessage("ERROR",message);
        System.out.print(formattedMessage);
        try {
            writer.write(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void fatal(String message){
        String formattedMessage=getFormattedMessage("FATAL",message);
        System.out.print(formattedMessage);
        try {
            writer.write(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void stop(){
        try {
            writer.flush();
            writer.close();
            strOut.flush();
            strOut.close();
            GZIPOutputStream gout=new GZIPOutputStream(new FileOutputStream(logFile));
            gout.write(strOut.toString().getBytes(StandardCharsets.UTF_8));
            gout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
