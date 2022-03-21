package shandiankulishe.kleebot.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.References;
import shandiankulishe.kleebot.async.Timer;
import shandiankulishe.kleebot.cache.CacheFactory;
import shandiankulishe.kleebot.services.GroupService;
import shandiankulishe.kleebot.services.builtin.BilibiliVideoInf;
import shandiankulishe.kleebot.utils.FileUtils;
import shandiankulishe.kleebot.utils.StringUtils;

import java.nio.charset.StandardCharsets;

public class BilibiliService implements GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().contains("bilibili.com");
    }

    @Override
    public void execute(GroupMessageEvent event) {
        String rawMessage=event.getMessage().serializeToMiraiCode();
        if (rawMessage.startsWith(new At(KleeBot.config.getBotAccount())+" bilibili")){
        } else if (rawMessage.contains("bilibili.com")){
            String rawUrl=rawMessage.substring(rawMessage.indexOf("bilibili.com"));
            if (rawUrl.contains("BV")){
                String bvid=getBVid(rawUrl);
                if (bvid!=null){
                    String cid=getCid(bvid);
                    String aid=getAid(cid,bvid);
                    sendVideoInformation(event,getVideoInformation(aid));
                }
            } else if (rawUrl.contains("av")){
                String aid= StringUtils.findDigit(rawUrl);
                if (aid!=null){
                    sendVideoInformation(event,getVideoInformation(aid));
                }
            }
        }
    }
    private void sendError(GroupMessageEvent event,String rawUrl){
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append(
                """
                        未识别URL: %s
                        """.formatted(rawUrl)
        );
        event.getGroup().sendMessage(builder.build());
    }
    private void sendVideoInformation(GroupMessageEvent event,BilibiliVideoInf inf){
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append("\n视频封面:");
        ExternalResource res=ExternalResource.create(inf.getCover());
        Image image=event.getSubject().uploadImage(res);
        builder.append(image);
        builder.append(
                """
                        
                        标题: %s
                        作者: %s
                        源URL: %s
                        """
        .formatted(inf.getTitle(),inf.getAuthor(),inf.getUrl()));
        event.getGroup().sendMessage(builder.build());
    }
    private String getBVid(String rawUrl){
        if (rawUrl.length()<rawUrl.indexOf("BV")+12){
            return null;
        }
        return rawUrl.substring(rawUrl.indexOf("BV"),rawUrl.indexOf("BV")+12);
    }
    private String getCid(String BVid){
        String playerList= new String(FileUtils.download(References.BILBILI_PLAYER_LIST_URL.formatted(BVid)), StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(playerList).getAsJsonObject();
        return String.valueOf(object.getAsJsonArray("data").get(0).getAsJsonObject().get("cid").getAsLong());
    }
    private String getAid(String Cid,String BVid){
        String webInterface=new String(FileUtils.download(References.BILBILI_WEB_INTERFACE_BVID_URL.formatted(Cid,BVid)),StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(webInterface).getAsJsonObject();
        return String.valueOf(object.getAsJsonObject("data").get("aid").getAsLong());
    }
    private BilibiliVideoInf getVideoInformation(String Aid){
        byte[] formattedInf;
        Gson gson=new Gson();
        if ((formattedInf=CacheFactory.getCache(Aid))!=null){
            BilibiliVideoInf inf=gson.fromJson(new String(formattedInf,StandardCharsets.UTF_8),BilibiliVideoInf.class);
            return inf;
        } else {
            String webInterface=new String(FileUtils.download(References.BILBILI_WEB_INTERFACE_AID_URL.formatted(Aid)),StandardCharsets.UTF_8);
            JsonObject object=JsonParser.parseString(webInterface).getAsJsonObject();
            JsonObject data=object.getAsJsonObject("data");
            String coverUrl=data.get("pic").getAsString();
            byte[] cover=FileUtils.download(coverUrl);
            String title=data.get("title").getAsString();
            JsonObject owner=data.get("owner").getAsJsonObject();
            String author=owner.get("name").getAsString();
            String url="https://www.bilibili.com/video/"+data.get("bvid").getAsString();
            BilibiliVideoInf inf=new BilibiliVideoInf(cover,title,author,url);
            formattedInf=gson.toJson(inf).getBytes(StandardCharsets.UTF_8);
            CacheFactory.restoreCache(Aid,formattedInf,Timer.NO_LIMIT);
            return inf;
        }
    }
}

