package glous.kleebot.services.impl;

import glous.kleebot.features.bilibili.BilibiliAPI;
import glous.kleebot.services.builtin.BilibiliVideoInf;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import glous.kleebot.KleeBot;
import glous.kleebot.services.GroupService;
import glous.kleebot.utils.StringUtils;

import java.io.IOException;

public class BilibiliService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().contains("bilibili")||event.getMessage().serializeToMiraiCode().contains("b23.tv");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws IOException {
        String rawMessage=event.getMessage().serializeToMiraiCode();
        BilibiliAPI api=new BilibiliAPI();
        if (rawMessage.startsWith(new At(KleeBot.config.getBotAccount())+" bilibili ")){
            if (rawMessage.startsWith(new At(KleeBot.config.getBotAccount())+" bilibili shorturl ")){
                String rawUrl=rawMessage.substring(rawMessage.indexOf("bilibili.com"));
                if (rawUrl.contains("BV")){
                    String bvid=api.getBVid(rawUrl);
                    if (bvid!=null){
                        sendMessage("解析后的链接: "+api.getShortUrl("https://www.bilibili.com/video/"+bvid),event);
                        return true;
                    }
                } else if (rawUrl.contains("av")){
                    String aid= StringUtils.findDigit(rawUrl);
                    if (aid!=null){
                        sendMessage("解析后的链接: "+api.getShortUrl("https://www.bilibili.com/video/av"+aid),event);
                        return true;
                    }
                }
            } else{
                if (rawMessage.contains("bilibili.com")){
                    String rawUrl=rawMessage.substring(rawMessage.indexOf("bilibili.com"));
                    if (rawUrl.contains("BV")){
                        String bvid=api.getBVid(rawUrl);
                        if (bvid!=null){
                            String cid=api.getCid(bvid);
                            String aid=api.getAid(cid,bvid);
                            sendVideoInformation(event,api.getVideoInformation(aid));
                            return true;
                        }
                    } else if (rawUrl.contains("av")){
                        String aid= StringUtils.findDigit(rawUrl);
                        if (aid!=null){
                            sendVideoInformation(event,api.getVideoInformation(aid));
                            return true;
                        }
                    }
                } else if (rawMessage.contains("b23.tv")){
                    String rawUrl=rawMessage.substring(rawMessage.indexOf("b23.tv"));
                    if (rawUrl.contains("av")){
                        String aid=StringUtils.findDigit(rawUrl);
                        if (aid!=null){
                            sendVideoInformation(event, api.getVideoInformation(aid));
                            return true;
                        }
                    } else{
                        String b23ShortUrl=rawUrl.substring(rawUrl.indexOf("b23.tv/"+8),rawUrl.indexOf("b23.tv/"+8)+7);
                        System.out.println(b23ShortUrl);
                        String url=api.getB23RedirectUrl(b23ShortUrl);
                        if (url.startsWith("https://")){
                            String bvid=api.getBVid(url);
                            if (bvid!=null){
                                String cid=api.getCid(bvid);
                                String aid=api.getAid(cid,bvid);
                                sendVideoInformation(event,api.getVideoInformation(aid));
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } else if (rawMessage.contains("bilibili.com")){
            String rawUrl=rawMessage.substring(rawMessage.indexOf("bilibili.com"));
            executeVideo(event, api, rawUrl);
            return true;
        } else if (rawMessage.contains("b23.tv")){
            String rawUrl=rawMessage.substring(rawMessage.indexOf("b23.tv")+7);
            if (rawUrl.contains("av")){
                String aid=StringUtils.findDigit(rawUrl);
                if (aid!=null){
                    sendVideoInformation(event, api.getVideoInformation(aid));
                }
            } else{
                String b23ShortUrl=rawUrl.substring(0,7);
                String url=api.getB23RedirectUrl("https://b23.tv/"+b23ShortUrl);
                if (url.startsWith("https://")){
                    executeVideo(event, api, url);
                }
            }
        }
        return true;
    }

    private void executeVideo(GroupMessageEvent event, BilibiliAPI api, String url) throws IOException {
        if (url.contains("BV")){
            String bvid=api.getBVid(url);
            if (bvid!=null){
                String cid=api.getCid(bvid);
                String aid=api.getAid(cid,bvid);
                sendVideoInformation(event,api.getVideoInformation(aid));
            }
        } else if (url.contains("av")){
            String aid= StringUtils.findDigit(url);
            if (aid!=null){
                sendVideoInformation(event,api.getVideoInformation(aid));
            }
        }
    }

    private void sendVideoInformation(GroupMessageEvent event, BilibiliVideoInf inf){
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
}

