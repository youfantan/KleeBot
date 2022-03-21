package shandiankulishe.kleebot.tests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import kotlinx.serialization.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import shandiankulishe.kleebot.BotConfig;
import shandiankulishe.kleebot.GlobalVars;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.References;
import shandiankulishe.kleebot.async.AsyncTaskQueue;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.features.pixiv.PixivAPI;
import shandiankulishe.kleebot.http.HttpClient;
import shandiankulishe.kleebot.http.HttpServer;
import shandiankulishe.kleebot.http.IWebService;
import shandiankulishe.kleebot.http.services.HardwareInfoService;
import shandiankulishe.kleebot.services.api.HardwareInfo;
import shandiankulishe.kleebot.utils.FileUtils;
import shandiankulishe.kleebot.utils.ZipUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class WebServiceTest {
    @Test
    public void TestHttpServer() throws InterruptedException, FileNotFoundException {
        System.load(new File("bin\\libHardwareInfo.dll").getAbsolutePath());
        AsyncTaskQueue queue=new AsyncTaskQueue(32);
        GlobalVars.setQueue(queue);
        new Thread(queue::start).start();
        Timer.start();
        HardwareInfo.init();
        KleeBot.config=new Gson().fromJson(new FileReader("kleebot.json"), BotConfig.class);
        HttpServer server=new HttpServer(KleeBot.config.getServicePort());
        server.register("BotStatus.zip");
        server.register("Base.zip");
        server.register("/services/getHardwareInfo",new HardwareInfoService());
        HashMap<String,String> dataMap=new HashMap<>();
        server.register("/services/storeWhisper", new IWebService() {
            @Override
            public boolean doGET(HttpClient client) {
                return false;
            }

            @Override
            public boolean doPOST(HttpClient client) throws IOException{
                String key= UUID.randomUUID().toString();
                client.setHeader("Access-Control-Allow-Origin","*");
                String encryptedData=client.getBody();
                StringWriter out=new StringWriter();
                JsonWriter writer=new JsonWriter(out);
                writer.beginObject();
                if (encryptedData.length()>1024*1024*20){
                    writer.name("status").value("error");
                    writer.name("message").value("max size limit of 10MB");
                    writer.endArray();
                    writer.flush();
                    writer.close();
                    client.writeResponseBody(out.toString());
                    client.finish();
                    return true;
                }
                dataMap.put(key,encryptedData);
                writer.name("status").value("ok");
                writer.name("message").value("successfully stored");
                writer.name("uuid").value(key);
                writer.endObject();
                writer.flush();
                writer.close();
                client.writeResponseBody(out.toString());
                client.finish();
                return true;
            }

            @Override
            public void init() {

            }

            @Override
            public void stop() {

            }
        });
        server.register("/services/getWhisper", new IWebService() {
            @Override
            public boolean doGET(HttpClient client) throws IOException {
                client.setHeader("Access-Control-Allow-Origin","*");
                String requestKey=client.getRequestPath();
                StringWriter out=new StringWriter();
                JsonWriter writer=new JsonWriter(out);
                writer.beginObject();
                if (!requestKey.contains(":")){
                    writer.name("status").value("error");
                    writer.name("message").value("request method not right");
                    writer.endObject();
                    writer.flush();
                    writer.close();
                    client.writeResponseBody(out.toString());
                    client.finish();
                    return true;
                }
                requestKey=requestKey.substring(requestKey.indexOf(":")+1);
                if (!dataMap.containsKey(requestKey)){
                    writer.name("status").value("error");
                    writer.name("message").value("whisper not found");
                    writer.endObject();
                    writer.flush();
                    writer.close();
                    client.writeResponseBody(out.toString());
                    client.finish();
                    return true;
                }
                writer.name("status").value("ok");
                writer.name("message").value("successfully got data");
                writer.name("body").value(dataMap.get(requestKey));
                writer.endObject();
                writer.flush();
                writer.close();
                client.writeResponseBody(out.toString());
                client.finish();
                return true;
            }

            @Override
            public boolean doPOST(HttpClient client) throws IOException {
                return false;
            }

            @Override
            public void init() {

            }

            @Override
            public void stop() {

            }
        });
        server.start();

    }
    @Test
    public void webDriverTest(){
        System.setProperty("webdriver.edge.driver",new File("bin\\msedgedriver.exe").getAbsolutePath());
        EdgeDriver driver=new EdgeDriver();
        driver.get("https://ntp.msn.cn/edge/ntp?locale=zh-CN&title=新建标签页&dsp=1&sp=必应");
    }
    @Test
    public void TestZip(){
        ZipUtils.extractZipFile("BotStatus.zip","web");
    }
    @Test
    public void testChangeName(){
        File traindata=new File("TrainData");
        File[] datas=traindata.listFiles();
        for (int i = 0; i < datas.length; i++) {
            File f=new File("Train\\"+i+".png");
            FileUtils.writeFile(f.getAbsolutePath(),FileUtils.readFile(datas[i].getAbsolutePath()));
        }
    }
}
