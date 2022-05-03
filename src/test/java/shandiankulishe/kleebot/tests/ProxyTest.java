package shandiankulishe.kleebot.tests;

import com.google.gson.Gson;
import glous.kleebot.BotConfig;
import glous.kleebot.KleeBot;
import glous.kleebot.config.Configuration;
import glous.kleebot.features.builtin.VariableInt;
import glous.kleebot.http.proxy.GeneralTCPProxy;
import glous.kleebot.log.Logger;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ProxyTest {

    @Test
    public void testVarInt() throws IOException {
        VariableInt vi=new VariableInt(724125);
        byte[] vBytes1=vi.getBytes();
        int i=vi.readBytes(vBytes1);
        byte[] vBytes2=vi.getBytes();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner=new Scanner(System.in);

        Configuration configuration=ConfigTest.getTestConfig();
        KleeBot.config=configuration.serializeToClass(BotConfig.class);
        Logger.init();
        GeneralTCPProxy proxy=new GeneralTCPProxy(2333,25565);
        new Thread(()->{
            while (scanner.hasNext()){
                String line=scanner.nextLine();
                if (line.equals("stop")){
                    System.out.println("stopped");
                    try {
                        proxy.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    scanner.close();
                    Logger.stop();
                    System.exit(0);
                    break;
                }
            }
        }).start();
        proxy.start();
    }
}
