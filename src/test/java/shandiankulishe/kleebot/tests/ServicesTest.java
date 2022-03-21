package shandiankulishe.kleebot.tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import shandiankulishe.kleebot.features.builtin.AbyssInfo;
import shandiankulishe.kleebot.features.builtin.PlayerInfo;
import shandiankulishe.kleebot.features.builtin.Role;
import shandiankulishe.kleebot.features.builtin.World;
import shandiankulishe.kleebot.features.genshin.GenshinPlayerAPI;
import shandiankulishe.kleebot.utils.FileUtils;
import shandiankulishe.kleebot.utils.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class ServicesTest {
    @Test
    public void testBilibili(){
        String avCode="https://www.bilibili.com/video/BV1y341157fh?spm_id_from=333.851.b_7265636f6d6d656e64.3";
        String a=avCode.substring(avCode.indexOf("bilibili.com"));
        a=a.substring(a.indexOf("BV"));
        //a=a.substring(a.indexOf("/"));
        //a=a.substring(a.indexOf("/"));
        a=a.substring(0,12);
        System.out.println(a);
        avCode="https://www.bilibili.com/video/av170001/auwgo129070912";
        String b=avCode.substring(avCode.indexOf("av"));
        String c=StringUtils.findDigit(b);
        System.out.println(c);
        String d="";
        String e=null;
        System.out.println(d.isEmpty());
        System.out.println(e.isEmpty());
    }
    @Test
    public void testGetPlayerInfo() throws IOException {
        String uid="128035175";
        String cookie = FileUtils.readFile("cookie.dat",StandardCharsets.UTF_8);
        GenshinPlayerAPI api=new GenshinPlayerAPI(cookie);
        PlayerInfo info=api.getPlayerInfo(GenshinPlayerAPI.GENSHIN_CHINA,uid);
        ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("player_info.dat"));
        os.writeObject(info);
        os.flush();
        os.close();
    }
    @Test
    public void testGetAbyssInfo() throws IOException {
        String uid="128035175";
        String cookie = FileUtils.readFile("cookie.dat",StandardCharsets.UTF_8);
        GenshinPlayerAPI api=new GenshinPlayerAPI(cookie);
        AbyssInfo info=api.getSpiralAbyssInfo(GenshinPlayerAPI.GENSHIN_CHINA,uid);
        ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("abyss_info.dat"));
        os.writeObject(info);
        os.flush();
        os.close();
    }
    @Test
    public void generatePlayerInfoImage() throws IOException {
        String uid="128035175";
        String cookie = FileUtils.readFile("cookie.dat",StandardCharsets.UTF_8);
        GenshinPlayerAPI api=new GenshinPlayerAPI(cookie);
        PlayerInfo info=api.getPlayerInfo(GenshinPlayerAPI.GENSHIN_CHINA,uid);
    }
}
