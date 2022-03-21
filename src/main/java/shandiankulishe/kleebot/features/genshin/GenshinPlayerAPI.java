package shandiankulishe.kleebot.features.genshin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import shandiankulishe.kleebot.References;
import shandiankulishe.kleebot.features.builtin.AbyssInfo;
import shandiankulishe.kleebot.features.builtin.PlayerInfo;
import shandiankulishe.kleebot.features.builtin.Role;
import shandiankulishe.kleebot.features.builtin.World;
import shandiankulishe.kleebot.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

public class GenshinPlayerAPI {
    private String cookie;
    public GenshinPlayerAPI(String cookie){
        this.cookie=cookie;
    }
    public static final String GENSHIN_CHINA="cn_gf01";
    public static final String GENSHIN_CHINA_BILIBILI="cn_qd01";
    public static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };
    public static String byteArrayToHex(byte[] src){
        int l=src.length;
        char[] str=new char[l*2];
        int k=0;
        for (int i = 0; i < l; i++) {
            byte buffer=src[i];
            str[k++]=hexDigits[buffer >>> 4 & 0xf];
            str[k++]=hexDigits[buffer & 0xf];
        }
        return new String(str);
    }
    public static String sumMD5(String src){
        try {
            MessageDigest digest=MessageDigest.getInstance("md5");
            digest.update(src.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public static final String salt="xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs";//defined in mihoyo bbs v2.11.0
    public static String sumDS(String url){
        String n=salt;
        String t=String.valueOf(System.currentTimeMillis()/1000);
        String r=getRandomString(6);
        String q=url.substring(url.indexOf("?")+1);
        StringBuilder param = new StringBuilder();
        List<String> paramList = new ArrayList<>(Arrays.asList(q.split("&")));
        Collections.sort(paramList);
        paramList.forEach(i->{
            param.append(i+"&");
        });
        param.deleteCharAt(param.length()-1);
        String c=sumMD5("salt="+n+"&t="+t+"&r="+r+"&b="+"&q="+param.toString());
        return t+","+r+","+c;
    }
    public AbyssInfo getSpiralAbyssInfo(String server,String uid){
        String url=References.MIHOYO_ABYSS_URL.formatted("1",server,uid);
        byte[] rawRet = FileUtils.download(url,null,getHeaders(url));
        AbyssInfo abyssInfo=new AbyssInfo();
        abyssInfo.setStatus(0);
        if (rawRet==null){
            abyssInfo.setStatus(-1);
            return abyssInfo;
        }
        String info=new String(rawRet,StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(info).getAsJsonObject();
        abyssInfo.setErrorMsg(info);
        if (object.get("retcode").getAsInt()!=0){
            abyssInfo.setStatus(-2);
            return abyssInfo;
        }
        object=object.get("data").getAsJsonObject();
        abyssInfo.setIs_unlock(object.get("is_unlock").getAsBoolean());
        abyssInfo.setMax_floor(object.get("max_floor").getAsString());
        abyssInfo.setTotal_star(object.get("total_star").getAsInt());
        abyssInfo.setTotal_battle_times(object.get("total_battle_times").getAsInt());
        abyssInfo.setTotal_win_times(object.get("total_win_times").getAsInt());
        JsonObject damage_rank=object.get("damage_rank").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject normal_skill_rank=object.get("normal_skill_rank").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject energy_skill_rank=object.get("energy_skill_rank").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject defeat_rank=object.get("defeat_rank").getAsJsonArray().get(0).getAsJsonObject();
        abyssInfo.setMax_damaged(damage_rank.get("value").getAsLong());
        abyssInfo.setMax_defeat(defeat_rank.get("value").getAsInt());
        abyssInfo.setMax_skill(normal_skill_rank.get("value").getAsInt());
        abyssInfo.setMax_energy_skill(energy_skill_rank.get("value").getAsInt());
        String max_damaged_image_url=damage_rank.get("avatar_icon").getAsString();
        String max_defeat_image_url=defeat_rank.get("avatar_icon").getAsString();
        String max_normal_skill_image_url=normal_skill_rank.get("avatar_icon").getAsString();
        String max_energy_skill_url=energy_skill_rank.get("avatar_icon").getAsString();
        abyssInfo.setMax_damaged_avatar(Base64.getEncoder().encodeToString(FileUtils.download(max_damaged_image_url)));
        abyssInfo.setMax_defeat_avatar(Base64.getEncoder().encodeToString(FileUtils.download(max_defeat_image_url)));
        abyssInfo.setMax_skill_avatar(Base64.getEncoder().encodeToString(FileUtils.download(max_normal_skill_image_url)));
        abyssInfo.setMax_energy_skill_avatar(Base64.getEncoder().encodeToString(FileUtils.download(max_energy_skill_url)));
        return abyssInfo;
    }
    public PlayerInfo getPlayerInfo(String server, String uid){
        String url=References.MIHOYO_GAME_RECORD_URL.formatted(uid, server);
        byte[] rawRet = FileUtils.download(url,null,getHeaders(url));
        if (rawRet == null) {
            return null;
        }
        String info = new String(rawRet, StandardCharsets.UTF_8);
        JsonObject object = JsonParser.parseString(info).getAsJsonObject();
        PlayerInfo playerInfo=new PlayerInfo();
        playerInfo.setErrorMsg(info);
        playerInfo.setStatus(0);
        if (object.get("retcode").getAsInt()!=0){
            playerInfo.setStatus(-1);
            return playerInfo;
        }
        object=object.get("data").getAsJsonObject();
        JsonArray worlds_obj=object.get("world_explorations").getAsJsonArray();
        World[] worlds=new World[worlds_obj.size()];
        for (int i=0;i<worlds_obj.size();i++){
            JsonObject world_obj=worlds_obj.get(i).getAsJsonObject();
            World world=new World(world_obj.get("name").getAsString(),world_obj.get("exploration_percentage").getAsInt());
            String icon_url=world_obj.get("icon").getAsString();
            world.setIcon(Base64.getEncoder().encodeToString(FileUtils.download(icon_url)));
            worlds[i]=world;
        }
        JsonArray avatars=object.get("avatars").getAsJsonArray();
        Role[] roles=new Role[avatars.size()];
        for (int i=0;i<avatars.size();i++){
            JsonObject role_obj=avatars.get(i).getAsJsonObject();
            String imageUrl=role_obj.get("image").getAsString();
            byte[] avatar_image=FileUtils.download(imageUrl);
            Role role=new Role(role_obj.get("name").getAsString(),role_obj.get("level").getAsInt(),Base64.getEncoder().encodeToString(avatar_image),role_obj.get("fetter").getAsInt());
            roles[i]=role;
        }
        JsonObject stats=object.get("stats").getAsJsonObject();
        playerInfo.setActive_day_number(stats.get("active_day_number").getAsInt());
        playerInfo.setAchievement_number(stats.get("achievement_number").getAsInt());
        playerInfo.setAnemoculus_number(stats.get("anemoculus_number").getAsInt());
        playerInfo.setAvatar_number(stats.get("avatar_number").getAsInt());
        playerInfo.setDomain_number(stats.get("domain_number").getAsInt());
        playerInfo.setElectroculus_number(stats.get("electroculus_number").getAsInt());
        playerInfo.setCommon_chest_number(stats.get("common_chest_number").getAsInt());
        playerInfo.setGeoculus_number(stats.get("geoculus_number").getAsInt());
        playerInfo.setExquisite_chest_number(stats.get("exquisite_chest_number").getAsInt());
        playerInfo.setLuxurious_chest_number(stats.get("luxurious_chest_number").getAsInt());
        playerInfo.setSpiral_abyss(stats.get("spiral_abyss").getAsString());
        playerInfo.setMagic_chest_number(stats.get("magic_chest_number").getAsInt());
        playerInfo.setPrecious_chest_number(stats.get("precious_chest_number").getAsInt());
        playerInfo.setRoles(roles);
        playerInfo.setWorlds(worlds);
        playerInfo.setUid(uid);
        return playerInfo;
    }
    private HashMap<String,String> getHeaders(String url){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-rpc-client_type", "5");
        headers.put("x-rpc-app_version", "2.11.1");
        headers.put("DS", sumDS(url));
        headers.put("Cookie", cookie);
        headers.put("Origin", "https://webstatic.mihoyo.com");
        headers.put("Referer", "https://webstatic.mihoyo.com/");
        headers.put("X-Requested-With", "com.mihoyo.hyperion");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/2.11.1");
        return headers;
    }

    public String generatePlayerInfoImage(PlayerInfo info){
        try {
            int width=7680;
            int height=4320;
            BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            Font hywh=Font.createFont(Font.TRUETYPE_FONT,Objects.requireNonNull(this.getClass().getResourceAsStream("/HYWenHei-85W.ttf")));
            Graphics2D g=image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            BufferedImage background= ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/background.jpg")));
            g.drawImage(background,0,0,width,height,null);
            g.setPaint(Color.WHITE);
            g.setFont(hywh.deriveFont(150f));
            g.drawString("UID %s 的统计数据".formatted(info.getUid()),70,200);
            g.setFont(hywh.deriveFont(120f));
            g.drawString("统计: ",250,500);
            g.setFont(hywh.deriveFont(100f));
            g.drawString("角色数量: "+info.getAvatar_number(),200,700);
            g.drawString("深渊最深抵达: "+info.getSpiral_abyss(),200,850);
            g.drawString("获得成就数: "+info.getAchievement_number(),200,1000);
            g.drawString("风神瞳收集: "+info.getAnemoculus_number(),200,1150);
            g.drawString("岩神瞳收集: "+info.getGeoculus_number(),200,1300);
            g.drawString("雷神瞳收集: "+info.getElectroculus_number(),200,1450);
            g.drawString("普通宝箱收集: "+info.getCommon_chest_number(),200,1600);
            g.drawString("稀有宝箱收集: "+info.getExquisite_chest_number(),200,1750);
            g.drawString("珍贵宝箱收集: "+info.getPrecious_chest_number(),200,1900);
            g.drawString("华丽宝箱收集: "+info.getLuxurious_chest_number(),200,2050);
            g.drawString("奇馈宝箱收集: "+info.getMagic_chest_number(),200,2200);
            g.drawString("活跃天数: "+info.getActive_day_number(),200,2350);
            g.setFont(hywh.deriveFont(100f));
            World[] worlds=info.getWorlds();
            for (int i=0;i<worlds.length;i++){
                World world=worlds[i];
                ByteArrayInputStream imgIn=new ByteArrayInputStream(Base64.getDecoder().decode(world.getIcon()));
                BufferedImage icon=ImageIO.read(imgIn);
                g.drawImage(icon,2000,500+200*i,200,200,null);
                String percentage=String.valueOf((double)world.getExploration_percentage()/10);
                g.drawString(world.getName(),2300,500+200*i+150);
                g.drawString("探索度: "+percentage+" %",2750,500+200*i+150);
            }
            g.setPaint(Color.BLACK);
            Role[] roles=info.getRoles();
            for (int i=0;i<8;i++){
                int posX=i%2;
                int posY=(i/2)*900;
                Role role=roles[i];
                ByteArrayInputStream imgIn=new ByteArrayInputStream(Base64.getDecoder().decode(role.getAvatar_image()));
                BufferedImage avatar=ImageIO.read(imgIn);
                if (posX==0){
                    g.drawImage(avatar, 5000, posY,600,600,null);
                    g.drawString(role.getName(),4600,200+posY);
                    g.drawString("好感度: "+role.getFetter(),4600,300+posY);
                    g.drawString("LV.  "+role.getLevel(),4600,400+posY);
                } else {
                    g.drawImage(avatar, 6500, posY, 600, 600, null);
                    g.drawString(role.getName(),6100,200+posY);
                    g.drawString("好感度: "+role.getFetter(),6100,300+posY);
                    g.drawString("LV.  "+role.getLevel(),6100,400+posY);
                }
            }
            g.setPaint(Color.WHITE);
            g.setFont(hywh.deriveFont(120f));
            g.drawString("\"琪亚娜，抬起头，继续前进吧\"",100,4200);
            g.setPaint(Color.BLACK);
            g.drawString("Copyright 2021-2022 shandiankulishe@gmail.com",4000,3900);
            g.drawString("项目开源地址: https://www.github.com/youfantan/KleeBot",4000,4050);
            g.drawString("画师: @Acami https://www.pixiv.net/artworks/88814776",4000,4200);
            g.dispose();
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            ImageIO.write(image,"jpg",out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

}
