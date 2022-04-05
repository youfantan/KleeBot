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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BilibiliService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().contains("bilibili.com")||event.getMessage().serializeToMiraiCode().contains("b23.tv");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws IOException {
        String rawMessage=event.getMessage().serializeToMiraiCode();
        if (rawMessage.startsWith(new At(KleeBot.config.getBotAccount())+" bilibili")){
            String rawUrl=rawMessage.substring(rawMessage.indexOf("bilibili.com"));
            if (rawUrl.contains("BV")){
                String bvid=getBVid(rawUrl);
                if (bvid!=null){
                    String cid=getCid(bvid);
                    String aid=getAid(cid,bvid);
                    sendVideoInformation(event,getVideoInformation(aid));
                } else{
                    return false;
                }
            } else if (rawUrl.contains("av")){
                String aid= StringUtils.findDigit(rawUrl);
                if (aid!=null){
                    sendVideoInformation(event,getVideoInformation(aid));
                } else{
                    return false;
                }
            }
            return true;
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
            return true;
        }
        return true;
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
    private String getCid(String BVid) throws IOException {
        String playerList= new String(FileUtils.download(References.BILBILI_PLAYER_LIST_URL.formatted(BVid)), StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(playerList).getAsJsonObject();
        return String.valueOf(object.getAsJsonArray("data").get(0).getAsJsonObject().get("cid").getAsLong());
    }
    private String getAid(String Cid,String BVid) throws IOException {
        String webInterface=new String(FileUtils.download(References.BILBILI_WEB_INTERFACE_BVID_URL.formatted(Cid,BVid)),StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(webInterface).getAsJsonObject();
        return String.valueOf(object.getAsJsonObject("data").get("aid").getAsLong());
    }
    private BilibiliVideoInf getVideoInformation(String Aid) throws IOException {
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
            CacheFactory.storeCache(Aid,formattedInf,Timer.NO_LIMIT);
            return inf;
        }
    }
}

