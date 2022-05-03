package glous.kleebot.http.proxy;

import glous.kleebot.async.AsyncTaskQueue;
import glous.kleebot.async.BaseFunction;
import glous.kleebot.async.Task;
import glous.kleebot.features.genshin.GenshinAPI;
import glous.kleebot.log.Logger;
import glous.kleebot.utils.FileUtils;
import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GeneralTCPProxy {
    private static final Logger logger=Logger.getLogger(GeneralTCPProxy.class);
    private boolean stop=false;
    private AsyncTaskQueue queue=new AsyncTaskQueue(64);
    public GeneralTCPProxy(int openPort, int forwardPort) {
        this.openPort = openPort;
        this.forwardPort = forwardPort;
    }
    private int openPort;
    private int forwardPort;
    public void start() throws IOException {
        queue.asyncStart();
        ServerSocket socket=new ServerSocket(openPort);
        while (!stop){
            Socket from=socket.accept();
            Socket to=new Socket("127.0.0.1",forwardPort);
            System.out.printf("Connected from %s to %s\n",from.getInetAddress().getHostAddress(),to.getInetAddress().getHostAddress());
            BufferedInputStream fromIn=new BufferedInputStream(from.getInputStream());
            BufferedOutputStream fromOut=new BufferedOutputStream(from.getOutputStream());
            BufferedInputStream toIn=new BufferedInputStream(to.getInputStream());
            BufferedOutputStream toOut=new BufferedOutputStream(to.getOutputStream());
            ByteArrayOutputStream fromRecorder=new ByteArrayOutputStream();
            ByteArrayOutputStream toRecorder=new ByteArrayOutputStream();
            queue.addTask(()->{
                logger.info("proxy start");
                byte[] buffer=new byte[1024];
                int bytesRead;
                while ((bytesRead=fromIn.read(buffer))!=-1){
                    logger.info("%s".formatted(GenshinAPI.byteArrayToHex(buffer)));
                    toOut.write(buffer,0,bytesRead);
                    toOut.flush();
                    toRecorder.write(buffer,0,bytesRead);
                    toRecorder.flush();
                }
                toIn.close();
                fromOut.close();
                fromRecorder.close();
                FileUtils.writeFile(String.valueOf(from.hashCode()),fromRecorder.toByteArray());
            },"TCP-PROXY-CLIENT-POSTER"+from.hashCode());
            queue.addTask(()->{
                logger.info("proxy start");
                byte[] buffer=new byte[1024];
                int bytesRead;
                while ((bytesRead=toIn.read(buffer))!=-1){
                    logger.info("%s".formatted(GenshinAPI.byteArrayToHex(buffer)));
                    fromOut.write(buffer,0,bytesRead);
                    fromOut.flush();
                    fromRecorder.write(buffer,0,bytesRead);
                    fromRecorder.flush();
                }
                fromIn.close();
                toOut.close();
                fromRecorder.close();
                FileUtils.writeFile(String.valueOf(to.hashCode()),toRecorder.toByteArray());
            },"TCP-PROXY-SERVER-POSTER"+to.hashCode());
        }
    }
    public void stop() throws IOException {
        this.stop=true;
        queue.stop();
    }
}
